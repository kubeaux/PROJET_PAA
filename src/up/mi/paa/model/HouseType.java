package up.mi.paa.model;

/**
 * Enumération représentant les différents types de maisons
 * avec leur consommation électrique associée.
 */

public enum HouseType {
    /** Maison à basse conso (10 kW) */
    BASSE(10), 
    
    /** Maison à conso normale (20 kW) */
    NORMAL(20), 
    
    /** Maison à forte conso (40 kW) */
    FORTE(40);

    /** Consommation électrique en kW */
    private int kw;

    /**
     * Constructeur de l'énum
     */
    HouseType(int kw) { 
        this.kw = kw; 
    }

    /**
     * Retourne la consommation électrique du type de maison.
     * 
     * @return consommation en kW
     */
    public int getKw() {
        return this.kw;
    }

    /**
     * Convertit une chaîne de caractères en HouseType.
     * Accepte les formats: BASSE, Basse, basse, etc.
     * 
     * @param str la chaîne à convertir
     * @return le HouseType correspondant
     * @throws IllegalArgumentException si la chaîne ne correspond à aucun type
     */
    public static HouseType fromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de maison ne peut pas être vide");
        }

        String normalized = str.trim().toUpperCase();
        try {
            return HouseType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Type de maison invalide: " + str + ". Utilisez BASSE, NORMAL ou FORTE.");
        }
    }
}
