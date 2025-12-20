package up.mi.paa.algo;

import up.mi.paa.model.*;
import java.util.*;

/**
 * Algo glouton amélioré qui essaie d'équilibrer intelligemment les charges entre les générateurs
 * 
 * Statégie:
 * 1. Identifie les générateurs sur/sous-utilisés
 * 2. Déplace les maisons des générateurs surchargés vers ceux sous-utilisés
 * 3. Optimisie l'équilibre global
 * 
 */
public class GreedySolver implements SolverAlgorithm {
    
    private Random random;

    public GreedySolver() {
        this.random = new Random();
    }

    public GreedySolver(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public double resoudre(Network network, int iter) {
        double coutActuel = network.calculerCout();
        int sansAmelioration = 0;
        int maxSansAmelioration = Math.max(100, iter / 10);

        for (int i = 0; i < iter && sansAmelioration < maxSansAmelioration; i++) {
            double newCout;

            // Alterner entre différentes stratégies
            if (i % 3 == 0) {
                newCout = optiEquilibre(network);
            } else if (i % 3 == 1) {
                newCout = optiEquilibre(network);
            } else {
                newCout = echangeAleatoire(network);
            }

            if (newCout < coutActuel) {
                coutActuel = newCout;
                sansAmelioration = 0;
            } else {
                sansAmelioration++;
            }
        }

        return coutActuel;
    }

    /**
     * Optimise l'equilibre des charges entre générateurs
     */
    private double optiEquilibre(Network network) {
        Map<String, Double> taux = network.getTauxUtilisation();

        if (taux.isEmpty()) {
            return network.calculerCout();
        }

        // Trouver le générateur le plus chargé et le moins chargé
        String genMax = null;
        String genMin = null;
        double tauxMax = -1;
        double tauxMin = Double.MAX_VALUE;

        for (Map.Entry<String, Double> entry : taux.entrySet()) {
            if (entry.getValue() > tauxMax) {
                tauxMax = entry.getValue();
                genMax = entry.getKey();
            }
            if (entry.getValue() < tauxMin) {
                tauxMin = entry.getValue();
                genMin = entry.getKey();
            }
        }

        // Si l'écart est significatif, déplacer une maison
        if (genMax != null && genMin != null && tauxMax - tauxMin > 0.1) {
            String movingHouse = findBestMovingHouse(network, genMax, genMin);
            if (movingHouse != null) {
                network.connecter(movingHouse, genMin);
            }
        }

        return network.calculerCout();
    }

    /**
     * Réduit les surcharges en déplaçant des maisons des générateurs surchargés
     */
    private double reduireSurcharges(Network network) {
        Map<String, Integer> charges = network.getCharges();

        // Trouver les générateurs surchargés
        for (Map.Entry<String, Generator> entry : network.getGenerators().entrySet()) {
            String nomGen = entry.getKey();
            Generator gen = entry.getValue();
            int charge = charges.getOrDefault(nomGen, 0);

            if (charge > gen.getKw()) {
                // Générateur surchargé, déplacer une maison
                String house = findHouseOnGen(network, nomGen);
                if (house != null) {
                    String newGen = findGenDispo(network, house);
                    if (newGen != null) {
                        network.connecter(house, newGen);
                        return network.calculerCout();
                    }
                }
            }
        }

        return network.calculerCout();
    }

    /**
     * Effectue un échange aléatoire intelligent
     */
    private double echangeAleatoire(Network network) {
        List<String> houses = new ArrayList<>(network.getHouses().keySet());
        List<String> gens = new ArrayList<>(network.getGenerators().keySet());

        if (houses.isEmpty() || gens.isEmpty()) {
            return network.calculerCout();
        }

        String house = houses.get(random.nextInt(houses.size()));
        String oldGen = network.getGeneratorConnect(house);
        String newGen = gens.get(random.nextInt(gens.size()));

        if (newGen.equals(oldGen) && gens.size() > 1) {
            while (newGen.equals(oldGen)) {
                newGen = gens.get(random.nextInt(gens.size()));
            }
        }

        double coutActuel = network.calculerCout();
        network.connecter(house, newGen);
        double newCout = network.calculerCout();

        // Revenir en arrière si pas d'amélioration
        if (newCout >= coutActuel) {
            network.connecter(house, oldGen);
            return coutActuel;
        }

        return newCout;
    }

    /**
     * Trouve la meilleure maison à déplacer d'un générateur à un autre
     */
    private String findBestMovingHouse(Network network, String genSource, String genDest) {
        String bestHouse = null;
        double bestGain = 0;

        for (Map.Entry<String, String> conn : network.getLink().entrySet()) {
            if (conn.getValue().equals(genSource)) {
                String house = conn.getKey();

                // Simuler le déplacement
                double coutAvant = network.calculerCout();
                network.connecter(house, genDest);
                double coutApres = network.calculerCout();
                double gain = coutAvant - coutApres;

                // Restaurer
                network.connecter(house, genSource);

                if (gain > bestGain) {
                    bestGain = gain;
                    bestHouse = house;
                }
            }
        }

        return bestHouse;
    }

    /**
     * Trouve une maison connectée à un générateur donné
     */
    private String findHouseOnGen(Network network, String nomGen) {
        for (Map.Entry<String, String> conn : network.getLink().entrySet()) {
            if (conn.getValue().equals(nomGen)) {
                return conn.getKey();
            }
        }
        return null;
    }

    /**
     * Trouve un générateur ayant de la capacité disponible
     */
    private String findGenDispo(Network network, String house) {
        House h = network.getHouse(house);
        if (h == null) {
            return null;
        }
        int conso = h.getConsommation();
        Map<String, Integer> charges = network.getCharges();

        String best = null;
        double bestTaux = Double.MAX_VALUE;

        for (Map.Entry<String, Generator> entry : network.getGenerators().entrySet()) {
            String nomGen = entry.getKey();
            Generator gen = entry.getValue();
            int charge = charges.getOrDefault(nomGen, 0);

            if (charge + conso <= gen.getKw()) {
                double tauxApres = (double) (charge + conso) / gen.getKw();
                if (tauxApres < bestTaux) {
                    bestTaux = tauxApres;
                    best = nomGen;
                }
            }
        }

        return best;
    }

    @Override
    public String getNom() {
        return "Algo Glouton Amélioré";
    }

    @Override
    public String getDesc() {
        return "Equilibre intelligement les charges et réduit les surcharges de façon systématique";
    }
}
