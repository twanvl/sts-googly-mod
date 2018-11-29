package googlymod.helpers;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Skeleton;

public class GooglyEyeHelpers {
    public static ArrayList<GooglyEye> initEyes(ArrayList<GooglyEyeConfig.CardEye> configs, float originX,float originY, float offsetX, float offsetY, float angle, float scale) {
        ArrayList<GooglyEye> eyes = new ArrayList<>();
        for (GooglyEyeConfig.CardEye config : configs) {
            eyes.add(new GooglyEye(config, originX, originY, offsetX, offsetY, angle, scale));
        }
        return eyes;
    }

    public static ArrayList<GooglyEyeOnBone> initEyes(ArrayList<GooglyEyeConfig.CreatureEye> configs, Skeleton skeleton) {
        ArrayList<GooglyEyeOnBone> eyes = new ArrayList<>();
        for (GooglyEyeConfig.CreatureEye config : configs) {
            eyes.add(new GooglyEyeOnBone(config, skeleton));
        }
        return eyes;
    }

    public static void updateEyes(ArrayList<GooglyEye> eyes, float originX,float originY, float offsetX, float offsetY, float angle, float scale) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.update(originX, originY, offsetX, offsetY, angle, scale);
        }
    }

    public static void updateEyes(ArrayList<GooglyEye> eyes, float x, float y, float scale, boolean animate, float mouseFactor) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.update(x,y,scale,animate,mouseFactor);
        }
    }

    public static void updateEyesPosition(ArrayList<GooglyEye> eyes, float x, float y, float scale) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.updatePosition(x,y,scale);
        }
    }

    public static void updateEyes(ArrayList<GooglyEyeOnBone> eyes, Skeleton skeleton) {
        if (eyes == null) return;
        for (GooglyEyeOnBone eye : eyes) {
            eye.update(skeleton, true);
        }
    }

    public static void updateEyesForCursor(ArrayList<? extends GooglyEye> eyes) {
        if (eyes == null) return;
        for (GooglyEye eye : eyes) {
            eye.updateForCursor();
        }
    }

    public static void renderEyes(ArrayList<? extends GooglyEye> eyes, SpriteBatch sb) {
        renderEyes(eyes, sb, Color.WHITE);
    }
    public static void renderEyes(ArrayList<? extends GooglyEye> eyes, SpriteBatch sb, Color color) {
        if (eyes != null) {
            sb.setColor(color);
            for (GooglyEye eye : eyes) {
                eye.render(sb);
            }
        }
    }
}