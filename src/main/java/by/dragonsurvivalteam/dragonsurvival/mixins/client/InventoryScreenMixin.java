package by.dragonsurvivalteam.dragonsurvival.mixins.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    public InventoryScreenMixin(InventoryMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Unique private static float dragon_survival$storedXAngle = 0;
    @Unique private static float dragon_survival$storedYAngle = 0;

    // This is to angle the dragon entity (including its head) to correctly follow the angle specified when rendering.
    @Redirect(method = "renderEntityInInventory", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;runAsFancy(Ljava/lang/Runnable;)V"))
    private static void dragon_survival$dragonScreenEntityRender(final Runnable runnable, @Local(argsOnly = true) LivingEntity entity) {
        LivingEntity newEntity;

		if (entity instanceof DragonEntity de) {
            newEntity = de.getPlayer();
        } else {
            newEntity = entity;
        }

        DragonStateProvider.getCap(newEntity).ifPresentOrElse(cap -> {
            if (cap.isDragon()) {
                double bodyYaw = cap.getMovementData().bodyYaw;
                double headYaw = cap.getMovementData().headYaw;
                double headPitch = cap.getMovementData().headPitch;
                Vec3 deltaMovement = cap.getMovementData().deltaMovement;
                Vec3 deltaMovementLastFrame = cap.getMovementData().deltaMovementLastFrame;

                cap.getMovementData().bodyYaw = newEntity.yBodyRot;
                cap.getMovementData().headYaw = -Math.toDegrees(dragon_survival$storedXAngle);
                cap.getMovementData().headPitch = -Math.toDegrees(dragon_survival$storedYAngle);
                cap.getMovementData().deltaMovement = Vec3.ZERO;
                cap.getMovementData().deltaMovementLastFrame = Vec3.ZERO;

                ClientDragonRenderer.isOverridingMovementData = true;
                RenderSystem.runAsFancy(runnable);
                ClientDragonRenderer.isOverridingMovementData = false;

                dragon_survival$storedXAngle = 0;
                dragon_survival$storedYAngle = 0;

                cap.getMovementData().bodyYaw = bodyYaw;
                cap.getMovementData().headYaw = headYaw;
                cap.getMovementData().headPitch = headPitch;
                cap.getMovementData().deltaMovement = deltaMovement;
                cap.getMovementData().deltaMovementLastFrame = deltaMovementLastFrame;
            } else {
                RenderSystem.runAsFancy(runnable);
            }
        }, () -> RenderSystem.runAsFancy(runnable));
    }

    // If we are a dragon, we don't want to angle the entire entity when rendering it with a follows mouse command (like vanilla does).
    // Instead, we angle just the dragon's head to follow the given angle. So we modify the angles to eb zero if we are a dragon and capture them to use them later.
    @ModifyArgs(method = "renderEntityInInventoryFollowsMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsAngle(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"))
    private static void dragon_survival$cancelEntityAnglingForDragons(Args args) {
        if (DragonStateProvider.isDragon(args.get(9))) {
            dragon_survival$storedXAngle = args.get(7);
            dragon_survival$storedYAngle = args.get(8);
            args.set(7, 0.f);
            args.set(8, 0.f);
        }
    }
}
