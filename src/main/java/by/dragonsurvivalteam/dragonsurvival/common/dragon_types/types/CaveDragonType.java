package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTraitHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.CaveDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.DragonBonusConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class CaveDragonType extends AbstractDragonType {
    @Translation(type = Translation.Type.MISC, comments = "Cave Dragon")
    private static final String NAME = Translation.Type.DESCRIPTION.wrap("cave_dragon");

    private static final ResourceLocation CAVE_FOOD = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_food_icons.png");
    private static final ResourceLocation CAVE_MANA = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_magic_icons.png");

    /** Once the rain resistance supply reaches this value the dragon will take damage and the rain resistance supply will go back to 0 */
    private static final int RAIN_DAMAGE_RATE_POINT = -40;
    private static final float SUPPLY_RATE = 0.013f;

    public int rainResistanceSupply;
    public int lavaAirSupply;

    public CaveDragonType() {
        clawTextureSlot = ClawInventoryData.Slot.PICKAXE.ordinal();
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("rain_resistance_supply", rainResistanceSupply);
        tag.putInt("lava_air_supply", lavaAirSupply);
        return tag;
    }

    @Override
    public void readNBT(CompoundTag base) {
        rainResistanceSupply = base.getInt("rain_resistance_supply");
        lavaAirSupply = base.getInt("lava_air_supply");
    }

    @Override
    public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (ServerConfig.penaltiesEnabled && !player.hasEffect(DSEffects.FIRE)) {
            boolean hadWaterContact = false;

            if (isTakingWaterDamage(player)) {
                hadWaterContact = true;

                if (player.tickCount % 10 == 0) {
                    player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), CaveDragonConfig.caveWaterDamage.floatValue());
                }
            }

            if (isTakingRainDamage(player)) {
                hadWaterContact = true;
                rainResistanceSupply--;

                if (rainResistanceSupply % 10 == 0) {
                    player.level().addParticle(ParticleTypes.POOF, player.getX() + player.level().random.nextDouble() * (player.level().random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + player.level().random.nextDouble() * (player.level().random.nextBoolean() ? 1 : -1), 0, 0, 0);
                }
            }

            if (hadWaterContact && rainResistanceSupply == RAIN_DAMAGE_RATE_POINT) {
                player.playSound(SoundEvents.LAVA_EXTINGUISH, 1, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2f + 1);

                if (!player.level().isClientSide()) { // TODO :: add the sound to the damage type itself? would need to extend the DamageEffects enum
                    player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.RAIN_BURN)), CaveDragonConfig.caveRainDamage.floatValue());
                }

                rainResistanceSupply = 0;
            }

            if (!hadWaterContact) {
                // Only increase the rain resistance supply if the player wasn't in contact with water
                int maxResistance = getMaxRainResistanceSupply(player);
                rainResistanceSupply = Math.min(maxResistance, rainResistanceSupply + (int) Math.ceil(maxResistance * SUPPLY_RATE));
            }
        }

        // In case the server config is changed and lowers the max. air supply
        lavaAirSupply = Math.min(lavaAirSupply, CaveDragonConfig.caveLavaSwimmingTicks);

        if (isLavaSwimming(player)) {
            lavaAirSupply--;

            if (lavaAirSupply == -20) {
                lavaAirSupply = 0;

                if (!player.level().isClientSide()) {
                    player.hurt(player.damageSources().drown(), 2F);
                }
            }

            if (!player.level().isClientSide() && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenUnderFluidType(NeoForgeMod.WATER_TYPE.value(), player)) {
                player.stopRiding();
            }
        } else if (lavaAirSupply < CaveDragonConfig.caveLavaSwimmingTicks && player.getEyeInFluidType().isAir()) {
            lavaAirSupply = Math.min(CaveDragonConfig.caveLavaSwimmingTicks, lavaAirSupply + (int) Math.ceil(CaveDragonConfig.caveLavaSwimmingTicks * SUPPLY_RATE));
        }
    }

    @Override
    public boolean isInManaCondition(Player player) {
        return player.isInLava() || player.isOnFire() || player.hasEffect(DSEffects.BURN) || player.hasEffect(DSEffects.FIRE) || DragonTraitHandler.isInCauldron(player, Blocks.LAVA_CAULDRON);
    }

    @Override
    public void onPlayerDeath() {
        rainResistanceSupply = 0;
    }

    @Override
    public ResourceLocation getFoodIcons() {
        return CAVE_FOOD;
    }

    @Override
    public ResourceLocation getManaIcons() {
        return CAVE_MANA;
    }

    @Override
    public TagKey<Block> harvestableBlocks() {
        return DSBlockTags.CAVE_DRAGON_HARVESTABLE;
    }

    @Override
    public String getTypeName() {
        return "cave";
    }

    @Override
    public Component translatableName() {
        return Component.translatable(NAME);
    }

    public static int getMaxRainResistanceSupply(final Player player) {
        return DragonAbilities.getAbility(player, ContrastShowerAbility.class).map(ability -> Functions.secondsToTicks(ability.getDuration())).orElse(1);
    }

    public static boolean isTakingRainDamage(final Player player) {
        return CaveDragonConfig.caveRainDamage > 0 && (/* check rain */ player.isInWaterOrRain() && !player.isInWater() || /* check other water sources */ DragonTraitHandler.isInCauldron(player, Blocks.WATER_CAULDRON) || player.getBlockStateOn().is(DSBlockTags.HYDRATES_SEA_DRAGON) || player.getInBlockState().is(DSBlockTags.HYDRATES_SEA_DRAGON));
    }

    public static boolean isTakingWaterDamage(final Player player) {
        return CaveDragonConfig.caveWaterDamage > 0 && player.isInWaterOrBubble();
    }

    public static boolean isLavaSwimming(final Player player) {
        return DragonBonusConfig.bonusesEnabled && CaveDragonConfig.caveLavaSwimming && CaveDragonConfig.caveLavaSwimmingTicks > 0 && player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value());
    }
}