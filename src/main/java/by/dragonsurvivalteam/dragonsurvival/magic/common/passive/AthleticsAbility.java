package by.dragonsurvivalteam.dragonsurvival.magic.common.passive;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public abstract class AthleticsAbility extends TickablePassiveAbility {
	@Override
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getName(), getDuration(), getLevel() == getMaxLevel() ? "III" : "II");
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
		BlockState feetBlock = player.getFeetBlockState();
		BlockState blockUnder = player.level.getBlockState(player.blockPosition().below());
		Block block = blockUnder.getBlock();

		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);

		boolean isSpeedBlock = DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS != null && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.containsKey(dragonStateHandler.getTypeName()) && DragonConfigHandler.DRAGON_SPEEDUP_BLOCKS.get(dragonStateHandler.getTypeName()).contains(block);
		boolean isSpeedMaterial = DragonConfigHandler.DRAGON_SPEED_MATERIALS != null && DragonConfigHandler.DRAGON_SPEED_MATERIALS.containsKey(dragonStateHandler.getTypeName()) && DragonConfigHandler.DRAGON_SPEED_MATERIALS.get(dragonStateHandler.getTypeName()).contains(blockUnder.getMaterial());


		if(!player.level.isClientSide && ServerConfig.bonuses && ServerConfig.speedupEffectLevel > 0 && (isSpeedBlock || isSpeedMaterial)){
			if(getDuration() > 0){
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Functions.secondsToTicks(getDuration()), ServerConfig.speedupEffectLevel - 1 + (getLevel() == getMaxLevel() ? 1 : 0), false, false));
			}
		}
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+1"));
		return list;
	}
}