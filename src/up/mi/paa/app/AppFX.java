package up.mi.paa.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import up.mi.paa.gui.NetworkFXApp;
import up.mi.paa.model.Network;

public class AppFX extends Application {

    @Override
    public void start(Stage stage) {
        Network network = new Network();
        TextArea console = new TextArea();
        console.setEditable(false);
        console.setPrefHeight(200);

        NetworkFXApp tabPane = new NetworkFXApp(network, console);

        VBox root = new VBox(tabPane, console);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Gestion réseau électrique");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
