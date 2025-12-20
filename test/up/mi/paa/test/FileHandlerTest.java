package test.up.mi.paa.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import up.mi.paa.io.NetworkFileHandler;
import up.mi.paa.exception.NetworkParseException;
import up.mi.paa.model.Network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHandlerTest {
    
    @TempDir
    Path tempDir; // JUnit crée un dossier temporaire auto-nettoyé

    @Test
    void testLectureFichierValide() throws IOException, NetworkParseException {
        // Création d'un fichier valide à la volée
        Path fichier = tempDir.resolve("test_valide.txt");
        List<String> lignes = List.of(
            "generateur(G1, 100).",
            "maison(M1, NORMAL).",
            "connexion(G1,M1)."
        );
        Files.write(fichier, lignes);

        Network net = NetworkFileHandler.readFiles(fichier.toString(), 10);

        assertNotNull(net);
        assertEquals(1, net.getGenerators().size());
        assertEquals(1, net.getHouses().size());
        assertTrue(net.estValide());
    }

    @Test
    void testErreurOrdreDefinition() throws IOException {
        // Test : Maison définie avant générateur (interdit)
        Path fichier = tempDir.resolve("test_ordre.txt");
        List<String> lignes = List.of(
            "maison(M1, NORMAL).",
            "generateur(G1, 100)."
        );
        Files.write(fichier, lignes);

        // On s'attend à une NetworkParseException
        assertThrows(NetworkParseException.class, () -> {
            NetworkFileHandler.readFiles(fichier.toString(), 10);
        }, "Devrait rejeter l'ordre incorrect");
    }

    @Test
    void testErreurSyntaxe() throws IOException {
        //Test : Oubli de parenthèse
        Path fichier = tempDir.resolve("test_syntaxe.txt");
        List<String> lignes = List.of(
            "generateur G1, 100."
        );
        Files.write(fichier, lignes);

        assertThrows(NetworkParseException.class, () -> {
            NetworkFileHandler.readFiles(fichier.toString(), 10);
        });
    }
}
