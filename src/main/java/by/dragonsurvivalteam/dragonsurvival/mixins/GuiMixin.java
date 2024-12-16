package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageModifications;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ModifiersWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(Gui.class)
public class GuiMixin {

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private boolean dragonSurvival$considerClientEffectsForIsEmpty(boolean originalReturnValue) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

        List<ClientEffectProvider> providers = new ArrayList<>();
        providers.addAll(player.getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).map(ModifiersWithDuration::all).orElse(List.of()));
        providers.addAll(player.getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).map(DamageModifications::all).orElse(List.of()));
        providers = providers.stream().filter(ClientEffectProvider::isVisible).toList();

        return originalReturnValue && providers.isEmpty();
    }

    // TODO :: Do we care to determine if effects are beneficial or not? In this UI vanilla puts harmful effects below beneficial ones instead of beside them
    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void dragonSurvival$renderAbilityEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local(ordinal = 0) int numBeneficialEffectsAlreadyRendered) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

        List<ClientEffectProvider> providers = new ArrayList<>();
        providers.addAll(player.getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).map(ModifiersWithDuration::all).orElse(List.of()));
        providers.addAll(player.getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).map(DamageModifications::all).orElse(List.of()));
        providers = providers.stream().filter(ClientEffectProvider::isVisible).toList();
        Gui self = (Gui) (Object) this;

        int numEffects = numBeneficialEffectsAlreadyRendered;
        for (ClientEffectProvider provider : providers) {
            int xPos = guiGraphics.guiWidth();
            int yPos = 1;
            if (Minecraft.getInstance().isDemo()) {
                yPos += 15;
            }

            numEffects++;
            xPos -= 25 * numEffects;
            guiGraphics.blitSprite(((GuiAccessor)self).dragonSurvival$getEffectBackgroundSprite(), xPos, yPos, 24, 24);

            float alpha = 1.0f;
            if (!provider.isInfiniteDuration() && provider.currentDuration() < 200) {
                int l = 10 - provider.currentDuration() / 20;
                alpha = Mth.clamp((float)provider.currentDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                        + Mth.cos((float)provider.currentDuration() * (float) Math.PI / 5.0F) * Mth.clamp((float)l / 10.0F * 0.25F, 0.0F, 0.25F);
            }

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
            guiGraphics.blit(provider.clientData().texture(), xPos + 3, yPos + 3, 0, 0, 0, 18, 18, 18, 18);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
