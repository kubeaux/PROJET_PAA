package up.mi.paa.model;

public class Generator {
    private String nom;
    private int kw;

    public Generator(String nom, int kw) {
        this.nom = nom;
        this.kw = kw;
    }
    public String getNom() {
        return this.nom;
    }
    public int getKw() {
        return this.kw;
    }
}
