package up.mi.paa.model;

import java.util.*;

/**
 * Représente un réseau électrique complet avec des générateurs, des maisons
 * et les connexions entre eux. Cette classe gère également le calcul du coût
 * du réseau selon les critères de dispersion et de surcharge.
 */
public class Network {
    /** Map des maisons du réseau (clé: nom, valeur: House) */
    private Map<String, House> houses;

    /** Map des générateurs du réseau (clé: nom, valeur: Generator) */
    private Map<String, Generator> generators;

    /** Map des connexions (clé: nom de la maison, valeur: nom du générateur) */
    private Map<String, String> connect;

    /** Paramètre de sévérité de pénalisation des surcharges */
    private double lambda;

    /**
     * Constructeur par défaut avec lambda = 10
     */
    public Network() {
        this(10.0);
    }

    /**
     * Constructeur avec param lambda personnalisé.
     * 
     * @return
     */
    public Network(double lambda) {
        if (lambda <= 0) {
            throw new IllegalArgumentException("Lambda doit être strictement positif");
        }

        this.houses = new HashMap<>();
        this.generators = new HashMap<>();
        this.connect = new HashMap<>();
        this.lambda = lambda;
    }

    /**
     * Accesseur de la map des maisons.
     * 
     * @return copie non modifiable de la map des maisons
     */
    public Map<String, House> getHouses() {
        return Collections.unmodifiableMap(houses);
    }

    /**
     * Accesseur de la map des générateurs. 
     * 
     * @return copie non modifiable de la map des générateurs
     */
    public Map<String, Generator> getGenerators() {
        return Collections.unmodifiableMap(generators);
    }

    /**
     * Acesseur de la map des connections
     * 
     * @return une copie non modifiable de la map des connections
     */
    public Map<String, String> getLink() {
        return Collections.unmodifiableMap(connect);
    }

    /**
     * Accesseur du paramètre lambda
     * 
     * @return la valeur de lambda
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Setter du paramètre lambda
     * @param nouvelle valeur de lambda
     * @throws IllegalArgumentException si lambda <= 0
     */
    public void setLambda(double lambda) {
        if (lambda <= 0) {
            throw new IllegalArgumentException("Lambda doit être strictement positif");
        }
        this.lambda = lambda;
    }

    /**
     * Ajout ou mise à jour d'une maison dans le réseai
     * 
     * @param nom de la maison
     * @param type de consommation
     * @return true si la maison a été mise à jour, false si elle a été créée
     */
    public boolean ajouteHouse(String nom, HouseType type) {
        boolean existed = houses.containsKey(nom);
        houses.put(nom, new House(nom, type));
        return existed;
    }

    /**
     * Vérifie si une maison existe.
     * 
     * @param nom de la maison
     * @return true si la maison existe
     */
    public boolean houseExist(String nom) {
        return houses.containsKey(nom);
    }

    /**
     * Retourne la maison par son nom.
     * 
     * @param nom de la maison
     * @return la maison ou null si elle n'existe pas
     */
    public House getHouse(String nom) {
        return houses.get(nom);
    }


    /**
     * Ajout ou mise à jour d'un générateur dans le réseau
     * 
     * @param nom du générateur
     * @param capacité max en kW
     * @return true si le générateur a été mis à jour
     */
    public boolean ajouteGenerator(String nom, int kw) {
        boolean existed = generators.containsKey(nom);
        generators.put(nom, new Generator(nom, kw));
        return existed;
    }

    /**
     * Vérifie si un générateur existe
     * @param nom du générateur
     * @return true si générateur existe
     */
    public boolean generatorExist(String nom) {
        return generators.containsKey(nom);
    }

    /**
     * Retourne un générateur par son nom
     * @param nom du générateur
     * @return le générateur ou null s'il n'existe pas
     */
    public Generator getGenerator(String nom) {
        return generators.get(nom);
    }

    /**
     * Connecte une maison à un générateur
     * @param nom de la maison
     * @param nom du générateur
     * @return true si la connexion a été créée, false si elle existait déjà
     * @throws IllegalArgumentException si la maison ou le générateur n'existe pas
     */
    public boolean connecter(String h, String g) {
        if (!houseExist(h)) {
            throw new IllegalArgumentException("La maison " + h + " n'existe pas");
        }
        if (!generatorExist(g)) {
            throw new IllegalArgumentException("Le générateur " + g + " n'existe pas");
        }

        String oldConnexion = connect.get(h);
        connect.put(h, g);

        return oldConnexion == null || !oldConnexion.equals(g);
    }

    /**
     * Déconnecte une maison de son générateur.
     * 
     * @param nom de la maison
     * @return true si la déconnexion a été effectuée, false si aucune connexion n'existait
     */
    public boolean deconnecter(String h) {
        return connect.remove(h) != null;
    }

    /**
     * Retourne le générateur connecté à une maison
     * 
     * @param nom de la maison
     * @return le nom du générateur ou null si pas de connexion
     */
    public String getGeneratorConnect(String h) {
        return connect.get(h);
    }

    /**
     * Vérifie si une maison est connectée.
     * 
     * @param nom de la maison
     * @return true si la maison est connecté
     */
    public boolean isConnect(String h) {
        return connect.containsKey(h);
    }

