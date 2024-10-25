package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class AthleticsAbility extends TickablePassiveAbility {
	@Override
	public Component getDescription(){
		return Component.translatable("ds.skill.description." + getName(), getLevel() == getMaxLevel() ? "III" : "II", getDuration());
	}

	public int getDuration(){
		return getLevel();
	}

	@Override
	public int getMaxLevel(){
		return 5;
	}

	@Override
	public int getMinLevel(){
		return 0;
	}

	@Override
	public void onTick(Player player){
		BlockState blockUnder = player.getBlockStateOn();
		Block block = blockUnder.getBlock();

		DragonStateHandler dragonStateHandler = DragonStateProvider.getData(player);

		boolean isSpeedBlock = DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS != null && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.containsKey(dragonStateHandler.getTypeName()) && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.get(dragonStateHandler.getTypeName()).contains(block);

		if(!player.level().isClientSide() && ServerConfig.bonusesEnabled && ServerConfig.speedupEffectLevel > 0 && isSpeedBlock){
			if(getDuration() > 0){
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(getDuration()), ServerConfig.speedupEffectLevel - 1 + (getLevel() == getMaxLevel() ? 1 : 0), false, false));
			}
		}
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(Component.translatable("ds.skill.duration.seconds", "+1"));
		return list;
	}
}