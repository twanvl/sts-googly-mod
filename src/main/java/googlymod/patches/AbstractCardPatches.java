package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeHelpers;

public class AbstractCardPatches {
    @SpirePatch(clz=AbstractCard.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractCard.class, method="update")
    public static class Update {
        public static void Postfix(AbstractCard card) {
            float drawX = card.current_x + (-250.0f/2.0f) * card.drawScale * Settings.scale;
            float drawY = card.current_y + (72.0f-190.0f/2) * card.drawScale * Settings.scale;
            float scale = card.drawScale * 0.5f * Settings.scale;
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(card);
            if (eyes == null) {
                EyeFields.eyes.set(card, GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCardEyes(card.cardID), drawX,drawY,scale));
            } else {
                GooglyEyeHelpers.updateEyes(eyes, drawX,drawY,scale);
            }
        }
    }

    @SpirePatch(clz=AbstractCard.class, method="renderPortrait")
    @SpirePatch(clz=AbstractCard.class, method="renderJokePortrait")
    public static class Render {
        public static void Postfix(AbstractCard card, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(card), sb);
        }
    }
}