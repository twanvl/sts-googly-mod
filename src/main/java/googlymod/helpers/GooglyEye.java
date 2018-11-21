package googlymod.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class GooglyEye {
    GooglyEyeConfig.CardEye config;
    float x, y, radius;
    float pupilX = 0.f, pupilY = 0.f;
    private static final float PUPIL_RADIUS = 0.6f;
    private static final float PUPIL_MAX_OFFSET = 0.95f - PUPIL_RADIUS;
    private static Texture eyeTexture = ImageMaster.loadImage("googlymod/images/eye.png");
    private static Texture pupilTexture = ImageMaster.loadImage("googlymod/images/pupil.png");

    public GooglyEye(GooglyEyeConfig.CardEye config, float x, float y, float scale) {
        this.config = config;
        updatePosition(x,y,scale);
    }

    public void render(SpriteBatch sb) {
        float pupilRadius = radius * PUPIL_RADIUS;
        sb.draw(eyeTexture, x,y, radius,radius);
        sb.draw(pupilTexture, pupilX,pupilY, pupilRadius,pupilRadius);
    }

    public void update(float x, float y, float scale) {
        updatePosition(x,y,scale);
        // TODO: fancy pupil animation
    }

    private void updatePosition(float x, float y, float scale) {
        this.x = x + config.x * scale;
        this.y = y + config.y * scale;
        this.radius = config.size * scale;
    }
}
