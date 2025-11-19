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
        if (houses.containsKey(h)) {
            link.put(h, g);
        } else {
            link.put(g, h);
        }
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

    public double cout() {
        double lambda = 10.0;
        Map<String, Integer> loads = load();

        Map<String, Double> u = new HashMap<>();
        for (String gName : gen.keySet()) {
            int charge = loads.getOrDefault(gName, 0);
            int capacite = gen.get(gName).getKw();
            double taux = (double) charge / capacite;
            u.put(gName, taux);
        }

        double moyenne = 0.0;
        for (double val : u.values()) {
            moyenne += val;
        }
        moyenne /= gen.size();

        double disp = 0.0;
        for (double taux : u.values()) {
            disp += Math.abs(taux - moyenne);
        }

        double surcharge = 0.0;
        for (String gName : gen.keySet()) {
            int charge = loads.getOrDefault(gName, 0);
            int capacite = gen.get(gName).getKw();
            double depassement = (double) (charge - capacite) / capacite;
            if (depassement > 0) {
                surcharge += depassement;
            }
        }

        double coutTotal = disp + lambda * surcharge;

        System.out.println("=== CALCUL DU COÛT ===");
        System.out.printf("Disp(S) = %.4f%n", disp);
        System.out.printf("Surcharge(S) = %.4f%n", surcharge);
        System.out.printf("Cout(S) = %.4f%n", coutTotal);

        return coutTotal;
    }

}
