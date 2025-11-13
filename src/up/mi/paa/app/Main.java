package up.mi.paa.app;

import up.mi.paa.model.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Network network = new Network();

        boolean fini = false;

        while (!fini) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion entre une maison et un générateur existants");
            System.out.println("4) Supprimer une connexion existante entre une maison et un générateur");
            System.out.println("5) Fin (passer au menu réseau)");
            System.out.print("Choix : ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1":
                    System.out.print("Nom du générateur et capacité (ex: G1 60) : ");
                    String[] genInfo = sc.nextLine().trim().split(" ");
                    if (genInfo.length == 2) {
                        String nomG = genInfo[0];
                        int kw = Integer.parseInt(genInfo[1]);
                        network.ajouteGenerator(nomG, kw);
                        System.out.println("Générateur ajouté : " + nomG + " (" + kw + " kW)");
                    } else {
                        System.out.println("Format incorrect !");
                    }
                    break;

                case "2":
                    System.out.print("Nom et type (ex: M1 NORMAL) : ");
                    String[] houseInfo = sc.nextLine().trim().split(" ");
                    if (houseInfo.length == 2) {
                        try {
                            String nomM = houseInfo[0];
                            HouseType type = HouseType.valueOf(houseInfo[1].substring(0, 1).toUpperCase() + houseInfo[1].substring(1).toLowerCase());
                            network.ajouteHouse(nomM, type);
                            System.out.println("Maison ajoutée : " + nomM + " (" + type + ")");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Type de maison invalide. Utilisez BASSE, NORMAL ou FORTE.");
                        }
                    } else {
                        System.out.println("Format incorrect !");
                    }
                    break;

                case "3":
                    System.out.print("Connexion (ex: M1 G1 ou G1 M1) : ");
                    String[] connect = sc.nextLine().trim().split(" ");
                    if (connect.length == 2) {
                        String a = connect[0], b = connect[1];
                        if (network.getHouses().containsKey(a) && network.getGenerators().containsKey(b)) {
                            network.connecter(a, b);
                            System.out.println("Connexion ajoutée : " + a + " → " + b);
                        } else if (network.getGenerators().containsKey(a) && network.getHouses().containsKey(b)) {
                            network.connecter(b, a);
                            System.out.println("Connexion ajoutée : " + b + " → " + a);
                        } else {
                            System.out.println("Générateur ou maison introuvable !");
                        }
                    } else {
                        System.out.println("Format incorrect !");
                    }
                    break;

                case "4":
                    System.out.print("Connexion à supprimer (ex: M1 G1 ou G1 M1) : ");
                    String[] disconnect = sc.nextLine().trim().split(" ");
                    if (disconnect.length == 2) {
                        String a = disconnect[0], b = disconnect[1];
                        if (network.getLink().containsKey(a) && network.getLink().get(a).equals(b)) {
                            network.deconnecter(a);
                            System.out.println("Connexion supprimée : " + a + " - " + b);
                        } else if (network.getLink().containsKey(b) && network.getLink().get(b).equals(a)) {
                            network.deconnecter(b);
                            System.out.println("Connexion supprimée : " + b + " - " + a);
                        } else {
                            System.out.println("Connexion inexistante !");
                        }
                    } else {
                        System.out.println("Format incorrect !");
                    }
                    break;

                case "5":
                    boolean ok = true;
                    for (String m : network.getHouses().keySet()) {
                        if (!network.getLink().containsKey(m)) {
                            System.out.println("La maison " + m + " n’est pas connectée !");
                            ok = false;
                        }
                    }
                    if (ok) {
                        System.out.println("Réseau conforme !");
                        menuReseau(sc, network);
                        fini = true;
                    } else {
                        System.out.println("Corrigez les problèmes avant de continuer.");
                    }
                    break;

                default:
                    System.out.println("Choix invalide !");
            }
        }
    }

    private static void menuReseau(Scanner sc, Network network) {
        boolean fini = false;
        while (!fini) {
            System.out.println("\n=== MENU RÉSEAU ===");
            System.out.println("1) Calculer le coût du réseau électrique actuel");
            System.out.println("2) Modifier une connexion");
            System.out.println("3) Afficher le réseau");
            System.out.println("4) Fin");
            System.out.print("Choix : ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1":
                    int lambda = 10;
                    double disp = network.calculerDisp();
                    double surcharge = network.calculerSurcharge();
                    double cout = network.cout(lambda);

                    System.out.printf(java.util.Locale.ROOT, 
                                    "Disp(S)=%.4f, Surcharge(S)=%.4f, Cout(S)=%.4f (lambda=%d)%n",
                                    disp, surcharge, cout, lambda
                    );
                    break;

                case "2":
                    System.out.print("Connexion à modifier (ex: M1 G1) : ");
                    String[] modif = sc.nextLine().trim().split(" ");
                    if (modif.length == 2) {
                        String m = modif[0], g = modif[1];
                        System.out.print("Nouvelle connexion (ex: M1 G2) : ");
                        String[] nouv = sc.nextLine().trim().split(" ");
                        if (nouv.length == 2) {
                            network.deconnecter(m);
                            network.connecter(nouv[0], nouv[1]);
                            System.out.println("Connexion modifiée !");
                        }
                    }
                    break;

                case "3":
                    afficherReseau(network);
                    break;

                case "4":
                    System.out.println("Fin du programme.");
                    fini = true;
                    break;

                default:
                    System.out.println("Choix invalide !");
            }
        }
    }

    private static void afficherReseau(Network network) {
        System.out.println("\n=== RÉSEAU ACTUEL ===");
        System.out.println("Maisons :");
        for (House h : network.getHouses().values()) {
            System.out.println(" - " + h.getNom() + " (" + h.getType() + ")");
        }
        System.out.println("Générateurs :");
        for (Generator g : network.getGenerators().values()) {
            System.out.println(" - " + g.getNom() + " (" + g.getKw() + " kW)");
        }
        System.out.println("Connexions :");
        for (Map.Entry<String, String> c : network.getLink().entrySet()) {
            System.out.println(" - " + c.getKey() + " → " + c.getValue());
        }
    }
}
