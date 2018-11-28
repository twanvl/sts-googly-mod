package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeHelpers;
import googlymod.helpers.GooglyEyeOnBone;
import googlymod.helpers.GooglyEyeOnBoneEditor;

public class AbstractCreaturePatches {
    @SpirePatch(clz=AbstractCreature.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEyeOnBone>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AbstractMonster.class, method="update")
    @SpirePatch(clz=AbstractPlayer.class, method="update")
    public static class Update {
        public static void Postfix(AbstractCreature creature) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(creature,AbstractCreature.class,"skeleton");
            if (skeleton != null) {
                ArrayList<GooglyEyeOnBone> eyes = EyeFields.eyes.get(creature);
                if (eyes == null) {
                    eyes = GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCreatureEyes(creature.id), skeleton);
                    EyeFields.eyes.set(creature, eyes);
                } else {
                    GooglyEyeHelpers.updateEyes(eyes, skeleton);
                }
                if (Settings.isDebug) {
                    GooglyEyeOnBoneEditor.updateEdit(skeleton, eyes, (configs) -> {
                        GooglyEyeConfig.setCreatureEyes(creature.id, configs);
                    });
                }
            }
        }
    }

    @SpirePatch(clz=AbstractMonster.class, method="render")
    @SpirePatch(clz=AbstractPlayer.class, method="renderPlayerImage")
    public static class Render {
        public static void Postfix(AbstractPlayer creature, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(creature), sb);
        }
        public static void Postfix(AbstractMonster creature, SpriteBatch sb) {
            if (!creature.isDead && !creature.escaped) {
                GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(creature), sb);
            }
        }
    }
}