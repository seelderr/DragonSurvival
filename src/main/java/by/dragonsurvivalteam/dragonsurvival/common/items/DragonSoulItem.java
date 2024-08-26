package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.PlayerLoginHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DragonSoulItem extends Item {
    public DragonSoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn){
        if(DragonStateProvider.isDragon(playerIn) || playerIn.getItemInHand(handIn).has(DataComponents.CUSTOM_DATA)) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        } else {
            return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
        }
    }

    private static int getCustomModelData(CompoundTag tag) {
        AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));

        int customModelData = 0;
        if(dragonType != null) {
            customModelData = switch (dragonType.toString()) {
                case "forest" -> 1;
                case "cave" -> 2;
                case "sea" -> 3;
                default -> 0;
            };
        }

        return customModelData;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
        // Store the player's dragon data in the item's NBT
        if(pLivingEntity instanceof Player playerIn) {
            DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(playerIn);
            if(pStack.has(DataComponents.CUSTOM_DATA)) {
                if(handler.isDragon()) {
                    // Swap the player's dragon data with the item's NBT
                    CompoundTag storedDragonData = pStack.get(DataComponents.CUSTOM_DATA).copyTag();
                    CompoundTag currentDragonData = handler.serializeNBT(pLevel.registryAccess(), true);
                    handler.deserializeNBT(pLevel.registryAccess(), storedDragonData, true);
                    pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(currentDragonData));
                    pStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getCustomModelData(currentDragonData)));
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                } else {
                    CompoundTag tag = pStack.get(DataComponents.CUSTOM_DATA).copyTag();
                    handler.deserializeNBT(pLevel.registryAccess(), tag, true);
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                    pStack.set(DataComponents.CUSTOM_DATA, null);
                    pStack.set(DataComponents.CUSTOM_MODEL_DATA, null);
                }
            } else {
                if(handler.isDragon()) {
                    CompoundTag currentDragonData = handler.serializeNBT(pLevel.registryAccess(), true);
                    pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(currentDragonData));
                    pStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getCustomModelData(currentDragonData)));
                    handler.revertToHumanForm(playerIn, true);
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                }
            }

            pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.ENDER_DRAGON_GROWL, pLivingEntity.getSoundSource(), 1.0F, 1.0F);

            // Add a bunch of random poof particles
            for(int i = 0; i < 10; i++) {
                pLevel.addParticle(ParticleTypes.POOF, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLivingEntity.getY() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
            }

            // If we transformed into a dragon, spawn particles based off of the dragon's type
            if(handler.isDragon()) {
                switch(handler.getType().toString()) {
                    case "forest" -> {
                        for(int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.HAPPY_VILLAGER, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                    case "cave" -> {
                        for(int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.SMOKE, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                    case "sea" -> {
                        for(int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.FALLING_WATER, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                }
            }

            return pStack;
        }

        return pStack;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        // See registerItemExtensions to understand how this UseAnim is animated
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity pEntity) {
        return Functions.secondsToTicks(2);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag){
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        if (pStack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.CUSTOM_DATA).copyTag();
            pTooltipComponents.add(Component.translatable("ds.description.dragon_soul"));
            AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));

            MutableComponent dragonName;
            if(dragonType != null) {
                dragonName = switch (dragonType.toString()) {
                    case "forest" -> Component.translatable("ds.skill.forest_dragon");
                    case "cave" -> Component.translatable("ds.skill.cave_dragon");
                    case "sea" -> Component.translatable("ds.skill.sea_dragon");
                    default -> Component.literal("No translation key for dragon type!");
                };
            } else {
                dragonName = Component.literal("No translation key for dragon type!");
            }

            double size = tag.getDouble("size");
            DragonLevel level = DragonStateHandler.getLevel(size);
            MutableComponent dragonGrowthStage = switch (level) {
                case DragonLevel.NEWBORN -> Component.translatable("ds.level.newborn");
                case DragonLevel.YOUNG -> Component.translatable("ds.level.young");
                case DragonLevel.ADULT -> Component.translatable("ds.level.adult");
            };

            pTooltipComponents.add(Component.translatable("ds.description.dragon_soul_info", dragonName, dragonGrowthStage, String.format("%.0f", size)));

            if(tag.getBoolean("spinLearned")) {
                pTooltipComponents.add(Component.translatable("ds.description.dragon_soul_has_spin"));
            }

            if(tag.getBoolean("hasWings")) {
                pTooltipComponents.add(Component.translatable("ds.description.dragon_soul_has_fly"));
            }
        } else {
            pTooltipComponents.add(Component.translatable("ds.description.dragon_soul_empty"));
        }
    }

    public static String getType(final ItemStack soul) {
        CustomData data = soul.get(DataComponents.CUSTOM_DATA);

        if (data != null) {
            // No need to copy the tag for this (since it's not being modified)
            return data.getUnsafe().getString("type");
        }

        return "";
    }

    @Override
    public void onUseTick(@NotNull final Level level, @NotNull final LivingEntity livingEntity, @NotNull final ItemStack soul, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, soul, remainingUseDuration);
        DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(livingEntity);
        String type = getType(soul);

        if (type.isBlank() && handler.isDragon()) {
            type = handler.getType().getTypeNameLowerCase();
        }

        SoundEvent sound = switch (type) {
            case "forest" -> DSSounds.FOREST_BREATH_END.get();
            case "cave" -> DSSounds.FIRE_BREATH_END.get();
            case "sea" -> DSSounds.STORM_BREATH_END.get();
            default -> null;
        };

        if (sound != null) {
            livingEntity.playSound(sound, (float) (0.3 + 0.3F * livingEntity.getRandom().nextInt(2)), livingEntity.getRandom().nextFloat() - livingEntity.getRandom().nextFloat() * 0.2F + 1.0F);
        }
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack pStack) {
        if(pStack.has(DataComponents.CUSTOM_DATA)) {
            AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(pStack.get(DataComponents.CUSTOM_DATA).copyTag().getString("type"));
            switch(dragonType.toString()) {
                case "forest" -> {
                    return "item.dragonsurvival.forest_dragon_soul";
                }
                case "cave" -> {
                    return "item.dragonsurvival.cave_dragon_soul";
                }
                case "sea" -> {
                    return "item.dragonsurvival.sea_dragon_soul";
                }
                default -> {
                    return "No translation key for dragon type!";
                }
            }
        } else {
            return "item.dragonsurvival.empty_dragon_soul";
        }
    }
}
