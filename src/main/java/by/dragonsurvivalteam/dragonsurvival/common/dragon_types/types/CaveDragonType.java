package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonTraitHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.passive.ContrastShowerAbility;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class CaveDragonType extends AbstractDragonType{
	public static ResourceLocation CAVE_FOOD = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_food_icons.png");
	public static ResourceLocation CAVE_MANA = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_magic_icons.png");

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
        boolean isInSeaBlock = DragonTraitHandler.isInCauldron(player, Blocks.WATER_CAULDRON) || player.getBlockStateOn().is(DSBlockTags.HYDRATES_SEA_DRAGON);
        ContrastShowerAbility contrastShower = DragonAbilities.getSelfAbility(player, ContrastShowerAbility.class);
        int maxRainTime = contrastShower != null ? Functions.secondsToTicks(contrastShower.getDuration()) : 1;

        Level level = player.level();
        double oldRainTime = timeInRain;
        int oldLavaTicks = lavaAirSupply;

        if (ServerConfig.penaltiesEnabled && !player.hasEffect(DSEffects.FIRE) && !player.isCreative() && !player.isSpectator()) {
            if (!level.isClientSide()) {
                if (player.isInWaterOrBubble() && ServerConfig.caveWaterDamage != 0.0 || player.isInWaterOrRain() && !player.isInWater() && ServerConfig.caveRainDamage != 0.0 || isInSeaBlock && ServerConfig.caveRainDamage != 0.0) {
                    if (player.isInWaterOrBubble() && player.tickCount % 10 == 0 && ServerConfig.caveWaterDamage != 0.0) {
                        player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), ServerConfig.caveWaterDamage.floatValue());
                    } else if ((player.isInWaterOrRain() && !player.isInWaterOrBubble() || isInSeaBlock) && ServerConfig.caveRainDamage != 0.0) {
                        timeInRain++;
                    }

                    if (timeInRain >= maxRainTime) {
                        if (player.tickCount % 40 == 0) {
                            player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.RAIN_BURN)), ServerConfig.caveRainDamage.floatValue());
                        }
                    }

                    if (player.tickCount % 40 == 0) {
                        player.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
                    }

                } else if (timeInRain > 0) {
                    if (maxRainTime > 0) {
                        timeInRain = Math.max(timeInRain - (int) Math.ceil(maxRainTime * 0.02F), 0);
                    } else {
                        timeInRain--;
                    }
                }
            }

            if (level.isClientSide()) {
                if (player.tickCount % 10 == 0 && timeInRain > 0) {
                    level.addParticle(ParticleTypes.POOF, player.getX() + level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1), 0, 0, 0);
                }
            }
        }

        if (player.isOnFire() && ServerConfig.bonusesEnabled && ServerConfig.caveFireImmunity) {
            player.clearFire();
        }

        if (!player.level().isClientSide()) {
            // Clamp the air supply to whatever the max is. This is needed to prevent issues if the server config is changed and lowers the max air supply.
            lavaAirSupply = Math.min(lavaAirSupply, ServerConfig.caveLavaSwimmingTicks);

            if (player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value()) && ServerConfig.bonusesEnabled && ServerConfig.caveLavaSwimming && ServerConfig.caveLavaSwimmingTicks != 0) {
                if (!player.getAbilities().invulnerable) {
                    lavaAirSupply--;

                    if (lavaAirSupply == -20) {
                        lavaAirSupply = 0;

                        if (!player.level().isClientSide()) {
                            player.hurt(player.damageSources().drown(), 2F);
                        }
                    }
                }

                if (!player.level().isClientSide() && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenUnderFluidType(NeoForgeMod.WATER_TYPE.value(), player)) {
                    player.stopRiding();
                }
            } else if (lavaAirSupply < ServerConfig.caveLavaSwimmingTicks && !player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                lavaAirSupply = Math.min(lavaAirSupply + (int) Math.ceil(ServerConfig.caveLavaSwimmingTicks * 0.0133333F), ServerConfig.caveLavaSwimmingTicks);
            }
        }

        if (!level.isClientSide() && (oldLavaTicks != lavaAirSupply || timeInRain != oldRainTime)) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), dragonStateHandler.getType().writeNBT()));
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
	public ResourceLocation getFoodIcons() {
		return CAVE_FOOD;
	}

	@Override
	public ResourceLocation getManaIcons() {
		return CAVE_MANA;
	}

	@Override
	public List<TagKey<Block>> mineableBlocks(){
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}

    @Override
    public String getTypeName() {
        return "cave";
    }
}