package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.BobEffect;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;

public class AbstractOrbPatches {
    @SpirePatch(clz=AbstractOrb.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractOrb.class, method="update")
    public static class Update {
        public static void Postfix(AbstractOrb orb) {
            BobEffect bobEffect = (BobEffect)ReflectionHacks.getPrivate(orb,AbstractOrb.class,"bobEffect");
            float x = orb.cX;
            float y = orb.cY + bobEffect.y;
            float scale = (float)ReflectionHacks.getPrivate(orb,AbstractOrb.class,"scale");
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(orb);
            if (eyes == null) {
                EyeFields.eyes.set(orb, GooglyEyeHelpers.initEyes(GooglyEyeConfig.getOrbEyes(orb.ID), x,y,0,0,0,scale));
            } else {
                GooglyEyeHelpers.updateEyes(eyes, x,y,scale,true,0.15f);
                if (Settings.isDebug) {
                    float w = 96.0f * Settings.scale / 2;
                    GooglyEyeEditor.updateEdit(x,y,Settings.scale,-w,-w,w,w, eyes, (configs) -> {
                        GooglyEyeConfig.setOrbEyes(orb.ID, configs);
                    });
                }
            }
        }
    }

    @SpirePatch(clz=AbstractPlayer.class, method="render")
    public static class Render {
        public static void Postfix(AbstractPlayer self, SpriteBatch sb) {
            if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom) && !self.isDead) {
                for (AbstractOrb o : self.orbs) {
                    GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(o), sb);
                }
            }
            if (Settings.hideRelics) return;
        }
    }
}