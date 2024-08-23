package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.client.render.item.RotatingKeyRenderer;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDataComponents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
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

import java.util.Optional;
import java.util.function.Consumer;

public class RotatingKeyItem extends Item implements GeoItem {
    public final ResourceLocation texture, model;
    private final TagKey<Structure> target;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public final RawAnimation NO_TARGET = RawAnimation.begin().thenPlay("no_target");

    // Client data used for rendering
    public final Vector3f fake_target = new Vector3f();
    public Vector3f currentTarget = new Vector3f();
    public Vector3f prevRotation = new Vector3f();
    public Player playerHoldingItem;

    public RotatingKeyItem(Properties pProperties, ResourceLocation model, ResourceLocation texture, ResourceLocation target) {
        super(pProperties);
        this.target = TagKey.create(Registries.STRUCTURE, target);
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
                .triggerableAnim("idle", IDLE)
                .triggerableAnim("no_target", NO_TARGET));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pEntity instanceof Player player) {
            if(player.getMainHandItem() == pStack || player.getOffhandItem() == pStack) {
                if (pLevel instanceof ServerLevel serverLevel) {
                    if (serverLevel.getGameTime() % 20 == 0) {
                        Optional<HolderSet.Named<Structure>> structure = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(this.target);
                        if (structure.isPresent()) {
                            Pair<BlockPos, Holder<Structure>> nearest = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
                                    serverLevel, structure.get(), pEntity.blockPosition(), 25, false);
                            if (nearest != null) {
                                SectionPos section = SectionPos.of(nearest.getFirst());
                                StructureStart start = serverLevel.structureManager().getStartForStructure(section, nearest.getSecond().value(),
                                        serverLevel.getChunk(section.x(), section.z(), ChunkStatus.STRUCTURE_STARTS));
                                if (start != null) {
                                    pStack.set(DSDataComponents.TARGET_POSITION, start.getBoundingBox().getCenter().getCenter().toVector3f());
                                    return;
                                }
                            }
                        }
                        pStack.set(DSDataComponents.TARGET_POSITION, fake_target);
                        triggerAnim(pEntity, GeoItem.getId(pStack), "rotating_key_controller", "no_target");
                    }
                    else
                    {
                        playerHoldingItem = player;
                        currentTarget = pStack.get(DSDataComponents.TARGET_POSITION);
                        if (currentTarget == fake_target || currentTarget == null || currentTarget.length() < 0.1) {
                            triggerAnim(pEntity, GeoItem.getId(pStack), "rotating_key_controller", "no_target");
                            return;
                        }
                        triggerAnim(pEntity, GeoItem.getId(pStack), "rotating_key_controller", "idle");
                    }
                }
            }
        }
    }
}
