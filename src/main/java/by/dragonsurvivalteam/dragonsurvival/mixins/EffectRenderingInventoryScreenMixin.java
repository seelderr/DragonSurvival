package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageModifications;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ModifiersWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I"))
    private int dragonSurvival$adjustRenderedEffectsSize(final int original) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        int additions = 0;

        additions += player.getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).map(ModifiersWithDuration::size).orElse(0);
        additions += player.getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).map(DamageModifications::size).orElse(0);

        return original + additions;
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;onScreenPotionSize(Lnet/minecraft/client/gui/screens/Screen;IZI)Lnet/neoforged/neoforge/client/event/ScreenEvent$RenderInventoryMobEffects;", shift = At.Shift.BY, by = 2))
    private void dragonSurvival$storeEvent(final CallbackInfo callback, @Local final ScreenEvent.RenderInventoryMobEffects event, @Share("stored_event") final LocalRef<ScreenEvent.RenderInventoryMobEffects> storedEvent) {
        storedEvent.set(event);
    }

    @Unique // TODO :: takes interface like 'ClientProvider' which returns resource location for icon / uuid for player name / component for text
    private void dragonSurvival$renderAbilityBackgrounds(final GuiGraphics graphics, int renderX, int yOffset, int initialYOffset, int providerAmount, boolean isCompact) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;
        int width = isCompact ? 32 : 120;

        for (int i = 0; i < providerAmount; i++) {
            graphics.blitSprite(((EffectRenderingInventoryScreenAccessor)self).dragonSurvival$getEffectBackgroundLargeSprite(), renderX, topPos, width, 32);
            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityIcons(final GuiGraphics graphics, int renderX, int yOffset, int initialYOffset, final List<ClientEffectProvider> providers, boolean isCompact) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ClientEffectProvider provider : providers) {
            // FIXME :: can this even be safely done if we don't know how the texture is stored etc.?
            //  maybe have dedicated texture icons for the effect type?
            //  for 'blitSprite' the texture would need to be in the '/gui' sub-directory
            graphics.blit(provider.clientData().texture(), renderX + (isCompact ? 6 : 7), topPos + 7, 0, 0, 0, 0);
            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityLabels(final GuiGraphics graphics, int renderX, int yOffset, int initialYOffset, final List<ClientEffectProvider> providers) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ClientEffectProvider provider : providers) {
            // TODO: have a generic name per client effect provider type?
//            Component name = Component.translatable(provider.id().getPath());
//            graphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), name, renderX + 10 + 18, topPos+ 6, /* ChatFormatting.WHITE */ 16777215);

            Component duration = Component.literal(Integer.toString(provider.currentDuration()));
            graphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), duration, renderX + 10 + 18, topPos + 6 + 10, 8355711);
            topPos += yOffset;
        }
    }

    @Inject(method = "renderEffects", at = @At(value = "TAIL"))
    private void dragonSurvival$renderAbilityEffects(final GuiGraphics graphics, int mouseX, int mouseY, final CallbackInfo callback, @Share("stored_event") final LocalRef<ScreenEvent.RenderInventoryMobEffects> storedEvent) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int offset = ((AbstractContainerScreenAccessor) self).dragonSurvival$getLeftPos() + ((AbstractContainerScreenAccessor) self).dragonSurvival$imageWidth() + 2;
        int width = self.width;

        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);

        List<ClientEffectProvider> providers = new ArrayList<>();
        providers.addAll(player.getExistingData(DSDataAttachments.MODIFIERS_WITH_DURATION).map(ModifiersWithDuration::all).orElse(List.of()));
        providers.addAll(player.getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).map(DamageModifications::all).orElse(List.of()));

        if (!providers.isEmpty() && width >= 32) {
            boolean isCompact = width < 120;
            ScreenEvent.RenderInventoryMobEffects event = storedEvent.get();

            if (event == null) {
                event = net.neoforged.neoforge.client.ClientHooks.onScreenPotionSize(self, width, isCompact, offset);
            }

            if (event.isCanceled()) {
                return;
            }

            isCompact = event.isCompact();
            offset = event.getHorizontalOffset();
            int yOffset = 33;
            Collection<MobEffectInstance> mobEffects = player.getActiveEffects();
            int totalElementsToRender = mobEffects.size() + providers.size();

            if (totalElementsToRender > 5) {
                yOffset = 132 / (totalElementsToRender - 1);
            }

            int renderedElements = mobEffects.stream().filter(net.neoforged.neoforge.client.ClientHooks::shouldRenderEffect).sorted().toList().size();
            int initialYOffset = yOffset * renderedElements;

            dragonSurvival$renderAbilityBackgrounds(graphics, offset, yOffset, initialYOffset, providers.size(), isCompact);
            dragonSurvival$renderAbilityIcons(graphics, offset, yOffset, initialYOffset, providers, isCompact);

            if (isCompact) {
                // TODO: Potentially render extra tooltip data anyways?
                this.dragonSurvival$renderAbilityLabels(graphics, offset, yOffset, initialYOffset, providers);
            } else if (mouseX >= offset && mouseX <= offset + 33) {
                int topPos = ((AbstractContainerScreenAccessor) self).dragonSurvival$getTopPos();
                ClientEffectProvider hoveredProvider = null;

                for (ClientEffectProvider provider : providers) {
                    if (mouseY >= topPos && mouseY <= topPos + yOffset) {
                        hoveredProvider = provider;
                    }

                    topPos += yOffset;
                }

                if (hoveredProvider != null) {
                    List<Component> list = List.of(
                            // TODO :: what should be part of this tooltip?
                            //  duration would be too dynamic since it gets set once on the server
                            hoveredProvider.clientData().tooltip(),
                            // TODO: Add a proper translation key here
                            //  see other comment in the method above about the name of the effect
//                            Component.translatable(hoveredProvider.id().getPath()),
                            Component.literal(Integer.toString(hoveredProvider.currentDuration()))
                    );

                    graphics.renderTooltip(((ScreenAccessor) self).dragonSurvival$getFont(), list, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
}
