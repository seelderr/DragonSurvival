package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;

public class DragonClawsAbility extends InnateDragonAbility{
	public DragonClawsAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel){
		super(type, abilityId, icon, minLevel, maxLevel);
	}

	@Override
	public DragonAbility createInstance(){
		return new DragonClawsAbility(type, id, icon, minLevel, maxLevel);
	}


	@Override
	public int getLevel(){
		return FMLEnvironment.dist == Dist.CLIENT ? getHarvestTexture() : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public int getHarvestTexture(){
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
		if(handler == null) return 0;

		Tier tier = Tiers.STONE;
		ItemStack stack = null;

		switch(handler.getType()){
			case SEA:{
				stack = handler.getClawInventory().getClawsInventory().getItem(3);
				break;
			}

			case FOREST:{
				stack = handler.getClawInventory().getClawsInventory().getItem(2);
				break;
			}

			case CAVE:{
				stack = handler.getClawInventory().getClawsInventory().getItem(1);
				break;
			}
		}

		if(stack != null && !stack.isEmpty() && stack.getItem() instanceof TieredItem){
			TieredItem tieredItem = (TieredItem)stack.getItem();
			tieredItem.getTier();
			tier = tieredItem.getTier();
		}

		if (Tiers.WOOD.equals(tier)) {
			return 1;
		} else if (Tiers.STONE.equals(tier)) {
			return 2;
		} else if (Tiers.IRON.equals(tier)) {
			return 3;
		} else if (Tiers.GOLD.equals(tier)) {
			return 4;
		} else if (Tiers.DIAMOND.equals(tier)) {
			return 5;
		} else if (Tiers.NETHERITE.equals(tier)) {
			return 6;
		}
		return 0;
	}

	@OnlyIn( Dist.CLIENT)
	@Override
	public ArrayList<Component> getInfo()
	{
		int harvestLevel = getHarvestTexture() - 1;
		Tier tier = null;

		for(Tier t : Tiers.values()){
			if(t.getLevel() <= harvestLevel){
				if(tier == null || t.getLevel() > tier.getLevel()){
					tier = t;
				}
			}
		}

		ArrayList<Component> components = super.getInfo();
		components.add(new TranslatableComponent("ds.skill.tool_type." + getId()));

		if(tier != null) {
			components.add(new TranslatableComponent("ds.skill.harvest_level", I18n.get("ds.skill.harvest_level." + ((Tiers)tier).name().toLowerCase())));
		}
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);

		if(handler != null) {
			ItemStack swordStack = handler.getClawInventory().getClawsInventory().getItem(0);
			double ageBonus = handler.isDragon() ? (handler.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : handler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0;
			double swordBonus = swordStack.isEmpty() ? 0 : swordStack.getItem() instanceof SwordItem ? ((((SwordItem)swordStack.getItem()).getDamage())) : 0;
			double bonus = Math.max(ageBonus, swordBonus - 1);

			if(bonus > 0.0) {
				components.add(new TranslatableComponent("ds.skill.claws.damage", "+" + bonus));
			}
		}

		return components;
	}
}