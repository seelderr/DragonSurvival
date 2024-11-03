package by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types;

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

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class CaveDragonType extends AbstractDragonType{
    public static ResourceLocation CAVE_FOOD = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_food_icons.png");
    public static ResourceLocation CAVE_MANA = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/cave_magic_icons.png");

    public int rainResistSupply;
    public int lavaAirSupply;

    public CaveDragonType() {
        slotForBonus = 1;
    }

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("rainResistSupply", rainResistSupply);
        tag.putInt("lavaAirSupply", lavaAirSupply);
        return tag;
    }

    @Override
    public void readNBT(CompoundTag base) {
        rainResistSupply = base.getInt("rainResistSupply");
        lavaAirSupply = base.getInt("lavaAirSupply");
    }

    private static boolean isDragonTakingWaterDamage(Player player) {
        return player.isInWaterOrBubble() && ServerConfig.caveWaterDamage != 0.0;
    }

    private static boolean isDragonTakingRainDamage(Player player) {
        boolean isOnCauldronOrHydratingBlock = DragonTraitHandler.isInCauldron(player, Blocks.WATER_CAULDRON) || player.getBlockStateOn().is(DSBlockTags.HYDRATES_SEA_DRAGON);
        return (player.isInWaterOrRain() || isOnCauldronOrHydratingBlock) && !player.isInWaterOrBubble() && ServerConfig.caveRainDamage != 0.0;
    }

    public static int getMaxRainResistSupply(Player player) {
        ContrastShowerAbility contrastShower = DragonAbilities.getSelfAbility(player, ContrastShowerAbility.class);
        return contrastShower != null ? Functions.secondsToTicks(contrastShower.getDuration()) : 1;
    }

    public boolean shouldShowRainResistSupply(Player player) {
        int maxRainResistSupply = getMaxRainResistSupply(player);
        return isDragonTakingRainDamage(player) || rainResistSupply < maxRainResistSupply && maxRainResistSupply != 1;
    }

    private static boolean isDragonLavaSwimming(Player player) {
        return player.isEyeInFluidType(NeoForgeMod.LAVA_TYPE.value()) && ServerConfig.bonusesEnabled && ServerConfig.caveLavaSwimming && ServerConfig.caveLavaSwimmingTicks != 0;
    }

    public boolean shouldShowLavaAirSupply(Player player) {
        return isDragonLavaSwimming(player) || lavaAirSupply < ServerConfig.caveLavaSwimmingTicks;
    }

    @Override
    public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler) {
        int maxRainTime = getMaxRainResistSupply(player);

        Level level = player.level();
        final float RAIN_AND_LAVA_PERCENTAGE_REGEN_RATE_PER_TICK = 0.0133333F;

        if (!level.isClientSide()) {
            int oldRainResistSupply = rainResistSupply;
            int oldLavaAirSupply = lavaAirSupply;

            if (ServerConfig.penaltiesEnabled && !player.hasEffect(DSEffects.FIRE) && !player.isCreative() && !player.isSpectator()) {
                if (isDragonTakingWaterDamage(player) || isDragonTakingRainDamage(player)) {
                    if (isDragonTakingWaterDamage(player)) {
                        player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.WATER_BURN)), ServerConfig.caveWaterDamage.floatValue());
                    } else {
                        rainResistSupply--;
                        if(rainResistSupply % 10 == 0) {
                            level.addParticle(ParticleTypes.POOF, player.getX() + level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1), player.getY() + 0.5F, player.getZ() + level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1), 0, 0, 0);
                        }
                    }

                    if (rainResistSupply == -40) {
                        rainResistSupply = 0;
                        player.hurt(new DamageSource(DSDamageTypes.get(player.level(), DSDamageTypes.RAIN_BURN)), ServerConfig.caveRainDamage.floatValue());
                        player.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    rainResistSupply = Math.min(rainResistSupply + (int) Math.ceil(maxRainTime * RAIN_AND_LAVA_PERCENTAGE_REGEN_RATE_PER_TICK), maxRainTime);
                }
            }

            // Clamp the air supply to whatever the max is. This is needed to prevent issues if the server config is changed and lowers the max air supply.
            lavaAirSupply = Math.min(lavaAirSupply, ServerConfig.caveLavaSwimmingTicks);

            if (isDragonLavaSwimming(player)) {
                if (!player.getAbilities().invulnerable) {
                    lavaAirSupply--;

                    if (lavaAirSupply == -20) {
                        lavaAirSupply = 0;

                        player.hurt(player.damageSources().drown(), 2F);
                    }
                }

                if (!player.level().isClientSide() && player.isPassenger() && player.getVehicle() != null && !player.getVehicle().canBeRiddenUnderFluidType(NeoForgeMod.WATER_TYPE.value(), player)) {
                    player.stopRiding();
                }
            } else if (lavaAirSupply < ServerConfig.caveLavaSwimmingTicks && !player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
                lavaAirSupply = Math.min(lavaAirSupply + (int) Math.ceil(ServerConfig.caveLavaSwimmingTicks * RAIN_AND_LAVA_PERCENTAGE_REGEN_RATE_PER_TICK), ServerConfig.caveLavaSwimmingTicks);
            }

            if((oldLavaAirSupply != lavaAirSupply || rainResistSupply != oldRainResistSupply)) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncDragonType.Data(player.getId(), dragonStateHandler.getType().writeNBT()));
            }
        }

        if (player.isOnFire() && ServerConfig.bonusesEnabled && ServerConfig.caveFireImmunity) {
            player.clearFire();
        }
    }

    @Override
    public boolean isInManaCondition(Player player, DragonStateHandler cap) {
        return player.isInLava() || player.isOnFire() || player.hasEffect(DSEffects.BURN) || player.hasEffect(DSEffects.FIRE) || DragonTraitHandler.isInCauldron(player, Blocks.LAVA_CAULDRON);
    }

    @Override
    public void onPlayerDeath() {
        rainResistSupply = 0;
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