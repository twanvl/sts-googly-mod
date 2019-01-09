package googlymod.helpers;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class GooglyEyeEditor {
    static GooglyEye activeEye = null;

    public static void updateEdit(float drawX, float drawY, float scale, float minX, float minY, float maxX, float maxY, ArrayList<GooglyEye> eyes, Consumer<ArrayList<GooglyEyeConfig.EyeLocation>> saveConfigs) {
        if (eyes == null) return;
        float x = (InputHelper.mX - drawX) / scale;
        float y = (InputHelper.mY - drawY) / scale;
        // find eye
        GooglyEye hoveredEye = null;
        for (GooglyEye eye : eyes) {
            if (eye.hovered()) {
                hoveredEye = eye;
                break;
            }
        }
        if (InputHelper.justClickedRight && hoveredEye != null) {
            // remove eye
            eyes.remove(hoveredEye);
            activeEye = null;
            saveConfig(eyes, saveConfigs);
        } else if (InputHelper.justClickedLeft && hoveredEye != null) {
            activeEye = hoveredEye;
            return;
        } else if (InputHelper.justClickedLeft && x >= minX && y >= minY && x <= maxX && y <= maxY && activeEye == null) {
            // add eye
            GooglyEyeConfig.EyeLocation config = new GooglyEyeConfig.EyeLocation(x, y, 25.f);
            eyes.add(new GooglyEye(config, drawX,drawY,scale));
            activeEye = null;
            saveConfig(eyes, saveConfigs);
        }
        if (activeEye != null) {
            GooglyEyeConfig.EyeLocation config = activeEye.config;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                // change radius
                float dx = x - config.x;
                float dy = y - config.y;
                config.size = (float)Math.sqrt(dx*dx + dy*dy);
                config.size = Math.max(config.size, 1.0f);
                activeEye.updatePosition(drawX,drawY,scale);
            } else {
                // move
                config.x = x;
                config.y = y;
                activeEye.updatePosition(drawX,drawY,scale);
            }
            if (InputHelper.justReleasedClickLeft) {
                activeEye = null;
                saveConfig(eyes, saveConfigs);
            }
        }
    }

    private static void saveConfig(ArrayList<GooglyEye> eyes, Consumer<ArrayList<GooglyEyeConfig.EyeLocation>> saveConfigs) {
        ArrayList<GooglyEyeConfig.EyeLocation> configs = new ArrayList<>();
        for (GooglyEye eye : eyes) {
            configs.add(eye.config);
        }
        saveConfigs.accept(configs);
    }
}