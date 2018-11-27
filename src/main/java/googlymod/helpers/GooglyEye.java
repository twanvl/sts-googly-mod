package googlymod.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class GooglyEye {
    public GooglyEyeConfig.CardEye config;
    float x, y, radius;
    float pupilX = 0.f, pupilY = 0.f;
    float vx, vy, pupilVx, pupilVy;
    private static final float PUPIL_RADIUS = 0.52f;
    private static final float PUPIL_MAX_OFFSET = 0.95f - PUPIL_RADIUS;
    private static final float IMAGE_SCALE = 1.1f;
    private static final boolean EYE_ACCELARATION = false;
    private static final float EYE_BLEND = 10.0f;
    private static final float FRICTION_EYE = 10.0f;
    private static final float FRICTION_PUPIL = 10.0f;
    private static final float FRICTION_VELOCITY = 10.0f; // above this velocity, friction force scales linearly
    private static Texture eyeTexture = mipmapTexture("googlymod/images/eye.png");
    private static Texture pupilTexture = mipmapTexture("googlymod/images/pupil.png");

    private static Texture mipmapTexture(String path) {
        Texture retVal = new Texture(Gdx.files.internal(path), true);
        retVal.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        return retVal;
    }

    public GooglyEye(GooglyEyeConfig.CardEye config, float x, float y, float scale) {
        this.config = config;
        updatePosition(x,y,scale);
    }
    public GooglyEye(GooglyEyeConfig.CardEye config, float x, float y, float offsetX,float offsetY, float angle, float scale) {
        this.config = config;
        updatePosition(x,y,offsetX,offsetY,angle,scale);
    }

    public void render(SpriteBatch sb) {
        float size = radius * IMAGE_SCALE;
        float pupilSize = radius * PUPIL_RADIUS * IMAGE_SCALE;
        sb.draw(eyeTexture, x-size,y-size, size*2.f,size*2.f);
        sb.draw(pupilTexture, x+pupilX-pupilSize,y+pupilY-pupilSize, pupilSize*2.f,pupilSize*2.f);
    }

    public void update(float tx, float ty, float offsetX, float offsetY, float angle, float scale) {
        /*
        Logic for the animation / physics simulation
        ============================================

        0. Friction and scaling
        -----------------------
        Friction is simply constant*normalized vector*delta_t.
        If we want to be scale invariant (i.e. Settings.scale shouldn't affect animation),
        then the position,velcity and readius all scale with Settings.scale. This means that the friction term should scale too.
        Simplest is to scale with radius.

        1. Infering eye movement
        ------------------------
        The eye has a velocity v and position x at time 0.
        In update() we observe it having position x' at time t (where t = Gdx.graphics.getDeltaTime()).
        This means that over dt it needs to move by dx=(x'-x).
        And also, dx = \int_{0}^{t} v(u) du.
        If we have a constant acceleration a for this time, then v(u) = v+a*u
          dx = v*t + a*t^2/2
        Solving for a is easy,
          a = (dx/t - v) * 2 / t

        A potential problem with this model is that we get oscilations if dx=0 and v>0. So perhaps there should be some kind of friction in the system.
        Then we get v(u) = v + \int_{0}^{t} a-k*v(u)/|v(u)|
        How to evaluate this? As an approximation, assume that v(u)~= v+a_0*t/2 = v+(dx/t-v)/2 = v/2 + dx/t/2.
        Or by afterwards scaling v by (1-k*t/|v|)
        Or by scaling the previous velocity in that way.
        Another approximation is to fix |v(u)|=|v|, then you get
          v(0) = v
          v'(u) = a - k/|v|*v(u)
        which gives
          v(u) = r*a + (v + a)*exp(-k/|v|*u)
          v'(u) = r*k/|v|*a + -r*k/|v|*a -k/|v|*(v + a)*exp(-k/|v|*u
        this is a pita to solve for a, so don't go there

        2. Pupil movement
        -----------------
        Now we know the eye position, velocity and acceleration.
        The pupil is constrained to be in a circle around the eye.
        It experiences two forces:
        1. Friction with the eye. Assume that the eye is infinitely more massive, so there is no reverse effect
        2. Collision with the edge of the circle. This is a hard constraint.

        For 1, at time u, the relative velocity is (vp(u)-v(u)).
        So we accelerate by k*normalize(v(u)-vp(u)).
        In the frame of reference of the eye, we can do the same

        For 2, calculate pupil position, then scale it to be within the bounding circle.
        This affects the velocity, we can keep the component perpendicular to the direction to the center, setting the rest to 0 (in the frame of reference of the eye).

        3. Relative coordinates
        -----------------------
        The pupil uses coordinates relative to the eye, both for velocity and position.
        This means we get some kind of inertia.
        Ineratia means that changes in the eye's v inversely affect the pupil
        */

        // New position
        offsetX += config.x * scale;
        offsetY += config.y * scale;
        float newX = tx + MathUtils.cosDeg(angle) * offsetX - MathUtils.sinDeg(angle) * offsetY;
        float newY = ty + MathUtils.sinDeg(angle) * offsetX + MathUtils.cosDeg(angle) * offsetY;
        float newRadius = config.size * scale;
        float t = Gdx.graphics.getDeltaTime();
        t = Math.max(t, 0.0001f);

        // Friction
        if (EYE_ACCELARATION) {
            float v = (float)Math.sqrt(vx*vx + vy*vy);
            float scaleV = Math.max(0.f, 1.0f - FRICTION_EYE * radius * t / Math.min(radius*FRICTION_VELOCITY,Math.max(v,1e-5f)));
            vx *= scaleV;
            vy *= scaleV;
        }

        // acceleration/velocity of eye
        float dx = newX - x;
        float dy = newY - y;
        float newVx;
        float newVy;
        if (EYE_ACCELARATION) {
            float ax = (dx / t - vx) * 2.0f / t;
            float ay = (dy / t - vy) * 2.0f / t;
            newVx = vx + ax * t;
            newVy = vy + ay * t;
        } else if (EYE_BLEND > 0.0f) {
            float blend = (float)Math.exp(-EYE_BLEND * t);
            newVx = blend*vx + (1.f-blend)*dx/t;
            newVy = blend*vx + (1.f-blend)*dy/t;
        } else {
            newVx = dx/t;
            newVy = dy/t;
        }

        // Adjust pupil frame of reference
        pupilX -= dx;
        pupilY -= dy;
        pupilVx -= newVx - vx;
        pupilVy -= newVy - vy;

        // Pupil friction
        float pv = (float)Math.sqrt(pupilVx*pupilVx + pupilVy*pupilVy);
        float scalePV = Math.max(0.f, 1.0f - FRICTION_PUPIL * radius * t / Math.min(radius*FRICTION_VELOCITY,Math.max(pv,1e-5f)));
        pupilVx *= scalePV;
        pupilVy *= scalePV;

        // velocity of pupil
        pupilX += pupilVx * t;
        pupilY += pupilVy * t;

        // constrain pupil
        float pupilD = (float)Math.sqrt(pupilX*pupilX + pupilY*pupilY);
        if (pupilD > radius * PUPIL_MAX_OFFSET) {
            float pupilVradial = (pupilVx * pupilX + pupilVy * pupilY) / pupilD;
            pupilVx -= pupilVradial * pupilX/pupilD;
            pupilVy -= pupilVradial * pupilY/pupilD;
            pupilX *= radius * PUPIL_MAX_OFFSET / pupilD;
            pupilY *= radius * PUPIL_MAX_OFFSET / pupilD;
        }

        // Set
        this.x = newX;
        this.y = newY;
        this.vx = newVx;
        this.vy = newVy;
        this.radius = newRadius;
    }

    public void updatePosition(float x, float y, float scale) {
        this.x = x + config.x * scale;
        this.y = y + config.y * scale;
        this.radius = config.size * scale;
    }

    public void updatePosition(float x, float y, float offsetX, float offsetY, float angle, float scale) {
        offsetX += config.x * scale;
        offsetY += config.y * scale;
        this.x = x + MathUtils.cosDeg(angle) * offsetX - MathUtils.sinDeg(angle) * offsetY;
        this.y = y + MathUtils.sinDeg(angle) * offsetX + MathUtils.cosDeg(angle) * offsetY;
        this.radius = config.size * scale;
    }

    public void updateForCursor() {
        // Track mouse cursor
        pupilX = 0.001f * radius * (InputHelper.mX - x) / Settings.scale;
        pupilY = 0.001f * radius * (InputHelper.mY - y) / Settings.scale;

        // constrain pupil
        float pupilD = (float)Math.sqrt(pupilX*pupilX + pupilY*pupilY);
        if (pupilD > radius * PUPIL_MAX_OFFSET) {
            pupilX *= radius * PUPIL_MAX_OFFSET / pupilD;
            pupilY *= radius * PUPIL_MAX_OFFSET / pupilD;
        }
    }
    public boolean hovered() {
        float dx = InputHelper.mX - x;
        float dy = InputHelper.mY - y;
        return dx*dx + dy*dy <= radius*radius;
    }
}
