<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
package by.jackraidenph.dragonsurvival.common.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
=======
package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Innate;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
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

	@OnlyIn( Dist.CLIENT )
	@Override
	public ArrayList<ITextComponent> getInfo(){
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

		if(tier != null){
			components.add(new TranslationTextComponent("ds.skill.harvest_level", I18n.get("ds.skill.harvest_level." + tier.name().toLowerCase())));
		}
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);

		if(handler != null){
			ItemStack swordStack = handler.getClawInventory().getClawsInventory().getItem(0);
			double ageBonus = handler.isDragon() ? (handler.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : handler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0;
			double swordBonus = swordStack.isEmpty() ? 0 : swordStack.getItem() instanceof SwordItem ? ((((SwordItem)swordStack.getItem()).getDamage())) : 0;
			double bonus = Math.max(ageBonus, swordBonus - 1);

			if(bonus > 0.0){
				components.add(new TranslationTextComponent("ds.skill.claws.damage", "+" + bonus));
			}
		}

		return components;
	}

	@Override
	public int getLevel(){
		return FMLEnvironment.dist == Dist.CLIENT ? getHarvestTexture() : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public int getHarvestTexture(){
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
		if(handler == null) return 0;
		
		Tier tier = Tiers.STONE;
=======
		if(handler == null){
			return 0;
		}

		ItemTier tier = ItemTier.STONE;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
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
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
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
=======
			if(tieredItem.getTier() instanceof ItemTier){
				tier = (ItemTier)tieredItem.getTier();
			}
		}

		switch(tier){
			case WOOD:
				return 1;

			case STONE:
				return 2;

			case IRON:
				return 3;

			case GOLD:
				return 4;

			case DIAMOND:
				return 5;

			case NETHERITE:
				return 6;

			default:
				return 0;
		}
	}

	@OnlyIn( Dist.CLIENT )
	public int getHarvestLevel(){
		int harvestLevel = 0;

		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
		if(handler != null){
			DragonLevel level = handler.getLevel();
			if(level.ordinal() >= ConfigHandler.SERVER.bonusUnlockedAt.get().ordinal()){
				harvestLevel = ConfigHandler.SERVER.bonusHarvestLevel.get();
			}else{
				harvestLevel = ConfigHandler.SERVER.baseHarvestLevel.get();
			}

			switch(handler.getType()){
				case SEA:{
					ItemStack item = handler.getClawInventory().getClawsInventory().getItem(3);
					if(!item.isEmpty()){
						harvestLevel += item.getHarvestLevel(ToolType.SHOVEL, Minecraft.getInstance().player, null);
					}
					break;
				}

				case FOREST:{
					ItemStack item = handler.getClawInventory().getClawsInventory().getItem(2);
					if(!item.isEmpty()){
						harvestLevel += item.getHarvestLevel(ToolType.AXE, Minecraft.getInstance().player, null);
					}
					break;
				}

				case CAVE:{
					ItemStack item = handler.getClawInventory().getClawsInventory().getItem(1);
					if(!item.isEmpty()){
						harvestLevel += item.getHarvestLevel(ToolType.PICKAXE, Minecraft.getInstance().player, null);
					}
					break;
				}
			}
		}

		return harvestLevel;
	}
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Innate/DragonClawsAbility.java
