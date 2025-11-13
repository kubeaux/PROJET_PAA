package up.mi.paa.model;

import java.security.cert.CollectionCertStoreParameters;
import java.util.HashMap;
import java.util.Map;

public class Network {
    private Map<String, House> houses = new HashMap<>();
    private Map<String, Generator> gen = new HashMap<>();
    private Map<String, String> link = new HashMap<>();

    public Map<String, House> getHouses() {
        return this.houses;
    }

    public Map<String, Generator> getGenerators() {
        return this.gen;
    }

    public Map<String, String> getLink() {
        return this.link;
    }

    public void ajouteHouse(String nom, HouseType type) {
        this.houses.put(nom, new House(nom, type));
    }

    public void ajouteGenerator(String nom, int kw) {
        this.gen.put(nom, new Generator(nom, kw));
    }

    public int consommation() {
        int res=0;
        for (House h: houses.values()) {
            res += h.getType().getKw();
        }
        return res;
    }

    public int capacite() {
        int res = 0;
        for (Generator g: gen.values()) {
            res += g.getKw();
        }
        return res;
    }

    public void connecter(String h, String g) {
        link.put(h, g);
    }

    public void deconnecter(String h) {
        link.remove(h);
    }

    public Map<String, Integer> load() {
        Map<String, Integer> load = new HashMap<>();

        for (String gName : gen.keySet()) {
            load.put(gName, 0);
        }

        for (Map.Entry<String, String> entry : link.entrySet()) {
            String houseName = entry.getKey();
            String generatorName = entry.getValue();

            House house = houses.get(houseName);
            Generator generator = gen.get(generatorName);

            if (house != null && generator != null) {
                int consommation = house.getType().getKw();
                load.merge(generatorName, consommation, Integer::sum);
            }
        }

        return load;
    }

    public double calculerDisp() {
        Map<String, Integer> load = load();
        int nbGen = gen.size();
        if (nbGen == 0) {
            return 0.0;
        }

        double sommeU = 0.0;
        for (Map.Entry<String, Integer> entry : load.entrySet()) {
            String nomG = entry.getKey();
            int Lg = entry.getValue();
            int Cg = gen.get(nomG).getKw();
            double ug = (double) Lg / (double) Cg;
            sommeU += ug;
        }
        double u = sommeU / nbGen;

        double disp = 0.0;
        for (Map.Entry<String, Integer> entry : load.entrySet()) {
            String nomG = entry.getKey();
            int Lg = entry.getValue();
            int Cg = gen.get(nomG).getKw();
            double ug = (double) Lg / (double) Cg;
            disp += Math.abs(ug - u);
        }
        return disp;
    }

    public double calculerSurcharge() {
        Map<String, Integer> load = load();
        double surcharge = 0.0;

        for (Map.Entry<String, Integer> entry : load.entrySet()) {
            String nomG = entry.getKey();
            int Lg = entry.getValue();
            int Cg = gen.get(nomG).getKw();
            double terme = (double) (Lg - Cg) / (double) Cg;
            if (terme > 0) {
                surcharge += terme;
            }
        }
        return surcharge;
    }

    public double cout(int lambda) {
        double disp = calculerDisp();
        double surcharge = calculerSurcharge();
        return disp + lambda * surcharge;
    }

}
