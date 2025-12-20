package up.mi.paa.gui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import up.mi.paa.model.*;
import up.mi.paa.algo.*;
import up.mi.paa.io.NetworkFileHandler;

import java.util.List;
import java.util.Map;

public class NetworkFXApp extends TabPane {

    private Network network;
    private TextArea console;

    private Tab tabConstruction;
    private Tab tabGestion;
    private Tab tabFichier;

    public NetworkFXApp(Network network, TextArea console) {
        this.network = network;
        this.console = console;

        // Onglets
        tabConstruction = new Tab("Construction", constructionTab());
        tabConstruction.setClosable(false);

        tabGestion = new Tab("Gestion", gestionTab());
        tabGestion.setClosable(false);
        tabGestion.setDisable(true); // désactivé tant que réseau non validé

        tabFichier = new Tab("Fichier", fichierTab());
        tabFichier.setClosable(false);

        getTabs().addAll(tabConstruction, tabGestion, tabFichier);
    }

    // ======================================
    // Onglet Construction
    // ======================================
    private GridPane constructionTab() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(20);
        grid.setVgap(15);

        ColumnConstraints col = new ColumnConstraints();
        col.setHgrow(Priority.ALWAYS);
        col.setFillWidth(true);
        grid.getColumnConstraints().addAll(col, col);

        grid.add(blocAjouterGenerateur(), 0, 0);
        grid.add(blocAjouterMaison(), 1, 0);
        grid.add(blocAjouterConnexion(), 0, 1);
        grid.add(blocSupprimerConnexion(), 1, 1);
        grid.add(blocValiderReseau(), 0, 2, 2, 1);

