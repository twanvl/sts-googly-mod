package googlymod.patches;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

import googlymod.helpers.GooglyEye;
import googlymod.helpers.GooglyEyeConfig;

public class SingleCardViewPopupPatches {
    @SpirePatch(clz=SingleCardViewPopup.class, method=SpirePatch.CLASS)
    public static class EyeFields {
        public static SpireField<ArrayList<GooglyEye>> eyes = new SpireField<>(() -> null);
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="open", paramtypez={AbstractCard.class})
    @SpirePatch(clz=SingleCardViewPopup.class, method="open", paramtypez={AbstractCard.class, CardGroup.class})
    public static class Open {
        public static void Postfix(SingleCardViewPopup self, AbstractCard card) {
            float drawX = (float)Settings.WIDTH / 2.0f - 250.0f * Settings.scale;
            float drawY = (float)Settings.HEIGHT / 2.0f - 190.0f * Settings.scale + 136.0f * Settings.scale;
            float scale = Settings.scale;
            ArrayList<GooglyEyeConfig.CardEye> configs = GooglyEyeConfig.getCardEyes(card.cardID);
            ArrayList<GooglyEye> eyes = new ArrayList<>();
            for (GooglyEyeConfig.CardEye config : configs) {
                eyes.add(new GooglyEye(config, drawX, drawY, scale));
            }
            EyeFields.eyes.set(self, eyes);
        }
        public static void Postfix(SingleCardViewPopup self, AbstractCard card, CardGroup group) {
            Postfix(self,card);
        }
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="renderPortrait")
    public static class Render {
        public static void Postfix(SingleCardViewPopup self, SpriteBatch sb) {
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
            if (eyes != null) {
                for (GooglyEye eye : eyes) {
                    eye.render(sb);
                }
            }
        }
    }

    @SpirePatch(clz=SingleCardViewPopup.class, method="update")
    public static class Update {
        public static void Postfix(SingleCardViewPopup self) {
            ArrayList<GooglyEye> eyes = EyeFields.eyes.get(self);
            if (eyes != null) {
                for (GooglyEye eye : eyes) {
                    eye.updateForCursor();
                }
            }
        }
    }
}