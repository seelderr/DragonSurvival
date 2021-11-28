package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class DragonClawsAbility extends InnateDragonAbility
{
	public DragonClawsAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	@Override
	public DragonAbility createInstance()
	{
		return new DragonClawsAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public int getLevel()
	{
		return getHarvestLevel() + 1;
	}
	
	public int getHarvestLevel(){
		int harvestLevel = 0;
		DragonLevel level = DragonStateProvider.getCap(Minecraft.getInstance().player).map((cap) -> cap.getLevel()).get();
		if(level.ordinal() >= ConfigHandler.SERVER.bonusUnlockedAt.get().ordinal()){
			harvestLevel = ConfigHandler.SERVER.bonusHarvestLevel.get();
		}else{
			harvestLevel = ConfigHandler.SERVER.baseHarvestLevel.get();
		}
		
		return harvestLevel;
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		int harvestLevel = getHarvestLevel();
		ItemTier tier = null;
		
		for(ItemTier t : ItemTier.values()){
			if(t.getLevel() <= harvestLevel){
				if(tier == null || t.getLevel() > tier.getLevel()){
					tier = t;
				}
			}
		}
		
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.tool_type." + getId()));
		
		if(tier != null) {
			components.add(new TranslationTextComponent("ds.skill.harvest_level", I18n.get("ds.skill.harvest_level." + tier.name().toLowerCase())));
		}
		double bonusDamage = DragonStateProvider.getCap(Minecraft.getInstance().player).map((cap) -> (cap.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : cap.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get())).get();
		if(bonusDamage > 0.0) {
			components.add(new TranslationTextComponent("ds.skill.damage", "+" + bonusDamage));
		}
		//components.add(new TranslationTextComponent("ds.skill.harvest_level." + getId(), "TBA"));
		
		return components;
	}
}
