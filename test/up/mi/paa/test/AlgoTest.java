package test.up.mi.paa.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import up.mi.paa.model.*;
import up.mi.paa.algo.*;

public class AlgoTest {
    
    @Test
    void testAmeliorationOuStabilite() {
        // Setup un réseau mal optimisé
        Network net = new Network();
        net.ajouteGenerator("G1", 50); // Petit
        net.ajouteGenerator("G2", 100); // Gros

        // On met tout sur le petit (Surcharge !)
        net.ajouteHouse("M1", HouseType.FORTE); // 40
        net.ajouteHouse("M2", HouseType.FORTE); // 40

        net.connecter("M1", "G1");
        net.connecter("M2", "G1");

        double coutAvant = net.calculerCout();

        // Lancer l'algo Glouton
        SolverAlgorithm solver = new GreedySolver();
        double coutApres = solver.resoudre(net, 1000);

        // Assertions
        assertTrue(coutApres <= coutAvant, "L'algo ne devrait jamais dégrader la solution");
        assertTrue(net.estValide(), "Le réseau doit rester valide après optimisation");

        // Normalement, le glouton doit avoir déplacé au moins une maison vers G2
        boolean aBouge = "G2".equals(net.getGeneratorConnect("M1")) ||
                        "G2".equals(net.getGeneratorConnect("M2"));
        assertTrue(aBouge, "L'algo aurait dû déplacer une maison vers le gros générateur");
    }
}
