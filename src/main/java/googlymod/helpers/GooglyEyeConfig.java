package googlymod.helpers;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GooglyEyeConfig {
    public static class CardEye {
        public float x, y, size;
        public CardEye() {}
        public CardEye(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
    public static class CreatureEye extends CardEye {
        public String bone;
        public CreatureEye() {}
        public CreatureEye(float x, float y, float size, String bone) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.bone = bone;
        }
    }

    public HashMap<String,ArrayList<CardEye>> cards = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> relics = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> events = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> charSelect = new HashMap<>();
    public HashMap<String,ArrayList<CreatureEye>> creatures = new HashMap<>();

    // Global configuration
    private static GooglyEyeConfig theConfig;
    // Edited configuration
    // Note: use TreeMap to get sorted json files
    private static class EditedConfig {
        public TreeMap<String,ArrayList<CardEye>> cards = new TreeMap<>();
        public TreeMap<String,ArrayList<CardEye>> relics = new TreeMap<>();
        public TreeMap<String,ArrayList<CardEye>> events = new TreeMap<>();
        public TreeMap<String,ArrayList<CardEye>> charSelect = new TreeMap<>();
        public TreeMap<String,ArrayList<CreatureEye>> creatures = new TreeMap<>();

        private void save() {
            try {
                FileWriter writer = new FileWriter("googly-eye-locations.json");
                gson.toJson(this, writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    static EditedConfig editedConfig = new EditedConfig();

    private static ArrayList<CardEye> noEyes = new ArrayList<>();
    private static ArrayList<CreatureEye> noCreatureEyes = new ArrayList<>();

    private void merge(GooglyEyeConfig config) {
        cards.putAll(config.cards);
        relics.putAll(config.relics);
        events.putAll(config.events);
        charSelect.putAll(config.charSelect);
        creatures.putAll(config.creatures);
    }

    static Gson gson;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.setPrettyPrinting().create();
    }

    private static GooglyEyeConfig load(String path) {
        String contents = Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8));
        return gson.fromJson(contents, GooglyEyeConfig.class);
    }

    private static GooglyEyeConfig loadFromModJar(URL mod_jar) {
        URLClassLoader loader = null;
        try {
            loader = new URLClassLoader(new URL[] {mod_jar}, null);
            InputStream in = loader.getResourceAsStream("googly-eye-locations.json");
            if (in == null) return null;
            GooglyEyeConfig config = gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), GooglyEyeConfig.class);
            in.close();
            return config;
        } catch (Exception e) {
            System.out.println(mod_jar);
            e.printStackTrace();
            return null;
        } finally {
            if (loader != null) {
                try {
                    loader.close();
                } catch(Exception e) {
                    System.out.println(mod_jar);
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        theConfig = load("googlymod/eye-locations.json");
        //theConfig.merge(load("googlymod/eye-locations-conspire.json"));
        //theConfig.merge(load("googlymod/eye-locations-hubris.json"));
        // Load googly eye locations from other mods
        for (ModInfo modinfo : Loader.MODINFOS) {
            GooglyEyeConfig config = loadFromModJar(modinfo.jarURL);
            if (config != null) theConfig.merge(config);
        }
    }

    public static ArrayList<CardEye> getCardEyes(String cardId) {
        return theConfig.cards.getOrDefault(cardId, noEyes);
    }
    public static void setCardEyes(String cardId, ArrayList<CardEye> eyes) {
        theConfig.cards.put(cardId, eyes);
        editedConfig.cards.put(cardId, eyes);
        editedConfig.save();
    }

    public static ArrayList<CardEye> getRelicEyes(String relicId) {
        return theConfig.relics.getOrDefault(relicId, noEyes);
    }
    public static void setRelicEyes(String relicId, ArrayList<CardEye> eyes) {
        theConfig.relics.put(relicId, eyes);
        editedConfig.relics.put(relicId, eyes);
        editedConfig.save();
    }

    public static ArrayList<CardEye> getCharSelectEyes(String playerClass) {
        return theConfig.charSelect.getOrDefault(playerClass, noEyes);
    }
    public static void setCharSelectEyes(String playerClass, ArrayList<CardEye> eyes) {
        theConfig.charSelect.put(playerClass, eyes);
        editedConfig.charSelect.put(playerClass, eyes);
        editedConfig.save();
    }

    public static ArrayList<CardEye> getEventEyes(String eventImage) {
        return theConfig.events.getOrDefault(eventImage, noEyes);
    }
    public static void setEventEyes(String eventImage, ArrayList<CardEye> eyes) {
        theConfig.events.put(eventImage, eyes);
        editedConfig.events.put(eventImage, eyes);
        editedConfig.save();
    }

    public static ArrayList<CreatureEye> getCreatureEyes(String skeletonPath) {
        return theConfig.creatures.getOrDefault(skeletonPath, noCreatureEyes);
    }
    public static void setCreatureEyes(String eventImage, ArrayList<CreatureEye> eyes) {
        theConfig.creatures.put(eventImage, eyes);
        editedConfig.creatures.put(eventImage, eyes);
        editedConfig.save();
    }
}