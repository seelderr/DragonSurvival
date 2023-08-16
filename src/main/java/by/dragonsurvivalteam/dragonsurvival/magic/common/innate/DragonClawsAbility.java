package by.dragonsurvivalteam.dragonsurvival.magic.common.innate;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;

import static by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes.*;

public abstract class DragonClawsAbility extends InnateDragonAbility {

	@Override
	public int getMaxLevel(){
		return 1;
	}

	@Override
	public int getMinLevel(){
		return 1;
	}

	@OnlyIn( Dist.CLIENT )
	@Override
	public ArrayList<Component> getInfo(){
		int harvestLevel = getHarvestTexture() - 1;
		Tier tier = null;

		for(Tier t : Tiers.values())
			if(t.getLevel() <= harvestLevel){
				if(tier == null || t.getLevel() > tier.getLevel()){
					tier = t;
				}
			}

		ArrayList<Component> components = super.getInfo();
		components.add(new TranslatableComponent("ds.skill.tool_type." + getName()));

		if(tier != null)
			components.add(new TranslatableComponent("ds.skill.harvest_level", I18n.get("ds.skill.harvest_level." + ((Tiers)tier).name().toLowerCase())));

		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);

		ItemStack swordStack = handler.getClawToolData().getClawsInventory().getItem(0);
		double ageBonus = handler.isDragon() ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;
		double swordBonus = swordStack.isEmpty() ? 0 : swordStack.getItem() instanceof SwordItem ? ((SwordItem)swordStack.getItem()).getDamage() : 0;
		double bonus = Math.max(ageBonus, swordBonus - 1);

		if(bonus > 0.0)
			components.add(new TranslatableComponent("ds.skill.claws.damage", "+" + bonus));

		return components;
	}

	@Override
	public int getLevel(){
		return FMLEnvironment.dist == Dist.CLIENT ? getHarvestTexture() : 0;
	}

	@OnlyIn( Dist.CLIENT )
	public int getHarvestTexture(){
		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);

		Tier tier = Tiers.STONE;
		ItemStack stack = null;

		if(handler.getType() == null) return 0;

		if(SEA.equals(handler.getType())){
			stack = handler.getClawToolData().getClawsInventory().getItem(3);
		}else if(FOREST.equals(handler.getType())){
			stack = handler.getClawToolData().getClawsInventory().getItem(2);
		}else if(CAVE.equals(handler.getType())){
			stack = handler.getClawToolData().getClawsInventory().getItem(1);
		}

		if(stack != null && !stack.isEmpty() && stack.getItem() instanceof TieredItem st)
			tier = st.getTier();

		if(Tiers.WOOD.equals(tier))
			return 1;
		else if(Tiers.STONE.equals(tier))
			return 2;
		else if(Tiers.IRON.equals(tier))
			return 3;
		else if(Tiers.GOLD.equals(tier))
			return 4;
		else if(Tiers.DIAMOND.equals(tier))
			return 5;
		else if(Tiers.NETHERITE.equals(tier))
			return 6;
		return 0;
	}
}