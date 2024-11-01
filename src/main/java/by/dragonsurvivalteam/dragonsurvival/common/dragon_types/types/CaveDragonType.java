package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTraitHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.server.dragon.CaveDragonConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.List;

public class CaveDragonType extends AbstractDragonType {
    public int timeInRain;
    public int lavaAirSupply;

    public CaveDragonType() {
        slotForBonus = 1;
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("timeInRain", timeInRain);
        tag.putInt("lavaAirSupply", lavaAirSupply);
        return tag;
    }

    @Override
    public void readNBT(CompoundTag base) {
        timeInRain = base.getInt("timeInRain");
        lavaAirSupply = base.getInt("lavaAirSupply");
    }

    @Override
    public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        if (ServerConfig.penaltiesEnabled && !player.hasEffect(DSEffects.FIRE)) {
            int maxRainTime = DragonAbilities.getAbility(player, ContrastShowerAbility.class).map(ability -> Functions.secondsToTicks(ability.getDuration())).orElse(1);
            boolean wasInWater = false;

            if (CaveDragonConfig.caveWaterDamage > 0 && player.isInWaterOrBubble()) {
                wasInWater = true;

                if (player.tickCount % 10 == 0) {
                    player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), CaveDragonConfig.caveWaterDamage.floatValue());
                }
            }

            if (CaveDragonConfig.caveRainDamage > 0 && (/* check rain */ player.isInWaterOrRain() && !player.isInWater() || /* check other water sources */ DragonTraitHandler.isInCauldron(player, Blocks.WATER_CAULDRON) || player.getBlockStateOn().is(DSBlockTags.HYDRATES_SEA_DRAGON))) {
                wasInWater = true;
                timeInRain++;
            }

            if (!player.level().isClientSide() && wasInWater && player.tickCount % 40 == 0) {
                if (timeInRain >= maxRainTime) {
                    player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.RAIN_BURN)), CaveDragonConfig.caveRainDamage.floatValue());
                }

                player.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
            }

            // Only reduce the time in rain if the player wasn't in contact with water
            if (!wasInWater && timeInRain > 0) {
                if (maxRainTime > 1) {
                    // The ability reduces the time in rain by a larger amount (e.g. level 1 is 30 seconds -> 600 ticks * 0.02f -> 12)
                    timeInRain = Math.max(0, timeInRain - (int) Math.ceil(maxRainTime * 0.02f));
                } else {
                    timeInRain--;
                }
            }

            if (player.level().isClientSide() && timeInRain > 0 && player.tickCount % 10 == 0) {
                player.level().addParticle(ParticleTypes.POOF, player.getX() + player.level().random.nextDouble() * (player.level().random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + player.level().random.nextDouble() * (player.level().random.nextBoolean() ? 1 : -1), 0, 0, 0);
            }
        }

        // In case the server config is changed and lowers the max. air supply
        lavaAirSupply = Math.min(lavaAirSupply, CaveDragonConfig.caveLavaSwimmingTicks);

        if (ServerConfig.bonusesEnabled && CaveDragonConfig.caveLavaSwimming && CaveDragonConfig.caveLavaSwimmingTicks > 0 && player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value())) {
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
        } else if (lavaAirSupply < CaveDragonConfig.caveLavaSwimmingTicks && !player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
            lavaAirSupply = Math.min(lavaAirSupply + (int) Math.ceil(CaveDragonConfig.caveLavaSwimmingTicks * 0.0133333F), CaveDragonConfig.caveLavaSwimmingTicks);
        }
    }

    @Override
    public boolean isInManaCondition(Player player, DragonStateHandler cap) {
        return player.isInLava() || player.isOnFire() || player.hasEffect(DSEffects.BURN) || player.hasEffect(DSEffects.FIRE) || DragonTraitHandler.isInCauldron(player, Blocks.LAVA_CAULDRON);
    }

    @Override
    public void onPlayerDeath() {
        timeInRain = 0;
    }

    @Override
    public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler handler) {
        return null;
    }

    @Override
    public List<TagKey<Block>> mineableBlocks() {
        return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public String getTypeName() {
        return "cave";
    }
}