package up.mi.paa.model;

public enum HouseType {
    Basse(10), Normal(20), Forte(40);
    private int kw;
    HouseType(int kw) { 
        this.kw = kw; 
    }
    public int getKw() {
        return this.kw;
    }
}
