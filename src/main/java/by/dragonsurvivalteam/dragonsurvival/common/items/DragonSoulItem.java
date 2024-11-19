package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSSounds;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.PlayerLoginHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.List;

public class DragonSoulItem extends Item {
    @Translation(type = Translation.Type.MISC, comments = "Empty Dragon Soul")
    private static final String EMPTY_DRAGON_SOUL = Translation.Type.ITEM.wrap("empty_dragon_soul");

    @Translation(type = Translation.Type.MISC, comments = "Cave Dragon Soul")
    private static final String CAVE_DRAGON_SOUL = Translation.Type.ITEM.wrap("cave_dragon_soul");

    @Translation(type = Translation.Type.MISC, comments = "Sea Dragon Soul")
    private static final String SEA_DRAGON_SOUL = Translation.Type.ITEM.wrap("sea_dragon_soul");

    @Translation(type = Translation.Type.MISC, comments = "Forest Dragon Soul")
    private static final String FOREST_DRAGON_SOUL = Translation.Type.ITEM.wrap("forest_dragon_soul");

    @Translation(type = Translation.Type.MISC, comments = "■§7 This vessel holds the dragon's soul. Use it to become a dragon. Replaces your current stats if you are a dragon.\n")
    private static final String DESCRIPTION = Translation.Type.DESCRIPTION.wrap("dragon_soul");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Type:§r %s\n§6■ Growth Stage:§r %s\n§6■ Size:§r %s\n")
    private static final String INFO = Translation.Type.DESCRIPTION.wrap("dragon_soul.info");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Can Spin§r")
    private static final String HAS_SPIN = Translation.Type.DESCRIPTION.wrap("dragon_soul.has_spin");

    @Translation(type = Translation.Type.MISC, comments = "§6■ Can Fly§r")
    private static final String HAS_FLIGHT = Translation.Type.DESCRIPTION.wrap("dragon_soul.has_flight");

    @Translation(type = Translation.Type.MISC, comments = "■§7 An empty dragon's soul. With this item, you can store all your dragon's characteristics. After using it, you become human.")
    private static final String IS_EMPTY = Translation.Type.DESCRIPTION.wrap("dragon_soul.empty");

