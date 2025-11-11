package up.mi.paa.model;

public class House {
    private String nom;
    private HouseType type;

    public House(String nom, HouseType type) {
        this.nom = nom;
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public HouseType getType() {
        return type;
    }
}
