package up.mi.paa.model;

import java.util.Objects;

/**
 * Représente un générateur électrique dans le réseau.
 * Chaque générateur a un nom unique et une capacité max en kW.
 */

public class Generator {
    /** Nom unique du générateur */
    private String nom;

    /** Capacité max en kW */
    private int kw;

    /**
     * Constructeur d'un générateur.
     * 
     * @param nom unique du générateur (ne peut pas être null ou vide)
     * @param kw max (doit être > 0)
     * @throws IllegalArgumentException si nom est null/vide ou kW <= 0
     */
    public Generator(String nom, int kw) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du générateur ne peut pas être vide");
        }
        if (kw <= 0) {
            throw new IllegalArgumentException("La capacité de kW doit être strictement positive");
        }

        this.nom = nom.trim();
        this.kw = kw;
    }

    /**
     * Accesseur du nom du générateur.
     * 
     * @return le nom du générateur
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Accesseur de la capacité max du générateur
     * 
     * @return la capcité en kW
     */
    public int getKw() {
        return this.kw;
    }

    /**
     * Setter de la capicité max du générateur
     * 
     * @param la nouvelle capacité en kW
     * @throws IllegalArgumentException si capacité <= 0
     */
    public void setKw(int kw) {
        if (kw <= 0) {
            throw new IllegalArgumentException("La capacité doit être strictement positive");
        }
        this.kw = kw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Generator generator = (Generator) o;
        return Objects.equals(nom, generator.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    @Override
    public String toString() {
        return nom + " (" + kw + " kW)";
    }
}
