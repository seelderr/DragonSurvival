package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ModifierWithDuration;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I"))
    private int respectAbilityModifiersWhenCountingEffects(final int original) {
        List<ModifierWithDuration> modifiersWithDuration = Minecraft.getInstance().player.getData(DSDataAttachments.MODIFIERS_WITH_DURATION).modifiersWithDuration;
        return original + (modifiersWithDuration != null ? modifiersWithDuration.size() : 0);
    }

    @ModifyExpressionValue(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/ScreenEvent$RenderInventoryMobEffects;isCanceled()Z"))
    private boolean storeEventData(final boolean original, @Local ScreenEvent.RenderInventoryMobEffects event, @Share("event") LocalRef<ScreenEvent.RenderInventoryMobEffects> storedEvent, @Share("eventWasStored") LocalBooleanRef eventWasStored) {
        storedEvent.set(event);
        eventWasStored.set(true);
        return original;
    }

    @Unique private void dragonSurvival$renderAbilityBackgrounds(GuiGraphics guiGraphics, int renderX, int yOffset, int initialYOffset, Iterable<ModifierWithDuration> modifiers, boolean isSmall) {
        EffectRenderingInventoryScreen self = (EffectRenderingInventoryScreen) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ModifierWithDuration modifier : modifiers) {
            if (isSmall) {
                guiGraphics.blitSprite(((EffectRenderingInventoryScreenAccessor)self).dragonSurvival$getEffectBackgroundLargeSprite(), renderX, topPos, 120, 32);
            } else {
                guiGraphics.blitSprite(((EffectRenderingInventoryScreenAccessor)self).dragonSurvival$getEffectBackgroundSmallSprite(), renderX, topPos, 32, 32);
            }

            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityIcons(GuiGraphics guiGraphics, int renderX, int yOffset, int initialYOffset, Iterable<ModifierWithDuration> modifiers, boolean isSmall) {
        EffectRenderingInventoryScreen self = (EffectRenderingInventoryScreen) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ModifierWithDuration modifier : modifiers) {
            // TODO: Actually parse a texture here
            ResourceLocation placeHolderTexture = ResourceLocation.fromNamespaceAndPath(DragonSurvival.MODID, "textures/gui/growth/cave/adult");
            guiGraphics.blitSprite(placeHolderTexture, renderX + (isSmall ? 6 : 7), topPos + 7, 0, 18, 18);
            topPos += yOffset;
        }
    }

    @Unique private void dragonSurvival$renderAbilityLabels(GuiGraphics guiGraphics, int renderX, int yOffset, int initialYOffset, Iterable<ModifierWithDuration> modifiers) {
        EffectRenderingInventoryScreen self = (EffectRenderingInventoryScreen) (Object) this;
        int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos() + initialYOffset;

        for (ModifierWithDuration modifier : modifiers) {
            // TODO: Add a proper translation key here
            Component name = Component.translatable(modifier.id().getPath());
            guiGraphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), name, renderX + 10 + 18, topPos+ 6, 16777215);
            Component duration = Component.literal(Integer.toString(modifier.currentDuration()));
            guiGraphics.drawString(((ScreenAccessor)self).dragonSurvival$getFont(), duration, renderX + 10 + 18, topPos + 6 + 10, 8355711);
            topPos += yOffset;
        }
    }

    @Inject(method = "renderEffects", at = @At(value = "TAIL"))
    private void renderAbilityModifiers(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci, @Share("event")LocalRef<ScreenEvent.RenderInventoryMobEffects> storedEvent, @Share("eventWasStored") LocalBooleanRef eventWasStored) {
        EffectRenderingInventoryScreen self = (EffectRenderingInventoryScreen) (Object) this;
        int offset = ((AbstractContainerScreenAccessor)self).dragonSurvival$getLeftPos() + ((AbstractContainerScreenAccessor)self).dragonSurvival$imageWidth() + 2;
        int width = self.width;
        List<ModifierWithDuration> modifiersWithDuration = Minecraft.getInstance().player.getData(DSDataAttachments.MODIFIERS_WITH_DURATION).modifiersWithDuration;
        if (modifiersWithDuration != null && !modifiersWithDuration.isEmpty() && width >= 32) {
            boolean notCompact = width >= 120;
            ScreenEvent.RenderInventoryMobEffects event;
            if(eventWasStored.get()) {
                event = storedEvent.get();
            } else {
                event = net.neoforged.neoforge.client.ClientHooks.onScreenPotionSize(self, width, !notCompact, offset);
            }
            if (event.isCanceled()) return;
            notCompact = !event.isCompact();
            offset = event.getHorizontalOffset();
            int yOffset = 33;
            if (modifiersWithDuration.size() > 5) {
                yOffset = 132 / (modifiersWithDuration.size() - 1);
            }

            int numRenderedEffectElements = Minecraft.getInstance().player.getActiveEffects().stream().filter(net.neoforged.neoforge.client.ClientHooks::shouldRenderEffect).sorted().collect(java.util.stream.Collectors.toList()).size();
            int initialYOffset = yOffset * numRenderedEffectElements;
            this.dragonSurvival$renderAbilityBackgrounds(guiGraphics, offset, yOffset, initialYOffset, modifiersWithDuration, notCompact);
            this.dragonSurvival$renderAbilityIcons(guiGraphics, offset, yOffset, initialYOffset, modifiersWithDuration, notCompact);
            if (notCompact) {
                // TODO: Potentially render extra tooltip data anyways?
                this.dragonSurvival$renderAbilityLabels(guiGraphics, offset, yOffset, initialYOffset, modifiersWithDuration);
            } else if (mouseX >= offset && mouseX <= offset + 33) {
                int topPos = ((AbstractContainerScreenAccessor)self).dragonSurvival$getTopPos();
                ModifierWithDuration hoveredModifier = null;

                for (ModifierWithDuration modifier : modifiersWithDuration) {
                    if (mouseY >= topPos && mouseY <= topPos + yOffset) {
                        hoveredModifier = modifier;
                    }

                    topPos += yOffset;
                }

                if (hoveredModifier != null) {
                    List<Component> list = List.of(
                            // TODO: Add a proper translation key here
                            Component.translatable(hoveredModifier.id().getPath()),
                            Component.literal(Integer.toString(hoveredModifier.currentDuration()))
                    );
                    guiGraphics.renderTooltip(((ScreenAccessor)self).dragonSurvival$getFont(), list, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
}
