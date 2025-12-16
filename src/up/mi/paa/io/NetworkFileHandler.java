package up.mi.paa.io;

import up.mi.paa.model.*;
import up.mi.paa.exception.NetworkParseException;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

/**
 * Classe responsable de la lecture et de l'écriture de fichiers réseau.
 * Format du fichier :
 * - generateur(nom,capacite)
 * - maison(nom,TYPE)
 * - connexion(element1,element2)
 * 
 */
public class NetworkFileHandler {
   
    /** Pattern pour parser une ligne de générateur */
    private static Pattern GENERATOR_PATTERN = 
        Pattern.compile("^generateur\\(([a-zA-Z0-9]+),\\s*(\\d+)\\)\\.$");
    
    /** Pattern pour parser une ligne de maison */
     private static Pattern HOUSE_PATTERN =
        Pattern.compile("^maison\\(([a-zA-Z0-9]+),\\s*(BASSE|NORMAL|FORTE)\\)\\.$");
    
    /** Pattern pour parser une ligne de connexion */
    private static Pattern CONNECT_PATTERN =
        Pattern.compile("^connexion\\(([a-zA-Z0-9]+),\\s*([a-zA-Z0-9]+)\\)\\.$");

    /**
     * Lit un fichier et construit un réseau électrique.
     * 
     * @param chemin vers le fichier à lire
     * @param lambda
     * @return le réseau construit
     * @throws IOException si erreur de lecture
     * @throws NetworkParseException si erreur de parsing
     */
        public static Network readFiles(String chemin, double lambda) throws IOException, NetworkParseException {
        Path path = Paths.get(chemin);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Fichier non trouvé: " + chemin);
        }

        Network network = new Network(lambda);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            int nbLigne = 0;
            String ligne;

            Phase phase = Phase.GENERATORS;

            while ((ligne = reader.readLine()) != null) {
                nbLigne++;

                // Ignorer les lignes vides et les commentaires
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("#")) {
                    continue;
                }

