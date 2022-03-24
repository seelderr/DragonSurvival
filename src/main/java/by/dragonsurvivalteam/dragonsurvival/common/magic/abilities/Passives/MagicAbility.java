package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
=======
import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
	public ResourceLocation getIcon()
	{
		String levelKey = Integer.toString(DragonUtils.getMaxMana(player == null ? Minecraft.getInstance().player : player));
		
=======
	public ResourceLocation getIcon(){
		String levelKey = Integer.toString(ManaHandler.getMaxMana(player == null ? Minecraft.getInstance().player : player));

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
		if(!iconCache.containsKey(levelKey + "_" + getId())){
			ResourceLocation texture = new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/" + icon + "_" + levelKey + ".png");
			iconCache.put(levelKey + "_" + getId(), texture);
		}

		return iconCache.get(levelKey + "_" + getId());
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
	public Component getDescription()
	{
		int level = Math.max(0, (Math.min(50, getPlayer().experienceLevel) - 5) / 5);
		String points = getPoints() > 0 ? "+" + getPoints() : "0";
		String levels = level > 0 ? "+" + level : "0";
		
		return new TranslatableComponent("ds.skill.description." + getId(), DragonUtils.getMaxMana(player), points, levels);
=======
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/MagicAbility.java
	}
}