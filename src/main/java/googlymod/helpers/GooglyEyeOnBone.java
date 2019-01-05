package googlymod.helpers;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;

public class GooglyEyeOnBone extends GooglyEye {
    Bone bone;

    public GooglyEyeOnBone(GooglyEyeConfig.CreatureEye config, Skeleton skeleton) {
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
        GooglyEyeConfig.CreatureEye config = getConfig();
        Vector2 coord = bone.localToWorld(new Vector2(config.x, config.y));
        float scale = bone.getWorldScaleX();
        updateInternal(skeleton.getX() + coord.x, skeleton.getY() + coord.y, scale, animate, mouseFactor);
    }

    public GooglyEyeConfig.CreatureEye getConfig() {
        return (GooglyEyeConfig.CreatureEye)this.config;
    }
}