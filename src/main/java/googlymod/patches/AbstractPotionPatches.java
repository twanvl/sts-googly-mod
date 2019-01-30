package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;

public class AbstractPotionPatches {
    @SpirePatch(clz=AbstractPotion.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=TopPanel.class, method="updatePotions")
    public static class Update {
        public static void Prefix(TopPanel self) {
            // Note: prefix, because topPanel eats left clicks
            for (AbstractPotion potion : AbstractDungeon.player.potions) {
                if (!(potion instanceof PotionSlot) && potion.isObtained) {
                    ArrayList<GooglyEye> eyes = EyeFields.eyes.get(potion);
                    if (eyes == null) {
                        eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getPotionEyes(potion.ID), potion.posX,potion.posY,0,0,0,potion.scale);
                        EyeFields.eyes.set(potion, eyes);
                    } else {
                        GooglyEyeHelpers.updateEyes(eyes, potion.posX,potion.posY,potion.scale,true,0.15f);
                        if (Settings.isDebug) {
                            GooglyEyeEditor.updateEdit(potion.posX,potion.posY,potion.scale,-32.f,-32.f,32.f,32.f, eyes, (configs) -> {
                                GooglyEyeConfig.setPotionEyes(potion.ID, configs);
                            });
                        }
                    }
                }
            }
        }
    }

    @SpirePatch(clz=AbstractPotion.class, method="render")
    public static class Render {
        public static void Postfix(AbstractPotion potion, SpriteBatch sb) {
            if (potion.isObtained) {
                GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(potion), sb);
            }
        }
    }
}