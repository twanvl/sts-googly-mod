package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Byrd;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeEditor;
import googlymod.helpers.GooglyEyeHelpers;
import googlymod.helpers.GooglyEyeOnBone;
import googlymod.helpers.GooglyEyeOnBoneEditor;

public class AbstractCreaturePatches {
    @SpirePatch(clz=AbstractCreature.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<Skeleton> skeletonForEyes = new SpireField<>(() -> null);
        public static SpireField<ArrayList<? extends GooglyEye>> eyes = new SpireField<>(() -> null);
        public static SpireField<ArrayList<GooglyEyeOnBone>> eyesOnBone = new SpireField<>(() -> null);
        public static SpireField<ArrayList<GooglyEye>> eyesOther = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractMonster.class, method="update")
    @SpirePatch(clz=AbstractPlayer.class, method="update")
    public static class Update {
        public static void Postfix(AbstractPlayer creature) {
            go(creature, creature.chosenClass.toString());
        }
        public static void Postfix(AbstractMonster creature) {
            go(creature, creature.id);
        }
        public static void go(AbstractCreature creature, String id) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(creature,AbstractCreature.class,"skeleton");
            if (skeleton != null) {
                ArrayList<GooglyEyeOnBone> eyes = EyeFields.eyesOnBone.get(creature);
                if (eyes == null || skeleton != EyeFields.skeletonForEyes.get(creature)) {
                    eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCreatureEyes(id), skeleton);
                    EyeFields.eyes.set(creature, eyes);
                    EyeFields.eyesOnBone.set(creature, eyes);
                    EyeFields.skeletonForEyes.set(creature, skeleton);
                } else {
                    GooglyEyeHelpers.updateEyes(eyes, skeleton);
                }
                if (Settings.isDebug) {
                    GooglyEyeOnBoneEditor.updateEdit(skeleton, eyes, (configs) -> {
                        GooglyEyeConfig.setCreatureEyes(id, configs);
                    });
                }
            } else {
                ArrayList<GooglyEye> eyes = EyeFields.eyesOther.get(creature);
                float drawX = creature.drawX + creature.animX;
                float drawY = creature.drawY + creature.animY + AbstractDungeon.sceneOffsetY;
                if (eyes == null) {
                    eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getStaticCreatureEyes(id), drawX, drawY,0,0,0, Settings.scale);
                    EyeFields.eyes.set(creature, eyes);
                    EyeFields.eyesOther.set(creature, eyes);
                } else {
                    GooglyEyeHelpers.updateEyes(eyes, drawX, drawY, Settings.scale, true, 0.2f);
                }
                if (Settings.isDebug) {
                    GooglyEyeEditor.updateEdit(drawX,drawY,Settings.scale,0,0,creature.hb.width,creature.hb.height, eyes, (configs) -> {
                        GooglyEyeConfig.setStaticCreatureEyes(id, configs);
                    });
                }
            }
        }
    }

    @SpirePatch(clz=AbstractMonster.class, method="render")
    @SpirePatch(clz=AbstractPlayer.class, method="renderPlayerImage")
    public static class Render {
        public static void Postfix(AbstractPlayer creature, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(creature), sb, creature.tint.color);
        }
        public static void Postfix(AbstractMonster creature, SpriteBatch sb) {
            if (!creature.isDead && !creature.escaped) {
                GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(creature), sb, creature.tint.color);
            }
        }
    }

    @SpirePatch(clz=Byrd.class, method="changeState")
    public static class ChangeState {
        public static void Postfix(AbstractCreature creature, String stateName) {
            // Re initializes the googly eyes after the state has changed
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(creature,AbstractCreature.class,"skeleton");
            ArrayList<GooglyEyeOnBone> eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCreatureEyes(creature.id), skeleton);
            EyeFields.eyes.set(creature, eyes);
            EyeFields.eyesOnBone.set(creature, eyes);
        }
    }
}