package googlymod.helpers;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GooglyEyeHelpers {
    public static ArrayList<GooglyEye> initEyes(ArrayList<GooglyEyeConfig.CardEye> configs, float drawX, float drawY, float scale) {
        ArrayList<GooglyEye> eyes = new ArrayList<>();
        for (GooglyEyeConfig.CardEye config : configs) {
            eyes.add(new GooglyEye(config, drawX, drawY, scale));
        }
        return eyes;
    }

    public static void updateEyes(ArrayList<GooglyEye> eyes, float drawX, float drawY, float scale) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.update(drawX, drawY, scale);
        }
    }

    public static void updateEyesForCursor(ArrayList<GooglyEye> eyes) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.updateForCursor();
        }
    }

    public static void renderEyes(ArrayList<GooglyEye> eyes, SpriteBatch sb) {
        if (eyes != null) {
            for (GooglyEye eye : eyes) {
                eye.render(sb);
            }
        }
    }
}