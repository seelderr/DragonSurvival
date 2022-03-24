package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Passives;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/AthleticsAbility.java
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
=======
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/AthleticsAbility.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class AthleticsAbility extends PassiveDragonAbility{
	public AthleticsAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public AthleticsAbility createInstance(){
		return new AthleticsAbility(type, id, icon, minLevel, maxLevel);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Passives/AthleticsAbility.java
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), getDuration(), getLevel() == getMaxLevel() ? "III" : "II");
=======
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration(), getLevel() == getMaxLevel() ? "III" : "II");
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Passives/AthleticsAbility.java
	}

	public int getDuration(){
		return getLevel();
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.duration.seconds", "+1"));
		return list;
	}

	@Override
	public boolean isDisabled(){
		if(type == DragonType.FOREST && !ConfigHandler.SERVER.forestAthletics.get()){
			return true;
		}
		if(type == DragonType.SEA && !ConfigHandler.SERVER.seaAthletics.get()){
			return true;
		}
		if(type == DragonType.CAVE && !ConfigHandler.SERVER.caveAthletics.get()){
			return true;
		}

		return super.isDisabled();
	}
}