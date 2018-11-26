package googlymod.helpers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.helpers.AsyncSaver;

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
    }

    public HashMap<String,ArrayList<CardEye>> cards = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> relics = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> events = new HashMap<>();
    public HashMap<String,ArrayList<CardEye>> charSelect = new HashMap<>();
    public HashMap<String,ArrayList<CreatureEye>> creatures = new HashMap<>();

    private static final String DATA_FILE = "googlymod/eye-locations.json";
    private static GooglyEyeConfig theConfig = null;

    private static boolean lotsOfEyes = false;
    private static ArrayList<CardEye> noEyes;
    static {
        noEyes = new ArrayList<>();
        if (lotsOfEyes) {
            noEyes.add(new CardEye(250-30, 190, 25));
            noEyes.add(new CardEye(250+30, 190, 25));
            noEyes.add(new CardEye(250, 90, 50));
        }
    }

    private static void load() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String contents = Gdx.files.internal(DATA_FILE).readString(String.valueOf(StandardCharsets.UTF_8));
        theConfig = gson.fromJson(contents, GooglyEyeConfig.class);
    }

    private static void save() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        AsyncSaver.save("eye-locations.json", gson.toJson(theConfig));
    }

    public static ArrayList<CardEye> getCardEyes(String cardId) {
        if (theConfig == null) load();
        ArrayList<CardEye> result = theConfig.cards.get(cardId);
        if (result == null) return noEyes;
        return result;
    }
    public static void setCardEyes(String cardId, ArrayList<CardEye> eyes) {
        if (theConfig == null) load();
        theConfig.cards.put(cardId, eyes);
        save();
    }

    public static ArrayList<CardEye> getRelicEyes(String relicId) {
        if (theConfig == null) load();
        ArrayList<CardEye> result = theConfig.relics.get(relicId);
        if (result == null) return noEyes;
        return result;
    }
    public static void setRelicEyes(String relicId, ArrayList<CardEye> eyes) {
        if (theConfig == null) load();
        theConfig.relics.put(relicId, eyes);
        save();
    }
}