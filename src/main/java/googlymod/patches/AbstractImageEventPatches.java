package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;

public class AbstractImageEventPatches {
    @SpirePatch(clz=GenericEventDialog.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
        public static SpireField<String> imgUrl = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractImageEvent.class, method=SpirePatch.CONSTRUCTOR)
    public static class Open {
        public static void Postfix(AbstractImageEvent self, String title, String body, String imgUrl) {
            float drawX = (460.0f - 300.0f) * Settings.scale;
            float drawY = Settings.EVENT_Y + (16.0f - 300.0f) * Settings.scale;
            ArrayList<GooglyEye> eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getEventEyes(imgUrl), drawX, drawY,0,0,0, Settings.scale);
            EyeFields.eyes.set(self.imageEventText, eyes);
            EyeFields.imgUrl.set(self.imageEventText, imgUrl);
        }
    }

    @SpirePatch(clz=GenericEventDialog.class, method="render")
    public static class Render {
        public static void Postfix(GenericEventDialog self, SpriteBatch sb) {
            boolean show = (boolean)ReflectionHacks.getPrivate(self, GenericEventDialog.class, "show");
            if (show && !AbstractDungeon.player.isDead) {
                Color imgColor = (Color)ReflectionHacks.getPrivate(self, GenericEventDialog.class, "imgColor");
                GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(self), sb, imgColor);
            }
        }
    }

    @SpirePatch(clz=GenericEventDialog.class, method="update")
    public static class Update {
        public static void Postfix(GenericEventDialog self) {
            GooglyEyeHelpers.updateEyesForCursor(EyeFields.eyes.get(self));
            // Editing
            if (Settings.isDebug) {
                updateEdit(self);
            }
        }
    }

    private static void updateEdit(GenericEventDialog self) {
        ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
        if (eyes == null) return;
        float drawX = (460.0f - 300.0f) * Settings.scale;
        float drawY = Settings.EVENT_Y + (16.0f - 300.0f) * Settings.scale;
        GooglyEyeEditor.updateEdit(drawX, drawY, Settings.scale, 0,0,600,600, eyes, (configs) -> {
            GooglyEyeConfig.setEventEyes(EyeFields.imgUrl.get(self), configs);
        });
    }
}