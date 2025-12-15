package up.mi.paa.exception;

/**
 * Exception levée lors d'erreurs de parsing de fichier réseau
 */
public class NetworkParseException extends Exception {
    /** Numéro de ligne où l'erreur s'est produite */
    private int nbLigne;

    /** Ligne problématique */
    private String ligne;

    /**
     * Constructeur avec message et numéro de ligne.
     * 
     * @param message d'erreur
     * @param numéro de ligne
     * @param contenu de la ligne
     */
    public NetworkParseException(String message, int nbLigne, String ligne) {
        super(formatMessage(message, nbLigne, ligne));
        this.nbLigne = nbLigne;
        this.ligne = ligne;
    }

    /**
     * Constructeur avec cause.
     * 
     * @param message d'erreur
     * @param numéro de ligne
     * @param contenu de la ligne
     * @param cause de l'exception
     */
    public NetworkParseException(String message, int nbLigne, String ligne, Throwable cause) {
        super(formatMessage(message, nbLigne, ligne), cause);
        this.nbLigne = nbLigne;
        this.ligne = ligne;
    }

    /**
     * Formate le message d'erreur avec les informations de ligne
     */
    private static String formatMessage(String message, int nbLigne, String ligne) {
        return String.format("Erreur ligne %d: %s\n Ligne: %s", nbLigne, message, ligne);
    }

    public int getNbLigne() {
        return this.nbLigne;
    }

    public String getLigne() {
        return this.ligne;
    }
}
