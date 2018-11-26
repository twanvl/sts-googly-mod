package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;
import javassist.CtBehavior;

public class CharacterSelectScreenPatches {
    @SpirePatch(clz=CharacterSelectScreen.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=CharacterOption.class, method="updateHitbox")
    public static class SelectOption {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(CharacterOption self) {
            float bg_y_offset = Settings.isSixteenByTen ? 0 : (float)ReflectionHacks.getPrivate(CardCrawlGame.mainMenuScreen.charSelectScreen, CharacterSelectScreen.class, "bg_y_offset");
            float drawX = (float)Settings.WIDTH / 2.0f - 960 * Settings.scale;
            float drawY = (float)Settings.HEIGHT / 2.0f - 600 * Settings.scale + bg_y_offset;
            String playerClass = self.c.chosenClass.toString();
            ArrayList<GooglyEye> eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCharSelectEyes(playerClass), drawX, drawY,0,0,0, Settings.scale);
            EyeFields.eyes.set(CardCrawlGame.mainMenuScreen.charSelectScreen, eyes);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ImageMaster.class, "loadImage");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch(clz=CharacterSelectScreen.class, method="render")
    public static class Render {
        public static void Postfix(CharacterSelectScreen self, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(self), sb);
        }
    }

    @SpirePatch(clz=CharacterSelectScreen.class, method="update")
    public static class Update {
        public static void Postfix(CharacterSelectScreen self) {
            float bg_y_offset = Settings.isSixteenByTen ? 0 : (float)ReflectionHacks.getPrivate(self, CharacterSelectScreen.class, "bg_y_offset");
            float drawX = (float)Settings.WIDTH / 2.0f - 960 * Settings.scale;
            float drawY = (float)Settings.HEIGHT / 2.0f - 600 * Settings.scale + bg_y_offset;
            GooglyEyeHelpers.updateEyesPosition(EyeFields.eyes.get(self), drawX, drawY, Settings.scale);
            GooglyEyeHelpers.updateEyesForCursor(EyeFields.eyes.get(self));
            // Editing
            if (Settings.isDebug) {
                updateEdit(self);
            }
        }
    }

    private static void updateEdit(CharacterSelectScreen self) {
        ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
        if (eyes == null) return;
        float bg_y_offset = Settings.isSixteenByTen ? 0 : (float)ReflectionHacks.getPrivate(self, CharacterSelectScreen.class, "bg_y_offset");
        float drawX = (float)Settings.WIDTH / 2.0f - 960 * Settings.scale;
        float drawY = (float)Settings.HEIGHT / 2.0f - 600 * Settings.scale + bg_y_offset;
        String playerClass = CardCrawlGame.chosenCharacter.toString();
        GooglyEyeEditor.updateEdit(drawX, drawY, Settings.scale, 0,440,1920,1200, eyes, (configs) -> {
            GooglyEyeConfig.setCharSelectEyes(playerClass, configs);
        });
    }
}