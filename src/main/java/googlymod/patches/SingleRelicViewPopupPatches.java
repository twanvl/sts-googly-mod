package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;

public class SingleRelicViewPopupPatches {
    private static final float RELIC_OFFSET_Y = 76.0f * Settings.scale;

    @SpirePatch(clz=SingleRelicViewPopup.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="open", paramtypez={AbstractRelic.class})
    @SpirePatch(clz=SingleRelicViewPopup.class, method="open", paramtypez={AbstractRelic.class, ArrayList.class})
    public static class Open {
        public static void Postfix(SingleRelicViewPopup self, AbstractRelic relic) {
            float drawX = (float)Settings.WIDTH / 2.0f;
            float drawY = (float)Settings.HEIGHT / 2.0f + RELIC_OFFSET_Y;
            float scale = Settings.scale;
            ArrayList<GooglyEye> eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getRelicEyes(relic.relicId), drawX, drawY,0,0,0, scale);
            EyeFields.eyes.set(self, eyes);
        }
        public static void Postfix(SingleRelicViewPopup self, AbstractRelic relic, ArrayList<AbstractRelic> group) {
            Postfix(self,relic);
        }
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="renderRelicImage")
    public static class Render {
        public static void Postfix(SingleRelicViewPopup self, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(self), sb);
        }
    }

    @SpirePatch(clz=SingleRelicViewPopup.class, method="update")
    public static class Update {
        public static void Postfix(SingleRelicViewPopup self) {
            GooglyEyeHelpers.updateEyesForCursor(EyeFields.eyes.get(self));
            // Editing
            if (Settings.isDebug) {
                updateEdit(self);
            }
        }
    }

    private static void updateEdit(SingleRelicViewPopup self) {
        ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
        if (eyes == null) return;
        float drawX = (float)Settings.WIDTH / 2.0f;
        float drawY = (float)Settings.HEIGHT / 2.0f + RELIC_OFFSET_Y;
        float scale = Settings.scale;
        GooglyEyeEditor.updateEdit(drawX, drawY, scale, -128.f,-128.f,128.f,128.f, eyes, (configs) -> {
            AbstractRelic relic = (AbstractRelic)ReflectionHacks.getPrivate(self, SingleRelicViewPopup.class, "relic");
            GooglyEyeConfig.setRelicEyes(relic.relicId, configs);
        });
    }
}