                // Déterminer le type de ligne et parser
                if (GENERATOR_PATTERN.matcher(ligne).matches()) {
                    if (phase.ordinal() > Phase.GENERATORS.ordinal()) {
                        throw new NetworkParseException(
                            "Les générateurs doivent être définis avant les maisons et connexions", 
                            nbLigne, ligne);
                    }
                    parseGenerator(network, ligne, nbLigne);

                } else if (HOUSE_PATTERN.matcher(ligne).matches()) {
                    if (phase == Phase.GENERATORS) {
                        phase = Phase.HOUSES;
                    } else if (phase.ordinal() > Phase.HOUSES.ordinal()) {
                        throw new NetworkParseException(
                            "Les maisons doivent être définies avant les connexions", 
                            nbLigne, ligne);
                    }
                    parseHouse(network, ligne, nbLigne);

                } else if (CONNECT_PATTERN.matcher(ligne).matches()) {
                    if (phase.ordinal() < Phase.CONNECTIONS.ordinal()) {
                        phase = Phase.CONNECTIONS;
                    }
                    parseConnection(network, ligne, nbLigne);

                } else {
                    throw new NetworkParseException(
                        "Format de ligne invalide. Attendu : generateur(...), maison(...) ou connexion(...)", 
                        nbLigne, ligne);
                }
            }
        }

        // Validation finale
        validerReseau(network);

        return network;
    }

    /**
     * Parse une ligne de générateur
     */
    private static void parseGenerator(Network network, String ligne, int nbLigne) throws NetworkParseException {
        Matcher matcher = GENERATOR_PATTERN.matcher(ligne);
        if (!matcher.matches()) {
            throw new NetworkParseException("Format de générateur invalide", nbLigne, ligne);
        }

        String nom = matcher.group(1);
        String capaciteStr = matcher.group(2);

        try {
            int capacite = Integer.parseInt(capaciteStr);
            if (capacite <= 0) {
                throw new NetworkParseException(
                    "La capacité doit être strictement positive", nbLigne, ligne);
            }

            boolean existed = network.ajouteGenerator(nom, capacite);
            if (existed) {
                System.out.println("AVERTISSEMENT ligne " + nbLigne + ": Le générateur " + nom + " existe déjà, capacité mise à jour.");
            }
        } catch (NumberFormatException e) {
            throw new NetworkParseException(
                "Capacité invalide (doit être un entier)", nbLigne, ligne, e);
        }
    }

    /**
     * Parse une ligne de maison
     */
    private static void parseHouse(Network network, String ligne, int nbLigne) throws NetworkParseException {
        Matcher matcher = HOUSE_PATTERN.matcher(ligne);
        if (!matcher.matches()) {
            throw new NetworkParseException("Format de maison invalide", nbLigne, ligne);
        }

        String nom = matcher.group(1);
        String typeStr = matcher.group(2);

        try {
            HouseType type = HouseType.fromString(typeStr);
            boolean existed = network.ajouteHouse(nom, type);
            if (existed) {
                System.out.println("AVERTISSEMENT ligne " + nbLigne + ": La maison " + nom + " existe déjà, type mis à jour.");
            }
        } catch (IllegalArgumentException e) {
            throw new NetworkParseException(
                "Type de maison invalide: " + typeStr, nbLigne, ligne, e);
        }
    }

    /**
     * Parse une ligne de commande
     */
    private static void parseConnection(Network network, String ligne, int nbLigne) throws NetworkParseException {
        Matcher matcher = CONNECT_PATTERN.matcher(ligne);
        if (!matcher.matches()) {
            throw new NetworkParseException("Format de connexion invalide", nbLigne, ligne);
        }

        String elem1 = matcher.group(1);
        String elem2 = matcher.group(2);

        // Déterminer qui est la maison et qui est le générateur
        String h = null;
        String g = null;

        if (network.houseExist(elem1) && network.generatorExist(elem2)) {
            h = elem1;
            g = elem2;
        } else if (network.generatorExist(elem1) && network.houseExist(elem2)) {
            h = elem2;
            g = elem1;
        } else {
            String message = "Connexion invalide: ";
            if (!network.houseExist(elem1) && !network.generatorExist(elem1)) {
                message += elem1 + " n'existe pas";
            }
            if (!network.houseExist(elem2) && !network.generatorExist(elem2)) {
                if (!message.endsWith("existe pas")) {
                    message += " et ";
                }
                message += elem2 + " n'existe pas";
            }
            throw new NetworkParseException(message, nbLigne, ligne);
        }

        // Vérifier si la maison est déjà connectée
        if (network.isConnect(h)) {
            String oldGen = network.getGeneratorConnect(h);
            System.out.println("AVERTISSEMENT ligne " + nbLigne +
                ": La maison " + h + " était déjà connectée à " + oldGen +
                ", connexion remplacée par " + g);
        }

        network.connecter(h, g);
    }

    /**
     * Valide le réseau après parsing
     */
    private static void validerReseau(Network network) throws NetworkParseException {
        // Vérifier qu'il y a au moins une maison et un générateur
        if (network.getHouses().isEmpty()) {
            throw new NetworkParseException("Le réseau doit contenir au moins une maison", 0, "");
        }
        if (network.getGenerators().isEmpty()) {
            throw new NetworkParseException("Le réseau doit contenir au moins un générateur", 0, "");
        }

        // Vérifier que toutes les maisons sont connectées
        if (!network.estValide()) {
            StringBuilder sb = new StringBuilder("Maisons non connectées: ");
            network.getHousesNoConnect().forEach(m -> sb.append(m).append(", "));
            throw new NetworkParseException(sb.toString(), 0, "");
        }

        // Vérifier que la capacité est suffisante
        if (!network.capaciteSuffisante()) {
            throw new NetworkParseException(
                String.format("Capacité insuffisante: consommation=%d kW, capacité=%d kW",
                network.getConsoTotale(), network.getCapaciteTotale()),
                0, "");
        }
    }

    /**
     * Sauvegarde un réseau dans un fichier
     * 
     * @param le réseau à sauvegarder
     * @param chemin du fichier de sortie
     * @throws IOException si erreur d'écriture
     */
    public static void saveFiles(Network network, String chemin) throws IOException {
        Path path = Paths.get(chemin);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Ecrire les générateurs
            for (Generator gen : network.getGenerators().values()) {
                writer.write(String.format("generateur(%s,%d).%n", gen.getNom(), gen.getKw()));
            }

            writer.newLine();

            // Ecrire les maisons
            for (House house : network.getHouses().values()) {
                writer.write(String.format("maison(%s,%s).%n", house.getNom(), house.getType().name()));
            }

            writer.newLine();

            // Ecrire les connexions
            for (var entry : network.getLink().entrySet()) {
                writer.write(String.format("connexion(%s,%s).%n", entry.getValue(), entry.getKey()));
            }
        }
    }

    /**
     * Enumération des phases de parsing
     */
    private enum Phase {
        GENERATORS, HOUSES, CONNECTIONS
    }
}
