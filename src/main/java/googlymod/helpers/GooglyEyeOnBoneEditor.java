package googlymod.helpers;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class GooglyEyeOnBoneEditor {
    static GooglyEyeOnBone activeEye = null;
    static Skeleton activeSkeleton = null;

    public static void updateEdit(Skeleton skeleton, ArrayList<GooglyEyeOnBone> eyes, Consumer<ArrayList<GooglyEyeConfig.EyeLocationOnBone>> saveConfigs) {
        if (eyes == null) return;
        // bounds
        Vector2 minBound = new Vector2(), sizeBound = new Vector2();
        skeleton.getBounds(minBound, sizeBound);
        Vector2 maxBound = sizeBound.add(minBound);
        // find eye
        GooglyEyeOnBone hoveredEye = null;
        for (GooglyEyeOnBone eye : eyes) {
            if (eye.hovered()) {
                hoveredEye = eye;
                break;
            }
        }
        if (InputHelper.justClickedRight && hoveredEye != null) {
            // remove eye
            eyes.remove(hoveredEye);
            activeEye = null;
            activeSkeleton = null;
            saveConfig(eyes, saveConfigs);
        } else if (InputHelper.justClickedLeft && hoveredEye != null) {
            activeEye = hoveredEye;
            activeSkeleton = skeleton;
            return;
        } else if (InputHelper.justClickedLeft && InputHelper.mX >= minBound.x && InputHelper.mY >= minBound.y && InputHelper.mX <= maxBound.x && InputHelper.mY <= maxBound.y && activeEye == null) {
            // find nearest bone
            Vector2 world = new Vector2(InputHelper.mX - skeleton.getX(), InputHelper.mY - skeleton.getY());
            Bone nearestBone = null;
            float nearestDist = 1e20f;
            for (Bone b : skeleton.getBones()) {
                float dist = world.dst(b.getWorldX(), b.getWorldY());
                if (dist < nearestDist) {
                    nearestBone = b;
                    nearestDist = dist;
                }
            }
            if (nearestBone == null) return;
            // transform to local
            Vector2 local = nearestBone.worldToLocal(world);
            // add eye
            GooglyEyeConfig.EyeLocationOnBone config = new GooglyEyeConfig.EyeLocationOnBone(local.x / Settings.scale, local.y / Settings.scale, 25.f, nearestBone.getData().getName());
            eyes.add(new GooglyEyeOnBone(config, skeleton));
            activeEye = null;
            activeSkeleton = null;
            saveConfig(eyes, saveConfigs);
        }
        if (activeEye != null && activeSkeleton == skeleton) {
            GooglyEyeConfig.EyeLocationOnBone config = activeEye.getConfig();
            Vector2 world = new Vector2(InputHelper.mX - skeleton.getX(), InputHelper.mY - skeleton.getY());
            Vector2 local = activeEye.bone.worldToLocal(world);
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                // change radius
                float dx = local.x / Settings.scale - config.x;
                float dy = local.y / Settings.scale - config.y;
                config.size = (float)Math.sqrt(dx*dx + dy*dy);
                config.size = Math.max(config.size, 1.0f);
                activeEye.update(skeleton, false);
            } else {
                // move
                config.x = local.x / Settings.scale;
                config.y = local.y / Settings.scale;
                activeEye.update(skeleton, false);
            }
            if (InputHelper.justReleasedClickLeft) {
                activeEye = null;
                saveConfig(eyes, saveConfigs);
            }
        }
    }

    private static void saveConfig(ArrayList<GooglyEyeOnBone> eyes, Consumer<ArrayList<GooglyEyeConfig.EyeLocationOnBone>> saveConfigs) {
        ArrayList<GooglyEyeConfig.EyeLocationOnBone> configs = new ArrayList<>();
        for (GooglyEyeOnBone eye : eyes) {
            configs.add(eye.getConfig());
        }
        saveConfigs.accept(configs);
    }
}