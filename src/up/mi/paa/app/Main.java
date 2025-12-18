package up.mi.paa.app;

import up.mi.paa.cli.Menu;
import up.mi.paa.exception.NetworkParseException;
import up.mi.paa.io.NetworkFileHandler;
import up.mi.paa.model.Network;

import java.io.IOException;

/**
 * Classe principale du programme de gestion de réseau électrique
 * 
 * Usage :
 *  java up.mi.paa.app.Main                 // Mode manuel
 *  java up.mi.paa.app.Main fichier.txt 10 // Mode fichier avec lambda=10
 */
public class Main {

    /**
     * Point d'entrée du programme
     * 
     * @param Arguments de la ligne de commande :
     *             args[0] : (optionnel) chemin vers le fichier de réseau
     *             args[1] : (optionnel, defaut=10) valeur de lambda
     */
    public static void main(String[] args) {
        afficheBanniere();

        try {
            if (args.length == 0) {
                // Mode manuel
                lancerModeManuel();
            } else {
                // Mode fichier
                lancerModeFichier(args);
            }
        } catch (Exception e) {
            System.err.println("ERREUR FATALE: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lancer le programme en mode manuel (construction interactive)
     */
    private static void lancerModeManuel() {
        System.out.println(" MODE MANUEL - Construction interactive du réseau");
        System.out.println("=".repeat(60));
        
        Network network = new Network();
        Menu menu = new Menu(network);
        
        try {
            menu.menuConstruction();
        } finally {
            menu.close();
        }
    }

    /**
     * Lancer le programme en mode fichier (chargement et optimisation)
     * 
     * @param arguments: [fichier, lambda (optionnel)]
     */
    private static void lancerModeFichier(String[] args) {
        System.out.println(" MODE FICHIER - Chargement et optimisation");
        System.out.println("=".repeat(60));
        
        // Parser les arguments
        String nomFichier = args[0];
        double lambda = 10.0; // Valeur par défaut
        
        if (args.length >= 2) {
            try {
                lambda = Double.parseDouble(args[1]);
                if (lambda <= 0) {
                    System.err.println("  Lambda doit être > 0, utilisation de la valeur par défaut (10)");
                    lambda = 10.0;
                }
            } catch (NumberFormatException e) {
                System.err.println("  Lambda invalide, utilisation de la valeur par défaut (10)");
            }
        }
        
        System.out.println("Fichier: " + nomFichier);
        System.out.println("Lambda:  " + lambda);
        System.out.println();
        
        // Charger le réseau
        Network network;
        try {
            System.out.println(" Chargement du fichier...");
            network = NetworkFileHandler.readFiles(nomFichier, lambda);
            System.out.println(" Fichier chargé avec succès!");
            
            // Afficher les statistiques
            System.out.println("\n Statistiques du réseau:");
            System.out.println("  - Maisons:      " + network.getHouses().size());
            System.out.println("  - Générateurs:  " + network.getGenerators().size());
            System.out.println("  - Connexions:   " + network.getLink().size());
            System.out.println("  - Consommation: " + network.getConsoTotale() + " kW");
            System.out.println("  - Capacité:     " + network.getCapaciteTotale() + " kW");
            System.out.printf("  - Coût initial: %.4f%n", network.calculerCout());
            
        } catch (NetworkParseException e) {
            System.err.println("\n ERREUR DE PARSING:");
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        } catch (IOException e) {
            System.err.println("\n ERREUR DE LECTURE:");
            System.err.println(e.getMessage());
            System.exit(1);
            return;
    }

    // Lancer le menu
    Menu menu = new Menu(network);
        try {
            menu.menuFichier();
        } finally {
            menu.close();
        }
    }

    /**
     * Affiche la bannière du programme
     */
    private static void afficheBanniere() {
        System.out.println();
        System.out.println("╔" + "═".repeat(58) + "╗");
        System.out.println("║" + centrer("GESTION DE RÉSEAU ÉLECTRIQUE", 58) + "║");
        System.out.println("║" + centrer("Projet PAA ", 58) + "║");
        System.out.println("║" + centrer("Université Paris-Cité", 58) + "║");
        System.out.println("╚" + "═".repeat(58) + "╝");
        System.out.println();
    }

    /**
     * Centre un texte dans une largeur donnée
     */
    private static String centrer(String texte, int largeur) {
        int espacesTotal = largeur - texte.length();
        int espacesGauche = espacesTotal / 2;
        int espacesDroite = espacesTotal - espacesGauche;
        return " ".repeat(espacesGauche) + texte + " ".repeat(espacesDroite);
    }
}