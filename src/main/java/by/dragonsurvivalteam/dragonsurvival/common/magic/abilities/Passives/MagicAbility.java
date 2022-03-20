package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicAbility extends PassiveDragonAbility{
	public MagicAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public MagicAbility createInstance(){
		return new MagicAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
	public IFormattableTextComponent getDescription(){
		int level = Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5);
		String points = getPoints() > 0 ? "+" + getPoints() : "0";
		String levels = level > 0 ? "+" + level : "0";

		return new TranslationTextComponent("ds.skill.description." + getId(), ManaHandler.getMaxMana(player), points, levels);
	}

	public int getPoints(){
		return getLevel();
	}

	@OnlyIn( Dist.CLIENT )
	@Override
	public ResourceLocation getIcon(){
		String levelKey = Integer.toString(ManaHandler.getMaxMana(player == null ? Minecraft.getInstance().player : player));

		if(!iconCache.containsKey(levelKey + "_" + getId())){
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/" + icon + "_" + levelKey + ".png");
			iconCache.put(levelKey + "_" + getId(), texture);
		}

		return iconCache.get(levelKey + "_" + getId());
	}

	@Override
	public boolean isDisabled(){
		if(type == DragonType.FOREST && !ConfigHandler.SERVER.forestMagic.get()){
			return true;
		}
		if(type == DragonType.SEA && !ConfigHandler.SERVER.seaMagic.get()){
			return true;
		}
		if(type == DragonType.CAVE && !ConfigHandler.SERVER.caveMagic.get()){
			return true;
		}

		return super.isDisabled();
	}
}