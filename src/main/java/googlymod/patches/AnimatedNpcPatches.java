package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.Skeleton;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.shop.Merchant;

import basemod.ReflectionHacks;
import googlymod.helpers.GooglyEyeConfig;
import googlymod.helpers.GooglyEyeHelpers;
import googlymod.helpers.GooglyEyeOnBone;
import googlymod.helpers.GooglyEyeOnBoneEditor;

public class AnimatedNpcPatches {
    @SpirePatch(clz=AnimatedNpc.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<String> skeletonUrl = new SpireField<>(() -> null);
        public static SpireField<ArrayList<GooglyEyeOnBone>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=AnimatedNpc.class, method=SpirePatch.CONSTRUCTOR)
    public static class Init {
        public static void Postfix(AnimatedNpc npc, float x, float y, String atlasUrl, String skeletonUrl, String trackName) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(npc,AnimatedNpc.class,"skeleton");
            EyeFields.eyes.set(npc, GooglyEyeHelpers.initEyes(GooglyEyeConfig.getCreatureEyes(skeletonUrl), skeleton));
            EyeFields.skeletonUrl.set(npc, skeletonUrl);
        }
    }

    @SpirePatch(clz=NeowEvent.class, method="update")
    @SpirePatch(clz=Merchant.class, method="update")
    public static class Update {
        public static void Postfix(NeowEvent self) {
            doUpdate((AnimatedNpc)ReflectionHacks.getPrivate(self,NeowEvent.class,"npc"));
        }
        public static void Postfix(Merchant self) {
            doUpdate((AnimatedNpc)ReflectionHacks.getPrivate(self,Merchant.class,"anim"));
        }
        private static void doUpdate(AnimatedNpc npc) {
            Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(npc,AnimatedNpc.class,"skeleton");
            GooglyEyeHelpers.updateEyes(EyeFields.eyes.get(npc), skeleton);
            if (Settings.isDebug) {
                GooglyEyeOnBoneEditor.updateEdit(skeleton, EyeFields.eyes.get(npc), (configs) -> {
                    GooglyEyeConfig.setCreatureEyes(EyeFields.skeletonUrl.get(npc), configs);
                });
            }
        }
    }

    @SpirePatch(clz=AnimatedNpc.class, method="render", paramtypez={SpriteBatch.class})
    public static class Render {
        public static void Postfix(AnimatedNpc npc, SpriteBatch sb) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(npc), sb);
        }
    }
    @SpirePatch(clz=AnimatedNpc.class, method="render", paramtypez={SpriteBatch.class, Color.class})
    public static class Render2 {
        public static void Postfix(AnimatedNpc npc, SpriteBatch sb, Color color) {
            GooglyEyeHelpers.renderEyes(EyeFields.eyes.get(npc), sb);
        }
    }
}