    /**
     * Modifie la connexion d'une maison.
     * 
     * @param nom de la maison
     * @param nom de l'ancien générateur
     * @param nom du nouveau générateur
     * @return true si la modification a réussi
     * @throws IllegalArgumentException si la connexion initiale pas ou ne correspond pas
     */
    public boolean modifierConnexion(String h, String oldGenerator, String newGenerator) {
        String connexionActuelle = connect.get(h);

        if (connexionActuelle == null) {
            throw new IllegalArgumentException("La maison " + h + " n'a pas de connexion existante");
        }

        if (!connexionActuelle.equals(oldGenerator)) {
            throw new IllegalArgumentException("La maison " + h + " n'est pas connectée à " + oldGenerator);
        }

        return connecter(h, newGenerator);
    }

    /**
     * Calcule de la consommation totale de toutes les maisons.
     * 
     * @return la consommation totale en kW
     */
    public int getConsoTotale() {
        return houses.values().stream().mapToInt(House::getConsommation).sum();
    }

    /**
     * Calcule de la capacité totale de tous les générateurs
     * 
     * @return la capacité totale en kW
     */
    public int getCapaciteTotale() {
        return generators.values().stream().mapToInt(Generator::getKw).sum();
    }

    /**
     * Calcule la charge actuelle de chaque générateur.
     * 
     * @return une map (nom du générateur -> charge en kW)
     */
    public Map<String, Integer> getCharges() {
        Map<String, Integer> charges = new HashMap<>();

        // Initialiser toutes les charges à 0
        for (String nomGen : generators.keySet()) {
            charges.put(nomGen, 0);
        }

        // Calculer les charges
        for (Map.Entry<String, String> connexion : connect.entrySet()) {
            String nomHouse = connexion.getKey();
            String nomGen = connexion.getValue();

            House h = houses.get(nomHouse);
            if (h != null) {
                charges.merge(nomGen, h.getConsommation(), Integer::sum);
            }
        }

        return charges;
    }

    /**
     * Calcule du taux d'utilisation de chaque générateur
     * 
     * @return une map (nom du générateur -> taux d'utilisation)
     */
    public Map<String, Double> getTauxUtilisation() {
        Map<String, Integer> charges = getCharges();
        Map<String, Double> taux = new HashMap<>();

        for (Map.Entry<String, Generator> entry : generators.entrySet()) {
            String nom = entry.getKey();
            Generator gen = entry.getValue();
            int charge = charges.getOrDefault(nom, 0);
            double tauxUtil = (double) charge / gen.getKw();
            taux.put(nom, tauxUtil);
        }

        return taux;
    }

    /**
     * Calcule de la dispersion des taux d'utilisation
     * 
     * @return la valeur de Disp(s)
     */
    public double calculerDisp() {
        Map<String, Double> taux = getTauxUtilisation();

        if (taux.isEmpty()) {
            return 0.0;
        }

        // Calculer la moyenne
        double moy = taux.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return taux.values().stream().mapToDouble(t -> Math.abs(t - moy)).sum();
    }

    /**
     * Calcule de la surcharge totale du réseau
     * 
     * @return la valeur de Surcharge(S)
     */
    public double calculerSurcharge() {
        Map<String, Integer> charges = getCharges();
        double surcharge = 0.0;

        for (Map.Entry<String, Generator> entry : generators.entrySet()) {
            String nom = entry.getKey();
            Generator gen = entry.getValue();
            int charge = charges.getOrDefault(nom, 0);

            if (charge > gen.getKw()) {
                double depassement = (double) (charge - gen.getKw()) / gen.getKw();
                surcharge += depassement;
            }
        }

        return surcharge;
    }

    /**
     * Calcule du coût total du réseau
     * 
     * @return la valeur de Cout(S) = Disp(S) + lambda x Surcharge(S)
     */
    public double calculerCout() {
        double disp = calculerDisp();
        double surcharge = calculerSurcharge();
        return disp + lambda * surcharge;
    }

    /**
     * Vérifie si le réseau est valide (toutes les maisons connectées)
     * 
     * @return true si le réseau est valide
     */
    public boolean estValide() {
        return houses.keySet().stream().allMatch(connect::containsKey);
    }

    /**
     * Retourne la liste des maisons non connectées.
     * 
     * @return liste des noms de maisons non connectées
     */
    public List<String> getHousesNoConnect() {
        List<String> noConnect = new ArrayList<>();
        for (String house : houses.keySet()) {
            if (!connect.containsKey(house)) {
                noConnect.add(house);
            }
        }
        return noConnect;
    }

    /**
     * Vérifie si la capacité totale est suffisante.
     * 
     * @return true si capacité >= consommation
     */
    public boolean capaciteSuffisante() {
        return getCapaciteTotale() >= getConsoTotale();
    }

    public Network copie() {
        Network copie = new Network(this.lambda);

        // Copier les maisons
        for (Map.Entry<String, House> entry : houses.entrySet()) {
            House h = entry.getValue();
            copie.ajouteHouse(h.getNom(), h.getType());
        }

        // Copier les générateurs
        for (Map.Entry<String, Generator> entry : generators.entrySet()) {
            Generator g = entry.getValue();
            copie.ajouteGenerator(g.getNom(), g.getKw());
        }

        // Copier les connexions
        for (Map.Entry<String, String> entry : connect.entrySet()) {
            copie.connecter(entry.getKey(), entry.getValue());
        }

        return copie;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESEAU ELECTRIQUE ===\n");
        sb.append("Maisons: ").append(houses.size()).append("\n");
        sb.append("Générateurs: ").append(generators.size()).append("\n");
        sb.append("Connexions: ").append(connect.size()).append("\n");
        sb.append("Consommation totale: ").append(getConsoTotale()).append(" kW\n");
        sb.append("Capacité totale: ").append(getCapaciteTotale()).append(" kW\n");
        return sb.toString();
    }
}
