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

        for (String g : gen.keySet()) {
            load.put(g, 0);
        }

        for (Map.Entry<String, String> link = link.entrySet()) {
            if (h != null) {
                load.merge(link.getValue(), h.getType().getKw(), Integer::sum);
            }
        }
        return load;

    }


}
