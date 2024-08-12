package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.client.render.item.RotatingKeyRenderer;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDataComponents;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RotatingKeyItem extends Item implements GeoItem {
    public ResourceLocation texture, model;
    private final ResourceLocation target;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Vector3d prevRotation = new Vector3d();
    private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final RawAnimation NO_TARGET = RawAnimation.begin().thenPlay("no_target");

    public RotatingKeyItem(Properties pProperties, ResourceLocation model, ResourceLocation texture, ResourceLocation target) {
        super(pProperties);
        this.target = target;
        this.model = model;
        this.texture = texture;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RotatingKeyRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new RotatingKeyRenderer();


                return this.renderer;
            }
        });
    }

    @Override
    public double getTick(Object itemStack) {
        return GeoItem.super.getTick(itemStack);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "rotating_key_controller", 10, state -> PlayState.CONTINUE)
                .triggerableAnim("idle", IDLE));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pLevel instanceof ServerLevel serverLevel) {
            if (serverLevel.getGameTime() % 20 == 0) {
                Optional<HolderSet.Named<Structure>> structure = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(TagKey.create(Registries.STRUCTURE, this.target));
                if (structure.isPresent()) {
                    BlockPos nearest = serverLevel.findNearestMapStructure(
                            TagKey.create(Registries.STRUCTURE, this.target),
                            BlockPos.containing(pEntity.getPosition(0.0f)),
                            25,
                            false
                    );
                    if (nearest != null) {
                        pStack.set(DSDataComponents.TARGET_POSITION, nearest.getCenter().toVector3f().sub(0, 40, 0));
                        return;
                    }
                }
                pStack.set(DSDataComponents.TARGET_POSITION, null);
            }
        } else {
            Vector3f src = pStack.get(DSDataComponents.TARGET_POSITION);
            if (src == null || src.length() < 0.1) {
                triggerAnim(pEntity, GeoItem.getId(pStack), "rotating_key_controller", "no_target");
                return;
            }
            Vector3f vectorTo = pEntity.getPosition(0.0f).toVector3f().sub(src);
            triggerAnim(pEntity, GeoItem.getId(pStack), "rotating_key_controller", "idle");
            double pitch = Math.toDegrees(Math.atan2(Math.sqrt(Math.pow(vectorTo.x, 2) + Math.pow(vectorTo.z, 2)), vectorTo.y)) + 180;
            double yaw = Math.toDegrees(Math.atan2(vectorTo.x, vectorTo.z)) - pEntity.getYRot();
            double bank = 0;

            MathParser.setVariable("query.x_rotation", () -> !Double.isNaN(pitch) ? Mth.rotLerp(0.1, prevRotation.x, pitch) : prevRotation.x);
            MathParser.setVariable("query.y_rotation", () -> !Double.isNaN(yaw) ? Mth.rotLerp(0.1, prevRotation.y, yaw) : prevRotation.y);
            MathParser.setVariable("query.z_rotation", () -> 0);
            prevRotation = new Vector3d(pitch, yaw, bank);
        }
    }
}
