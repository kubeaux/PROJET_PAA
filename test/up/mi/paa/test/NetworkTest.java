package test.up.mi.paa.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import up.mi.paa.model.*;

/**
 * Tests unitaires pour la logique métier (Network).
 * Vérifie les calculs de coûts et la gestion de connexions.
 */
public class NetworkTest {
    
    private Network network;

    @BeforeEach
    void setUp() {
        // On recrée un réseau propre avant chaque test
        network = new Network(10.0); // Lambda = 10
    }

    @Test
    void testAjoutElements() {
        network.ajouteGenerator("G1", 100);
        network.ajouteHouse("M1", HouseType.NORMAL); // 20kW

        assertTrue(network.generatorExist("G1"), "Le générateur devrait exister");
        assertTrue(network.houseExist("M1"), "La maison devrait exister");
        assertEquals(100, network.getGenerator("G1").getKw());
    }

    @Test
    void testConnexionUnique() {
        network.ajouteGenerator("G1", 100);
        network.ajouteGenerator("G2", 100);
        network.ajouteHouse("M1", HouseType.NORMAL);

        network.connecter("M1", "G1");
        assertEquals("G1", network.getGeneratorConnect("M1"), "M1 devrait être sur G1");

        // Changement de connexion (doit écraser la précédente)
        network.connecter("M1", "G2");
        assertEquals("G2", network.getGeneratorConnect("M1"), "M1 devrait être passée sur G2");
    }

    @Test
    void testCalculCout_CasParfait() {
        // Scénario : 2 Générateurs (60kW), 2 Maisons (60kW chacune)
        // Charge = 100%, pas de surcharge, équilibre parfait.
        network.ajouteGenerator("G1", 60);
        network.ajouteGenerator("G2", 60);

        // On triche un peu sur les types pour l'exemple ou on combine
        network.ajouteHouse("M1_1", HouseType.FORTE); // 40
        network.ajouteHouse("M1_2", HouseType.NORMAL); // 20 -> Total 60 sur G1

        network.ajouteHouse("M2_1", HouseType.FORTE); // 40
        network.ajouteHouse("M2_2", HouseType.NORMAL); // 20 -> Total 60 sur G2

        network.connecter("M1_1", "G1");
        network.connecter("M1_2", "G1");
        network.connecter("M2_1", "G2");
        network.connecter("M2_2", "G2");
        
        // Verifs
        assertEquals(0.0, network.calculerSurcharge(), 0.0001, "Surcharge devrait être 0");
        assertEquals(0.0, network.calculerDisp(), 0.0001, "Dispersion devrait être 0 (équilibre parfait)");
        assertEquals(0.0, network.calculerCout(), 0.0001, "Coût total devrait être 0");
    }

    @Test
    void testCalculCout_Surcharge() {
        // G1 (50kW) avec M1 (40) + M2 (20) = 60kW -> Surcharge de 10kW
        network.ajouteGenerator("G1", 50);
        network.ajouteHouse("M1", HouseType.FORTE);
        network.ajouteHouse("M2", HouseType.NORMAL);

        network.connecter("M1", "G1");
        network.connecter("M2", "G1"); // Charge 60/50 = 1.2

        // Calcul théorique : Surcharge = (60-50)/50 = 0.2
        // Disp = 0 (car 1 seul générateur, écart à la moyenne est nul)
        // Coût = 0 + 10 * 0.2 = 2.0

        assertEquals(0.2, network.calculerSurcharge(), 0.0001, "Surcharge incorrecte");
        assertEquals(2.0, network.calculerCout(), 0.0001, "Coût incorrect avec lambda=10");
    }
}