        return grid;
    }

    // ======================================
    // Onglet Gestion
    // ======================================
    private VBox gestionTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Button btnCalculer = new Button("Calculer le coût");
        btnCalculer.setOnAction(e -> calculerCout());

        Button btnAfficher = new Button("Afficher réseau");
        btnAfficher.setOnAction(e -> afficherReseau());

        // Bloc modifier connexion avec 3 champs
        TitledPane modifierPane = new TitledPane();
        modifierPane.setText("Modifier Connexion");
        VBox modBox = new VBox(5);

        TextField txtMaison = new TextField();
        txtMaison.setPromptText("Maison (ex: M1)");

        TextField txtAncienGen = new TextField();
        txtAncienGen.setPromptText("Ancien Générateur");

        TextField txtNouveauGen = new TextField();
        txtNouveauGen.setPromptText("Nouveau Générateur");

        Button btnModifier = new Button("Modifier connexion");
        btnModifier.setOnAction(e -> {
            try {
                String maison = txtMaison.getText().trim();
                String ancien = txtAncienGen.getText().trim();
                String nouveau = txtNouveauGen.getText().trim();
                network.modifierConnexion(maison, ancien, nouveau);
                log("Connexion modifiée: " + maison + " : " + ancien + " -> " + nouveau);
                txtMaison.clear();
                txtAncienGen.clear();
                txtNouveauGen.clear();
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });

        modBox.getChildren().addAll(txtMaison, txtAncienGen, txtNouveauGen, btnModifier);
        modifierPane.setContent(modBox);

        vbox.getChildren().addAll(btnCalculer, btnAfficher, modifierPane);
        return vbox;
    }


    // ======================================
    // Onglet Fichier
    // ======================================
    private VBox fichierTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Button btnResolution = new Button("Résolution automatique");
        btnResolution.setOnAction(e -> resolutionAuto());

        Button btnSauvegarde = new Button("Sauvegarder solution");
        btnSauvegarde.setOnAction(e -> sauvegarderSolution());

        vbox.getChildren().addAll(btnResolution, btnSauvegarde);
        return vbox;
    }

    // ======================================
    // Blocs Construction
    // ======================================
    private TitledPane blocAjouterGenerateur() {
        VBox box = new VBox(5);
        TextField txtNom = new TextField();
        txtNom.setPromptText("Nom (ex: G1)");
        TextField txtCap = new TextField();
        txtCap.setPromptText("Capacité (kW)");

        Button btnAjouter = new Button("Ajouter Générateur");
        btnAjouter.setOnAction(e -> {
            try {
                String nom = txtNom.getText().trim();
                int cap = Integer.parseInt(txtCap.getText().trim());
                boolean existed = network.ajouteGenerator(nom, cap);
                log(existed ? "Générateur " + nom + " mis à jour" : "Générateur " + nom + " ajouté");
                txtNom.clear();
                txtCap.clear();
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(txtNom, txtCap, btnAjouter);
        return new TitledPane("Ajouter Générateur", box);
    }

    private TitledPane blocAjouterMaison() {
        VBox box = new VBox(5);
        TextField txtNom = new TextField();
        txtNom.setPromptText("Nom (ex: M1)");
        Label lblType = new Label("Consommation :");
        ComboBox<HouseType> comboType = new ComboBox<>();
        comboType.getItems().addAll(HouseType.values());

        Button btnAjouter = new Button("Ajouter Maison");
        btnAjouter.setOnAction(e -> {
            try {
                String nom = txtNom.getText().trim();
                HouseType type = comboType.getValue();
                boolean existed = network.ajouteHouse(nom, type);
                log(existed ? "Maison " + nom + " mise à jour" : "Maison " + nom + " ajoutée");
                txtNom.clear();
                comboType.setValue(null);
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(txtNom, lblType, comboType, btnAjouter);
        return new TitledPane("Ajouter Maison", box);
    }

    private TitledPane blocAjouterConnexion() {
        VBox box = new VBox(5);
        TextField txtElem1 = new TextField();
        txtElem1.setPromptText("Element 1");
        TextField txtElem2 = new TextField();
        txtElem2.setPromptText("Element 2");

        Button btnAjouter = new Button("Ajouter Connexion");
        btnAjouter.setOnAction(e -> {
            try {
                String e1 = txtElem1.getText().trim();
                String e2 = txtElem2.getText().trim();
                if (network.houseExist(e1) && network.generatorExist(e2)) {
                    network.connecter(e1, e2);
                } else if (network.generatorExist(e1) && network.houseExist(e2)) {
                    network.connecter(e2, e1);
                } else {
                    log("Erreur: un des éléments n'existe pas");
                    return;
                }
                log("Connexion ajoutée: " + e1 + " <-> " + e2);
                txtElem1.clear();
                txtElem2.clear();
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(txtElem1, txtElem2, btnAjouter);
        return new TitledPane("Ajouter Connexion", box);
    }

    private TitledPane blocSupprimerConnexion() {
        VBox box = new VBox(5);
        TextField txtElem1 = new TextField();
        txtElem1.setPromptText("Element 1");
        TextField txtElem2 = new TextField();
        txtElem2.setPromptText("Element 2");

        Button btnSupprimer = new Button("Supprimer Connexion");
        btnSupprimer.setOnAction(e -> {
            try {
                String h = network.houseExist(txtElem1.getText().trim()) ? txtElem1.getText().trim() : txtElem2.getText().trim();
                String g = network.getGeneratorConnect(h);
                if (g != null) {
                    network.deconnecter(h);
                    log("Connexion supprimée: " + h + " - " + g);
                } else {
                    log("Cette connexion n'existe pas!");
                }
                txtElem1.clear();
                txtElem2.clear();
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(txtElem1, txtElem2, btnSupprimer);
        return new TitledPane("Supprimer Connexion", box);
    }

    private VBox blocValiderReseau() {
        VBox box = new VBox(5);
        Button btnValider = new Button("Valider le réseau");
        btnValider.setOnAction(e -> {
            List<String> nonConnectees = network.getHousesNoConnect();
            if (nonConnectees.isEmpty()) {
                log("Réseau conforme !");
                tabGestion.setDisable(false);
            } else {
                log("Maisons non connectées: " + nonConnectees);
            }
        });
        box.getChildren().add(btnValider);
        return box;
    }

    // ======================================
    // Gestion actions
    // ======================================
    private void calculerCout() {
        StringBuilder sb = new StringBuilder();
        sb.append("========= CALCUL DU COÛT =========\n");
        double disp = network.calculerDisp();
        double surcharge = network.calculerSurcharge();
        double cout = network.calculerCout();

        sb.append(String.format("Dispersion: %.4f%n", disp));
        sb.append(String.format("Surcharge: %.4f%n", surcharge));
        sb.append(String.format("Lambda: %.1f%n", network.getLambda()));
        sb.append(String.format("COÛT TOTAL: %.4f%n", cout));
        sb.append("\nDétails par générateur:\n");

        Map<String, Generator> gens = network.getGenerators();
        Map<String, Integer> charges = network.getCharges();
        Map<String, Double> taux = network.getTauxUtilisation();
        for (String nom : gens.keySet()) {
            Generator g = gens.get(nom);
            int charge = charges.get(nom);
            double t = taux.get(nom);
            String status = charge > g.getKw() ? "SURCHARGE" : "Validé";
            sb.append(String.format("  %s %s: %d/%d kW (%.1f%%)%n", status, nom, charge, g.getKw(), t*100));
        }
        sb.append("==================================\n");

        log(sb.toString());
    }


    private void afficherReseau() {
        StringBuilder sb = new StringBuilder();
        sb.append("========= RÉSEAU ÉLECTRIQUE ACTUEL =========\n");
        sb.append("Maisons: ").append(network.getHouses().size()).append("\n");
        sb.append("Générateurs: ").append(network.getGenerators().size()).append("\n");
        sb.append("Connexions: ").append(network.getLink().size()).append("\n");
        sb.append("Consommation totale: ").append(network.getConsoTotale()).append(" kW\n");
        sb.append("Capacité totale: ").append(network.getCapaciteTotale()).append(" kW\n\n");

        sb.append("MAISONS:\n");
        for (House h : network.getHouses().values()) {
            String gen = network.getGeneratorConnect(h.getNom());
            String connexion = gen != null ? " -> " + gen : " (non connectée)";
            sb.append("  ").append(h).append(connexion).append("\n");
        }

        sb.append("\nGÉNÉRATEURS:\n");
        Map<String, Integer> charges = network.getCharges();
        for (Generator g : network.getGenerators().values()) {
            int charge = charges.get(g.getNom());
            sb.append("  ").append(g).append(" - Charge: ").append(charge).append("/").append(g.getKw()).append(" kW\n");
        }

        sb.append("===========================================\n");
        log(sb.toString());
    }

    // ======================================
    // Fichier actions
    // ======================================
    private void resolutionAuto() {
        // Vérifier si le réseau est valide
        List<String> nonConnectees = network.getHousesNoConnect();
        if (!nonConnectees.isEmpty()) {
            log("Erreur: le réseau n'est pas valide. Maisons non connectées: " + nonConnectees);
            return;
        }

        TextInputDialog algoDialog = new TextInputDialog("1");
        algoDialog.setTitle("Choix de l'algorithme");
        algoDialog.setHeaderText("1 = Naive, 2 = Glouton");
        algoDialog.setContentText("Votre choix: ");

        algoDialog.showAndWait().ifPresent(algo -> {
            int choix = algo.equals("2") ? 2 : 1;

            TextInputDialog iterDialog = new TextInputDialog("1000");
            iterDialog.setTitle("Nombre d'itérations");
            iterDialog.setHeaderText("Entrez le nombre d'itérations");
            iterDialog.setContentText("Iterations: ");
            iterDialog.showAndWait().ifPresent(iterStr -> {
                try {
                    int iter = Integer.parseInt(iterStr);
                    SolverAlgorithm solver = choix == 1 ? new NaiveSolver() : new GreedySolver();

                    log("Coût initial: " + network.calculerCout());
                    log("Optimisation en cours...");

                    double coutFinal = solver.resoudre(network, iter);

                    log("Optimisation terminée !");
                    log("Algorithme: " + (choix==1 ? "Naïf" : "Glouton"));
                    log("Coût final: " + String.format("%.4f", coutFinal));
                } catch (Exception ex) {
                    log("Erreur lors de l'optimisation: " + ex.getMessage());
                }
            });
        });
    }


    private void sauvegarderSolution() {
        TextInputDialog dialog = new TextInputDialog("solution.txt");
        dialog.setTitle("Sauvegarder solution");
        dialog.setHeaderText("Nom du fichier");
        dialog.setContentText("Nom: ");
        dialog.showAndWait().ifPresent(nomFichier -> {
            try {
                NetworkFileHandler.saveFiles(network, nomFichier);
                log("Solution sauvegardée dans: " + nomFichier);
            } catch (Exception ex) {
                log("Erreur: " + ex.getMessage());
            }
        });
    }

    // ======================================
    // Console log
    // ======================================
    private void log(String msg) {
        console.appendText(msg + "\n");
    }
}
