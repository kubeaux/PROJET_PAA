package up.mi.paa.algo;

import up.mi.paa.model.*;
import java.util.*;

/**
 * Algo naif de résolution basé sur des échanges aléatoires
 * Cet algo essaie k fois de changer aléatoirement la connexion
 * d'une maison et garde le changement si cela améliore le coût.
 */
public class NaiveSolver implements SolverAlgorithm {

    private Random random;

    /**
     * Constructeur par défaut
     */
    public NaiveSolver() {
        this.random = new Random();
    }

    /**
     * Constructeur avec seed pour la reproductibilité
     * 
     * @param seed pour le générateur aléatoire
     */
    public NaiveSolver(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public double resoudre(Network network, int iter) {
        List<String> houses = new ArrayList<>(network.getHouses().keySet());
        List<String> gens = new ArrayList<>(network.getGenerators().keySet());

        if (houses.isEmpty() || gens.isEmpty()) {
            return network.calculerCout();
        }

        double coutActuel = network.calculerCout();

        for (int i = 0; i < iter; i++) {
            //Choisir une maison et un générateur au hasard
            String house = houses.get(random.nextInt(houses.size()));
            String gen = gens.get(random.nextInt(gens.size()));

            //Sauvegarder la connexion actuelle
            String oldGen = network.getGeneratorConnect(house);

            //Essayer la nouvelle connexion
            network.connecter(house, gen);
            double newCout = network.calculerCout();

            //Garder si c'est mieux, sinon revenir en arrière
            if (newCout < coutActuel) {
                coutActuel = newCout;
            } else {
                network.connecter(house, oldGen);
            }
        }

        return coutActuel;
    }

    @Override
    public String getNom() {
        return "Algo Naïf";
    }

    @Override
    public String getDesc() {
        return "Essaie des connexions aléatoires et garde celles qui améliorent le coût.";
    }
}
