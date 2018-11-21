package googlymod.helpers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GooglyEyeConfig {
    public static class CardEye {
        public float x, y, size;
    }
    public static class CreatureEye extends CardEye {
        public String bone;
    }

    public HashMap<String,ArrayList<CardEye>> cards = new HashMap<>();
    public HashMap<String,ArrayList<CreatureEye>> creatures = new HashMap<>();

    private static final String DATA_FILE = "googlymod/eye-locations.json";
    private static GooglyEyeConfig theConfig = null;

    private static ArrayList<CardEye> noEyes;
    static {
        noEyes = new ArrayList<>();
        {
            CardEye eye = new CardEye(); eye.x = 250-20; eye.y = 190; eye.size = 10;
            noEyes.add(eye);
        }
        {
            CardEye eye = new CardEye(); eye.x = 250+20; eye.y = 190; eye.size = 20;
            noEyes.add(eye);
        }
    }

    private static void load() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String contents = Gdx.files.internal(DATA_FILE).readString(String.valueOf(StandardCharsets.UTF_8));
        theConfig = gson.fromJson(contents, GooglyEyeConfig.class);
    }

    public static ArrayList<CardEye> getCardEyes(String cardId) {
        if (theConfig == null) load();
        ArrayList<CardEye> result = theConfig.cards.get(cardId);
        if (result == null) return noEyes;
        return result;
    }
}