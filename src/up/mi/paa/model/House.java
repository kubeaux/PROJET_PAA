package up.mi.paa.model;

import java.util.Objects;

/**
 * Représente une maison consommatrice d'électricité dans le réseau.
 * Chaque maison a un nom unique et un type de consommation.
 */

public class House {
    /** Nom unique de la maison */
    private String nom;

    /** Type de consommation de la maison */
    private HouseType type;

    /**
     * Construteur d'une maison.
     * @param nom unique de la maison (ne peut pas être null ou vide)
     * @param type de consommation de la maison (ne peut pas être null)
     * @throws IllegalArgumentException si nom est null/vide ou type est null
     */
    public House(String nom, HouseType type) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("le nom de la maison ne peut pas être vide");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de la maison ne peut pas être null");
        }

        this.nom = nom.trim();
        this.type = type;
    }

    /**
     * Accesseurs du nom de la maison
     * 
     * @return le nom de la maison
     */
    public String getNom() {
        return nom;
    }

    /**
     * Accesseurs du type de conso de la maison.
     * 
     * @return le type de conso
     */
    public HouseType getType() {
        return type;
    }

    /**
     * Setters du type de conso de la maison.
     * 
     * @param le nouveau type de conso
     * @throws IllegalArgumentException si type est null
     */
    public void setType(HouseType type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de maison ne peut pas être null");
        }
        this.type = type;
    }

    /**
     * Accesseurs de la conso electrique de la maison
     * 
     * @return conso en kW
     */
    public int getConsommation() {
        return type.getKw();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        House house = (House) o;
        return Objects.equals(nom, house.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    @Override
    public String toString() {
        return nom + " (" + type + ", " + getConsommation() + " kW)";
    }

}
