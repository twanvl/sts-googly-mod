package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeHelpers;

public class AbstractRelicPatches {
    @SpirePatch(clz=AbstractRelic.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractRelic.class, method="update")
    public static class Update {
        public static void Postfix(AbstractRelic relic) {
            float drawX = relic.currentX;
            float drawY = relic.currentY;
            float scale = relic.scale * 0.5f;
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(relic);
            if (eyes == null) {
                EyeFields.eyes.set(relic, GooglyEyeHelpers.initEyes(GooglyEyeConfig.getRelicEyes(relic.relicId), drawX,drawY,scale));
            } else {
                GooglyEyeHelpers.updateEyes(eyes, drawX,drawY,scale);
            }
        }
    }

    @SpirePatch(clz=AbstractRelic.class, method="renderInTopPanel")
    @SpirePatch(clz=AbstractRelic.class, method="render", paramtypez={SpriteBatch.class})
    @SpirePatch(clz=AbstractRelic.class, method="render", paramtypez={SpriteBatch.class, boolean.class, Color.class})
    @SpirePatch(clz=AbstractRelic.class, method="renderWithoutAmount", paramtypez={SpriteBatch.class, Color.class})
    public static class Render {
        public static void Postfix(AbstractRelic relic, SpriteBatch sb) {
            if (Settings.hideRelics) return;
            sb.setColor(Color.WHITE);
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(relic), sb);
        }
        public static void Postfix(AbstractRelic relic, SpriteBatch sb, Color outlineColor) {
            if (Settings.hideRelics) return;
            sb.setColor(Color.WHITE);
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(relic), sb);
        }
        public static void Postfix(AbstractRelic relic, SpriteBatch sb, boolean renderAmound, Color outlineColor) {
            if (Settings.hideRelics) return;
            sb.setColor(Color.WHITE);
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(relic), sb);
        }
    }
}