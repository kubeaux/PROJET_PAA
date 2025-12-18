package up.mi.paa.algo;

import up.mi.paa.model.Network;

/**
 * Interface définissant le contrat pour les algo de résolution
 * de problèmes d'affectation dans un réseau électrique
 */
public interface SolverAlgorithm {

    /**
     * Tente d'améliorer le réseau donné en optimisant les connexions
     * @param le réseau à optimiser (sera modifié)
     * @param nombre max d'itérations
     * @return le coût final du réseau après optimisation
     */
    double resoudre(Network network, int iter);

    /**
     * Retourne le nom de l'algo
     * 
     * @return nom de l'algo
     */
    String getNom();

    /**
     * Retourne une description de l'algo.
     * 
     * @return description
     */
    String getDesc();
}
