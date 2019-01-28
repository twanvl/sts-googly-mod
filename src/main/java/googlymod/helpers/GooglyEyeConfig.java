package googlymod.helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
    public static class EyeLocation {
        public float x, y, size;
        public EyeLocation() {}
        public EyeLocation(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
    public static class EyeLocationOnBone extends EyeLocation {
        public String bone;
        public EyeLocationOnBone() {}
        public EyeLocationOnBone(float x, float y, float size, String bone) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.bone = bone;
        }
    }

    public HashMap<String,ArrayList<EyeLocation>> cards = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocation>> relics = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocation>> events = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocation>> charSelect = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocationOnBone>> creatures = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocation>> creaturesStatic = new HashMap<>();
    public HashMap<String,ArrayList<EyeLocation>> orbs = new HashMap<>();

    // Global configuration
    private static GooglyEyeConfig theConfig;
    // Edited configuration
    // Note: use TreeMap to get sorted json files
    private static class EditedConfig {
        public TreeMap<String,ArrayList<EyeLocation>> cards = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocation>> relics = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocation>> events = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocation>> charSelect = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocationOnBone>> creatures = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocation>> creaturesStatic = new TreeMap<>();
        public TreeMap<String,ArrayList<EyeLocation>> orbs = new TreeMap<>();

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

    private static ArrayList<EyeLocation> noEyes = new ArrayList<>();
    private static ArrayList<EyeLocationOnBone> noEyesOnBone = new ArrayList<>();

    private void merge(GooglyEyeConfig config) {
        cards.putAll(config.cards);
        relics.putAll(config.relics);
        events.putAll(config.events);
        charSelect.putAll(config.charSelect);
        creatures.putAll(config.creatures);
        creaturesStatic.putAll(config.creaturesStatic);
        orbs.putAll(config.orbs);
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
        try {
            URL eyeLocations = new URL("jar", "", mod_jar + "!/googly-eye-locations.json");
            try (InputStream in = eyeLocations.openStream()) {
                return gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), GooglyEyeConfig.class);
            }
        } catch (IOException e) {}
        return null;
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

    public static ArrayList<EyeLocation> getCardEyes(String cardId) {
        return theConfig.cards.getOrDefault(cardId, noEyes);
    }
    public static void setCardEyes(String cardId, ArrayList<EyeLocation> eyes) {
        theConfig.cards.put(cardId, eyes);
        editedConfig.cards.put(cardId, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocation> getRelicEyes(String relicId) {
        return theConfig.relics.getOrDefault(relicId, noEyes);
    }
    public static void setRelicEyes(String relicId, ArrayList<EyeLocation> eyes) {
        theConfig.relics.put(relicId, eyes);
        editedConfig.relics.put(relicId, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocation> getCharSelectEyes(String playerClass) {
        return theConfig.charSelect.getOrDefault(playerClass, noEyes);
    }
    public static void setCharSelectEyes(String playerClass, ArrayList<EyeLocation> eyes) {
        theConfig.charSelect.put(playerClass, eyes);
        editedConfig.charSelect.put(playerClass, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocation> getEventEyes(String eventImage) {
        return theConfig.events.getOrDefault(eventImage, noEyes);
    }
    public static void setEventEyes(String eventImage, ArrayList<EyeLocation> eyes) {
        theConfig.events.put(eventImage, eyes);
        editedConfig.events.put(eventImage, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocationOnBone> getCreatureEyes(String skeletonPath) {
        return theConfig.creatures.getOrDefault(skeletonPath, noEyesOnBone);
    }
    public static void setCreatureEyes(String eventImage, ArrayList<EyeLocationOnBone> eyes) {
        theConfig.creatures.put(eventImage, eyes);
        editedConfig.creatures.put(eventImage, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocation> getStaticCreatureEyes(String creatureId) {
        return theConfig.creaturesStatic.getOrDefault(creatureId, noEyes);
    }
    public static void setStaticCreatureEyes(String creatureId, ArrayList<EyeLocation> eyes) {
        theConfig.creaturesStatic.put(creatureId, eyes);
        editedConfig.creaturesStatic.put(creatureId, eyes);
        editedConfig.save();
    }

    public static ArrayList<EyeLocation> getOrbEyes(String orbId) {
        return theConfig.orbs.getOrDefault(orbId, noEyes);
    }
    public static void setOrbEyes(String orbId, ArrayList<EyeLocation> eyes) {
        theConfig.orbs.put(orbId, eyes);
        editedConfig.orbs.put(orbId, eyes);
        editedConfig.save();
    }
}