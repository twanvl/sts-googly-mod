package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;

public class SingleCardViewPopupPatches {
    @SpirePatch(clz=SingleCardViewPopup.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="open", paramtypez={AbstractCard.class})
    @SpirePatch(clz=SingleCardViewPopup.class, method="open", paramtypez={AbstractCard.class, CardGroup.class})
    public static class Open {
        public static void Postfix(SingleCardViewPopup self, AbstractCard card) {
            float drawX = (float)Settings.WIDTH / 2.0f - 250.0f * Settings.scale;
            float drawY = (float)Settings.HEIGHT / 2.0f - 190.0f * Settings.scale + 136.0f * Settings.scale;
            float scale = Settings.scale;
            ArrayList<GooglyEyeConfig.CardEye> configs = GooglyEyeConfig.getCardEyes(card.cardID);
            ArrayList<GooglyEye> eyes = new ArrayList<>();
            for (GooglyEyeConfig.CardEye config : configs) {
                eyes.add(new GooglyEye(config, drawX, drawY, scale));
            }
            EyeFields.eyes.set(self, eyes);
        }
        public static void Postfix(SingleCardViewPopup self, AbstractCard card, CardGroup group) {
            Postfix(self,card);
        }
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="renderPortrait")
    public static class Render {
        public static void Postfix(SingleCardViewPopup self, SpriteBatch sb) {
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
            if (eyes != null) {
                for (GooglyEye eye : eyes) {
                    eye.render(sb);
                }
            }
        }
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="update")
    public static class Update {
        public static void Postfix(SingleCardViewPopup self) {
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
            if (eyes != null) {
                for (GooglyEye eye : eyes) {
                    eye.updateForCursor();
                }
                // Editing
                if (Settings.isDebug) {
                    updateEdit(self);
                }
            }
        }
    }

    static GooglyEye activeEye = null;
    private static void updateEdit(SingleCardViewPopup self) {
        ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
        if (eyes == null) return;
        float drawX = (float)Settings.WIDTH / 2.0f - 250.0f * Settings.scale;
        float drawY = (float)Settings.HEIGHT / 2.0f - 190.0f * Settings.scale + 136.0f * Settings.scale;
        float scale = Settings.scale;
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
            saveConfig(self);
        } else if (InputHelper.justClickedLeft && hoveredEye != null) {
            activeEye = hoveredEye;
            return;
        } else if (InputHelper.justClickedLeft && x >= 0 && y >= 0 && x <= 500.0f && y <= 350.0f && activeEye == null) {
            // add eye
            GooglyEyeConfig.CardEye config = new GooglyEyeConfig.CardEye(x, y, 25.f);
            eyes.add(new GooglyEye(config, drawX,drawY,scale));
            activeEye = null;
            saveConfig(self);
        }
        if (activeEye != null) {
            GooglyEyeConfig.CardEye config = activeEye.config;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                // change radius
                float dx = x - config.x;
                float dy = y - config.y;
                config.size = (float)Math.sqrt(dx*dx + dy*dy);
                activeEye.updatePosition(drawX,drawY,scale);
            } else {
                // move
                config.x = x;
                config.y = y;
                activeEye.updatePosition(drawX,drawY,scale);
            }
            if (InputHelper.justReleasedClickLeft) {
                activeEye = null;
                saveConfig(self);
            }
        }
    }

    private static void saveConfig(SingleCardViewPopup self) {
        ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
        ArrayList<GooglyEyeConfig.CardEye> configs = new ArrayList<>();
        for (GooglyEye eye : eyes) {
            configs.add(eye.config);
        }
        AbstractCard card = (AbstractCard)ReflectionHacks.getPrivate(self, SingleCardViewPopup.class, "card");
        GooglyEyeConfig.setCardEyes(card.cardID, configs);
    }
}