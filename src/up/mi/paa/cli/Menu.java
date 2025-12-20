package up.mi.paa.cli;

import up.mi.paa.model.*;
import up.mi.paa.algo.*;
import up.mi.paa.io.NetworkFileHandler;

import java.util.*;

/**
 * Gère l'interface en ligne de commande pour intéragir avec le réseau électrique.
 * Cette classe sépare la logique d'affichage de la logie métier.
 */
public class Menu {
    
    private Scanner scanner;
    private Network network;

    /**
     * Constructeur
     * 
     * @param le réseau à gérer
     */
    public Menu(Network network) {
        this.scanner = new Scanner(System.in);
        this.network = network;
    }

    /**
     * Lance le menu principal de construction manuelle.
     */
    public void menuConstruction() {
        boolean fini = false;

        while (!fini) {
            afficherMenuPrincipal();

            try {
                int choix = lireChoix(1, 5);

                switch (choix) {
                    case 1:
                        addGen();
                        break;
                    case 2:
                        AddHouse();
                        break;
                    case 3:
                        addConnexion();
                        break;
                    case 4:
                        suppConnexion();
                        break;
                    case 5:
                        if (validerReseau()) {
                            System.out.println("\n Réseau conforme !");
                            menuReseau();
                            fini = true;
                        }
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
                scanner.nextLine(); // vider le buffer
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    /**
     * Lance le menu de gestion du réseau
     */
    public void menuReseau() {
        boolean fini = false;

        while (!fini) {
            afficherMenuReseau();

            try {
                int choix = lireChoix(1, 4);

                switch (choix) {
                    case 1:
                        calculerCout();
                        break;
                    case 2:
                        modifierConnexion();
                        break;
                    case 3:
                        afficherReseau();
                        break;
                    case 4:
                        System.out.println("Au revoir !");
                        fini = true;
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
                scanner.nextLine(); // vider le buffer
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    /**
     * Menu pour le mode fichier
     */
    public void menuFichier() {
        boolean fini = false;

        while (!fini) {
            afficherMenuFichier();

            try {
                int choix = lireChoix(1, 3);

                switch (choix) {
                    case 1:
                        resolutionAuto();
                        break;
                    case 2:
                        sauvegarderSolution();
                        break;
                    case 3:
                        System.out.println("Au revoir !");
                        fini = true;
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
                scanner.nextLine(); // vider le buffer
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MENU PRINCIPAL - CONSTRUCTION");
        System.out.println("=".repeat(50));
        System.out.println("1) Ajouter un générateur");
        System.out.println("2) Ajouter une maison");
        System.out.println("3) Ajouter une connexion");
        System.out.println("4) Supprimer une connexion");
        System.out.println("5) Valider le réseau");
        System.out.println("=".repeat(50));
        System.out.print("Votre choix: ");
    }

    private void afficherMenuReseau() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MENU RESEAU - GESTION");
        System.out.println("=".repeat(50));
        System.out.println("1) Calculer le coût du réseau");
        System.out.println("2) Modifier une connexion");
        System.out.println("3) Afficher le réseau");
        System.out.println("4) Quitter");
        System.out.println("=".repeat(50));
        System.out.print("Votre choix: ");
    }

    private void afficherMenuFichier() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MENU FICHIER - OPTIMISATION");
        System.out.println("=".repeat(50));
        System.out.println("1) Résolution automatique");
        System.out.println("2) Sauvegarder la solution");
        System.out.println("3) Quitter");
        System.out.println("=".repeat(50));
        System.out.print("Votre choix: ");
    }

    private void addGen() {
        System.out.print("\nNom et capacité (ex: G1 60): ");
        String ligne = scanner.nextLine().trim();
        String[] parts = ligne.split("\\s+");

        if (parts.length != 2) {
            System.out.println("Format incorrect! Utilisez: NOM CAPACITE");
            return;
        }

        try {
            String nom = parts[0];
            int capacite = Integer.parseInt(parts[1]);

            boolean existed = network.ajouteGenerator(nom, capacite);
            if (existed) {
                System.out.println("Générateur " + nom + " mis à jour: " + capacite + " kW");
            } else {
                System.out.println("Générateur ajouté: " + nom + " (" + capacite + " kW)");
            }
        } catch (NumberFormatException e) {
            System.out.println("La capacité doit être un nombre entier!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void AddHouse() {
        System.out.print("\nNom et type (ex: M1 NORMAL): ");
        String ligne = scanner.nextLine().trim();
        String[] parts = ligne.split("\\s+");

        if (parts.length != 2) {
            System.out.println("Format incorrect! Utilisez: NOM TYPE");
            return;
        }

        try {
            String nom = parts[0];
            HouseType type = HouseType.fromString(parts[1]);

            boolean existed = network.ajouteHouse(nom, type);
            if (existed) {
                System.out.println("Maison " + nom + " mise à jour: " + type);
            } else {
                System.out.println("Maison ajoutée: " + nom + " (" + type + ", " + type.getKw() + " kW)");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void addConnexion() {
        System.out.print("\nConnexion (ex: M1 G1 ou G1 M1): ");
        String ligne = scanner.nextLine().trim();
        String[] parts = ligne.split("\\s+");

        if (parts.length != 2) {
            System.out.println("Format incorrect! Utilisez: ELEMENT1 ELEMENT2");
            return;
        }

        String elem1 = parts[0];
        String elem2 = parts[1];

        try {
            // Déterminer qui est la maison et qui est le générateur
            if (network.houseExist(elem1) && network.generatorExist(elem2)) {
                network.connecter(elem1, elem2);
                System.out.println("Connexion ajoutée: " + elem1 + " -> " + elem2);
            } else if (network.generatorExist(elem1) && network.houseExist(elem2)) {
                network.connecter(elem2, elem1);
                System.out.println("Connexion ajoutée: " + elem2 + " -> " + elem1);
            } else {
                System.out.println("Erreur: un des éléments n'existe pas! ");
                if (!network.houseExist(elem1) && !network.generatorExist(elem1)) {
                    System.out.println("  " + elem1 + " n'existe pas.");
                }
                if (!network.houseExist(elem2) && !network.generatorExist(elem2)) {
                    System.out.println("  " + elem2 + " n'existe pas.");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void suppConnexion() {
        System.out.print("\nConnexion à supprimer (ex: M1 G1): ");
        String ligne = scanner.nextLine().trim();
        String[] parts = ligne.split("\\s+");

        if (parts.length != 2) {
            System.out.println("Format incorrect!");
            return;
        }

        String elem1 = parts[0];
        String elem2 = parts[1];

        //Déterminer qui est la maison
        String h = null;
        String g = null;

        if (network.houseExist(elem1)) {
            h = elem1;
            g = elem2;
        } else if (network.houseExist(elem2)) {
            h = elem2;
            g = elem1;
        }

        if (h == null) {
            System.out.println("Aucune maison trouvée dans la connexion!");
            return;
        }

        String genConnect = network.getGeneratorConnect(h);
        if (genConnect != null && genConnect.equals(g)) {
            network.deconnecter(h);
            System.out.println("Connexion supprimée: " + h + " - " + g);
        } else {
            System.out.println("Cette connexion n'existe pas!");
        }
    }

    private boolean validerReseau() {
        List<String> prbl = network.getHousesNoConnect();

        if (!prbl.isEmpty()) {
            System.out.println("Réseau non conforme! Maisons non connectées:");
            for (String maison : prbl) {
                System.out.println("   - " + maison);
            }
            return false;
        }

        return true;
    }

    private void calculerCout() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         CALCUL DU COÛT");
        System.out.println("=".repeat(50));
        
        double disp = network.calculerDisp();
        double surcharge = network.calculerSurcharge();
        double cout = network.calculerCout();
        
        System.out.printf("Dispersion:     %.4f%n", disp);
        System.out.printf("Surcharge:      %.4f%n", surcharge);
        System.out.printf("Lambda:         %.1f%n", network.getLambda());
        System.out.println("-".repeat(50));
        System.out.printf("COÛT TOTAL:     %.4f%n", cout);
        System.out.println("=".repeat(50));
        
        // Afficher détails par générateur
        afficherDetailsGen();
    }

    private void afficherDetailsGen() {
        System.out.println("\nDétails par générateur:");
        Map<String, Integer> charges = network.getCharges();
        Map<String, Double> taux = network.getTauxUtilisation();
        
        for (Map.Entry<String, Generator> entry : network.getGenerators().entrySet()) {
            String nom = entry.getKey();
            Generator gen = entry.getValue();
            int charge = charges.get(nom);
            double tauxUtil = taux.get(nom);
            
            String status = charge > gen.getKw() ? "SURCHARGE" : "Validé";
            System.out.printf("  %s %s: %d/%d kW (%.1f%%)%n", 
                status, nom, charge, gen.getKw(), tauxUtil * 100);
        }
    }

    private void modifierConnexion() {
        System.out.print("\nConnexion à modifier (ex: M1 G1): ");
        String ligne1 = scanner.nextLine().trim();
        String[] parts1 = ligne1.split("\\s+");
        
        if (parts1.length != 2) {
            System.out.println("Format incorrect!");
            return;
        }
        
        System.out.print("Nouvelle connexion (ex: M1 G2): ");
        String ligne2 = scanner.nextLine().trim();
        String[] parts2 = ligne2.split("\\s+");
        
        if (parts2.length != 2) {
            System.out.println("Format incorrect!");
            return;
        }
        
        try {
            // Identifier maison et générateurs
            String maison = null;
            String ancienGen = null;
            String nouveauGen = null;
            
            if (network.houseExist(parts1[0])) {
                maison = parts1[0];
                ancienGen = parts1[1];
            } else if (network.houseExist(parts1[1])) {
                maison = parts1[1];
                ancienGen = parts1[0];
            }

            if (network.houseExist(parts2[0]) && parts2[0].equals(maison)) {
                nouveauGen = parts2[1];
            } else if (network.houseExist(parts2[1]) && parts2[1].equals(maison)) {
                nouveauGen = parts2[0];
            }
            
            if (maison == null || ancienGen == null || nouveauGen == null) {
                System.out.println("Erreur dans l'identification des éléments!");
                return;
            }
            
            network.modifierConnexion(maison, ancienGen, nouveauGen);
            System.out.println("Connexion modifiée: " + maison + " : " + 
                ancienGen + " -> " + nouveauGen);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private void afficherReseau() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         RÉSEAU ÉLECTRIQUE ACTUEL");
        System.out.println("=".repeat(50));
        
        System.out.println("\n STATISTIQUES:");
        System.out.println("  Maisons:            " + network.getHouses().size());
        System.out.println("  Générateurs:        " + network.getGenerators().size());
        System.out.println("  Connexions:         " + network.getLink().size());
        System.out.println("  Consommation totale: " + network.getConsoTotale() + " kW");
        System.out.println("  Capacité totale:     " + network.getCapaciteTotale() + " kW");
        
        System.out.println("\n MAISONS:");
        for (House h : network.getHouses().values()) {
            String gen = network.getGeneratorConnect(h.getNom());
            String connexion = gen != null ? " -> " + gen : " (non connectée)";
            System.out.println("  " + h + connexion);
        }
        
        System.out.println("\n GÉNÉRATEURS:");
        Map<String, Integer> charges = network.getCharges();
        for (Generator g : network.getGenerators().values()) {
            int charge = charges.get(g.getNom());
            System.out.printf("  %s - Charge: %d/%d kW%n", 
                g, charge, g.getKw());
        }
        
        System.out.println("=".repeat(50));
    }

    private void resolutionAuto() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         RÉSOLUTION AUTOMATIQUE");
        System.out.println("=".repeat(50));
        
        System.out.println("\nChoisissez l'algo:");
        System.out.println("1) Algo naïf (rapide)");
        System.out.println("2) Algo glouton (meilleur)");
        System.out.print("Votre choix: ");
        
        int choixAlgo = lireChoix(1, 2);
        
        System.out.print("Nombre d'itérations (ex: 1000): ");
        int iter = lireEntier();
        
        System.out.println("\nCoût initial: " + String.format("%.4f", network.calculerCout()));
        System.out.println("Optimisation en cours...");
        
        SolverAlgorithm solver = choixAlgo == 1 ? new NaiveSolver() : new GreedySolver();
        
        long debut = System.currentTimeMillis();
        double coutFinal = solver.resoudre(network, iter);
        long duree = System.currentTimeMillis() - debut;
        
        System.out.println("\n Optimisation terminée!");
        System.out.println("Algorithme: " + solver.getNom());
        System.out.println("Durée: " + duree + " ms");
        System.out.printf("Coût final: %.4f%n", coutFinal);
        System.out.println("=".repeat(50));
    }

    private void sauvegarderSolution() {
        System.out.print("\nNom du fichier de sortie (ex: solution.txt): ");
        String nomFichier = scanner.nextLine().trim();
        
        try {
            NetworkFileHandler.saveFiles(network, nomFichier);
            System.out.println(" Solution sauvegardée dans: " + nomFichier);
        } catch (Exception e) {
            System.out.println(" Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private int lireChoix(int min, int max) {
        while (true) {
            try {
                int choix = Integer.parseInt(scanner.nextLine().trim());
                if (choix >= min && choix <= max) {
                    return choix;
                }
                System.out.print(" Choix invalide! Entrez un nombre entre " + min + " et " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print(" Veuillez entrer un nombre valide: ");
            }
        }
    }

    private int lireEntier() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print(" Veuillez entrer un nombre entier valide: ");
            }
        }
    }

    /**
     * Ferme le scanner
     */
    public void close() {
        scanner.close();
    }
}