    public DragonSoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (DragonStateProvider.isDragon(playerIn) || playerIn.getItemInHand(handIn).has(DataComponents.CUSTOM_DATA)) {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        } else {
            return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
        }
    }

    private static int getCustomModelData(CompoundTag tag) {
        AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));

        if (dragonType == null) {
            return 0;
        }

        return switch (dragonType) {
            case ForestDragonType ignored -> 1;
            case CaveDragonType ignored -> 2;
            case SeaDragonType ignored -> 3;
            default -> 0;
        };
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
        // Store the player's dragon data in the item's NBT
        if (pLivingEntity instanceof Player playerIn) {
            if (playerIn instanceof ServerPlayer serverPlayer) {
                DSAdvancementTriggers.USE_DRAGON_SOUL.get().trigger(serverPlayer);
            }
            DragonStateHandler handler = DragonStateProvider.getData(playerIn);
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                if (handler.isDragon()) {
                    // Swap the player's dragon data with the item's NBT
                    CompoundTag storedDragonData = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                    CompoundTag currentDragonData = handler.serializeNBT(pLevel.registryAccess(), true);
                    handler.deserializeNBT(pLevel.registryAccess(), storedDragonData, true);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(currentDragonData));
                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getCustomModelData(currentDragonData)));
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                } else {
                    CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                    handler.deserializeNBT(pLevel.registryAccess(), tag, true);
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                    stack.set(DataComponents.CUSTOM_DATA, null);
                    stack.set(DataComponents.CUSTOM_MODEL_DATA, null);
                }
            } else {
                if (handler.isDragon()) {
                    CompoundTag currentDragonData = handler.serializeNBT(pLevel.registryAccess(), true);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(currentDragonData));
                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(getCustomModelData(currentDragonData)));
                    handler.revertToHumanForm(playerIn, true);
                    PlayerLoginHandler.syncCompleteAll(playerIn);
                }
            }

            pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), SoundEvents.ENDER_DRAGON_GROWL, pLivingEntity.getSoundSource(), 1.0F, 1.0F);

            // Add a bunch of random poof particles
            for (int i = 0; i < 10; i++) {
                pLevel.addParticle(ParticleTypes.POOF, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLivingEntity.getY() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
            }

            // If we transformed into a dragon, spawn particles based off of the dragon's type
            if (handler.isDragon()) {
                switch (handler.getType()) {
                    case ForestDragonType ignored -> {
                        for (int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.HAPPY_VILLAGER, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                    case CaveDragonType ignored -> {
                        for (int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.SMOKE, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                    case SeaDragonType ignored -> {
                        for (int i = 0; i < 30; i++) {
                            pLevel.addParticle(ParticleTypes.FALLING_WATER, pLivingEntity.getX() + (pLevel.random.nextDouble() - 0.5D) * 2D, pLivingEntity.getY() + pLevel.random.nextDouble() * 2D, pLivingEntity.getZ() + (pLevel.random.nextDouble() - 0.5D) * 2D, (pLevel.random.nextDouble() - 0.5D) * 0.5D, pLevel.random.nextDouble() * 0.5D, (pLevel.random.nextDouble() - 0.5D) * 0.5D);
                        }
                    }
                    default -> throw new IllegalStateException("Invalid dragon type: [" + handler.getType() + "]");
                }
            }

            return stack;
        }

        return stack;
    }

    /** See {@link by.dragonsurvivalteam.dragonsurvival.client.extensions.ShakeWhenUsedExtension} */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity pEntity) {
        return Functions.secondsToTicks(2);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltips, flag);

        if (stack.has(DataComponents.CUSTOM_DATA)) {
            //noinspection DataFlowIssue, deprecation -> tag isn't modified, no need to create a copy
            CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).getUnsafe();
            tooltips.add(Component.translatable(DESCRIPTION));
            AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));

            Component dragonName;

            if (dragonType != null) {
                dragonName = dragonType.translatableName();
            } else {
                dragonName = Component.literal("Invalid dragon type");
            }

            double size = tag.getDouble("size");
            DragonLevel level = DragonStateHandler.getLevel(size);
            tooltips.add(Component.translatable(INFO, dragonName, level.translatableName(), String.format("%.0f", size)));

            if (tag.getBoolean("spinLearned")) {
                tooltips.add(Component.translatable(HAS_SPIN));
            }

            if (tag.getBoolean("hasWings")) {
                tooltips.add(Component.translatable(HAS_FLIGHT));
            }
        } else {
            tooltips.add(Component.translatable(IS_EMPTY));
        }
    }

    public static String getType(final ItemStack soul) {
        CustomData data = soul.get(DataComponents.CUSTOM_DATA);

        if (data != null) {
            //noinspection deprecation -> tag isn't modified, no need to create a copy
            return data.getUnsafe().getString("type");
        }

        return "";
    }

    @Override
    public void onUseTick(@NotNull final Level level, @NotNull final LivingEntity livingEntity, @NotNull final ItemStack soul, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, soul, remainingUseDuration);

        if (!(livingEntity instanceof Player player)) {
            return;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);
        String type = getType(soul);

        if (type.isBlank() && handler.isDragon()) {
            type = handler.getType().getTypeNameLowerCase();
        }

        SoundEvent sound = switch (type) {
            case "forest" -> DSSounds.FOREST_BREATH_END.get();
            case "cave" -> DSSounds.FIRE_BREATH_END.get();
            case "sea" -> DSSounds.STORM_BREATH_END.get();
            default -> throw new IllegalStateException("Dragon type [" + type + "] is invalid");
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
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            AbstractDragonType dragonType = DragonTypes.newDragonTypeInstance(stack.get(DataComponents.CUSTOM_DATA).copyTag().getString("type"));

            switch (dragonType.toString()) {
                case "forest" -> {
                    return FOREST_DRAGON_SOUL;
                }
                case "cave" -> {
                    return CAVE_DRAGON_SOUL;
                }
                case "sea" -> {
                    return SEA_DRAGON_SOUL;
                }
                default -> {
                    return "No translation key for dragon type!";
                }
            }
        } else {
            return EMPTY_DRAGON_SOUL;
        }
    }
}
