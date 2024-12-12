package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageModifications;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ModifiersWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
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

    @Unique
    private List<Rect2i> dragonSurvival$areasBlockedByModifierUIForJEI = new ArrayList<>();

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
            graphics.blitSprite(isCompact ? ((EffectRenderingInventoryScreenAccessor)self).dragonSurvival$getEffectBackgroundSmallSprite() : ((EffectRenderingInventoryScreenAccessor)self).dragonSurvival$getEffectBackgroundLargeSprite(), renderX, topPos, width, 32);
            dragonSurvival$areasBlockedByModifierUIForJEI.add(new Rect2i(renderX, topPos, width, 32));
            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityIcons(final GuiGraphics graphics, int renderX, int yOffset, int initialYOffset, final List<ClientEffectProvider> providers, boolean isCompact) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ClientEffectProvider provider : providers) {
            // FIXME :: can this even be safely done if we don't know how the texture is stored etc.?
            graphics.blit(provider.clientData().texture(), renderX + (isCompact ? 6 : 7), topPos + 7, 0, 0, 0, 18, 18, 18, 18);
            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityLabels(final GuiGraphics graphics, int renderX, int yOffset, int initialYOffset, final List<ClientEffectProvider> providers) {
        EffectRenderingInventoryScreen<?> self = (EffectRenderingInventoryScreen<?>) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ClientEffectProvider provider : providers) {
            Component name = Component.translatable(Translation.Type.MODIFIER.wrap(provider.getId().getPath()));
            graphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), name, renderX + 10 + 18, topPos+ 6, /* ChatFormatting.WHITE */ 16777215);
            Component duration = dragonSurvival$formatDuration(provider, Minecraft.getInstance().level.tickRateManager().tickrate());
            graphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), duration, renderX + 10 + 18, topPos + 6 + 10, 8355711);
            topPos += yOffset;
        }
    }

    @Unique private static Component dragonSurvival$formatDuration(final ClientEffectProvider effect, float ticksPerSecond) {
        if (effect.isInfiniteDuration()) {
            return Component.translatable("effect.duration.infinite");
        } else {
            int i = Mth.floor((float)effect.currentDuration());
            return Component.literal(StringUtil.formatTickDuration(i, ticksPerSecond));
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
        dragonSurvival$areasBlockedByModifierUIForJEI.clear();

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

            if (!isCompact) {
                // TODO: Potentially render extra tooltip data anyways?
                this.dragonSurvival$renderAbilityLabels(graphics, offset, yOffset, initialYOffset, providers);
            }

            if (mouseX >= offset && mouseX <= offset + (isCompact ? 32 : 120)) {
                int topPos = ((AbstractContainerScreenAccessor) self).dragonSurvival$getTopPos() + initialYOffset;
                ClientEffectProvider hoveredProvider = null;

                for (ClientEffectProvider provider : providers) {
                    if (mouseY >= topPos && mouseY <= topPos + yOffset) {
                        hoveredProvider = provider;
                    }

                    topPos += yOffset;
                }

                if (hoveredProvider != null) {
                    List<Component> list = new ArrayList<>();
                    if(isCompact) {
                        list.add(Component.translatable(Translation.Type.MODIFIER.wrap(hoveredProvider.getId().getPath())));
                        list.add(dragonSurvival$formatDuration(hoveredProvider, Minecraft.getInstance().level.tickRateManager().tickrate()));
                    }

                    if(!Objects.equals(hoveredProvider.clientData().tooltip(), Component.empty())) {
                        list.add(hoveredProvider.clientData().tooltip());
                    }

                    graphics.renderTooltip(((ScreenAccessor) self).dragonSurvival$getFont(), list, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
}
