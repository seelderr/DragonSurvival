package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.ForestDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.SeaDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.tags.DSBlockTags;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public abstract class AthleticsAbility extends TickablePassiveAbility {
    @Override
    public Component getDescription() {
        return Component.translatable("ds.skill.description." + getName(), getLevel() == getMaxLevel() ? "III" : "II", getDuration());
    }

    public int getDuration() {
        return getLevel();
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        AbstractDragonType type = DragonStateProvider.getData(player).getType();

        TagKey<Block> speedUpBlockTag = switch (type) {
            case CaveDragonType ignored -> DSBlockTags.SPEEDS_UP_CAVE_DRAGON;
            case SeaDragonType ignored -> DSBlockTags.SPEEDS_UP_SEA_DRAGON;
            case ForestDragonType ignored -> DSBlockTags.SPEEDS_UP_FOREST_DRAGON;
            default -> throw new IllegalStateException("Not a valid dragon type: " + type.getClass().getName());
        };

        boolean isSpeedBlock = player.getBlockStateOn().is(speedUpBlockTag);

        if (ServerConfig.bonusesEnabled && ServerConfig.speedupEffectLevel > 0 && isSpeedBlock) {
            if (getDuration() > 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(getDuration()), ServerConfig.speedupEffectLevel - 1 + (getLevel() == getMaxLevel() ? 1 : 0), false, false));
            }
        }
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable("ds.skill.duration.seconds", "+1"));
        return list;
    }
}