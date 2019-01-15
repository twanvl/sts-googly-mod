package googlymod.helpers;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.core.Settings;

public class GooglyEyeOnBone extends GooglyEye {
    Bone bone;

    public GooglyEyeOnBone(GooglyEyeConfig.EyeLocationOnBone config, Skeleton skeleton) {
        super(config,0,0,0);
        bone = skeleton.findBone(config.bone);
        if (bone == null) {
            System.out.println("Bone not found: "+ config.bone);
        }
        update(skeleton,false);
    }

    public void update(Skeleton skeleton, boolean animate) {
        update(skeleton, animate, 0.1f);
    }
    public void update(Skeleton skeleton, boolean animate, float mouseFactor) {
        if (bone == null) return;
        GooglyEyeConfig.EyeLocationOnBone config = getConfig();
        Vector2 coord = bone.localToWorld(new Vector2(config.x * Settings.scale, config.y * Settings.scale));
        float scale = bone.getWorldScaleX() * Settings.scale;
        updateInternal(skeleton.getX() + coord.x, skeleton.getY() + coord.y, scale, animate, mouseFactor);
    }

    public GooglyEyeConfig.EyeLocationOnBone getConfig() {
        return (GooglyEyeConfig.EyeLocationOnBone)this.config;
    }
}
