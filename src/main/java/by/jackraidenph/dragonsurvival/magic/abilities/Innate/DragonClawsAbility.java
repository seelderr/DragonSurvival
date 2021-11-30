package by.jackraidenph.dragonsurvival.magic.abilities.Innate;

import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.loading.FMLEnvironment;

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
		return MathHelper.clamp(FMLEnvironment.dist == Dist.CLIENT ? getHarvestLevel() : 0, 0, 5) + 1;
	}
	
	@OnlyIn( Dist.CLIENT)
	public int getHarvestLevel(){
		int harvestLevel = 0;
		
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
		if(handler != null) {
			DragonLevel level = handler.getLevel();
			if (level.ordinal() >= ConfigHandler.SERVER.bonusUnlockedAt.get().ordinal()) {
				harvestLevel = ConfigHandler.SERVER.bonusHarvestLevel.get();
			} else {
				harvestLevel = ConfigHandler.SERVER.baseHarvestLevel.get();
			}
			
			switch(handler.getType()){
				case SEA: {
					ItemStack item = handler.clawsInventory.getItem(3);
					if (!item.isEmpty()) {
						harvestLevel += item.getHarvestLevel(ToolType.SHOVEL, Minecraft.getInstance().player, null);
					}
					break;
				}
					
				case FOREST: {
					ItemStack item = handler.clawsInventory.getItem(2);
					if (!item.isEmpty()) {
						harvestLevel += item.getHarvestLevel(ToolType.AXE, Minecraft.getInstance().player, null);
					}
					break;
				}
					
				case CAVE: {
					ItemStack item = handler.clawsInventory.getItem(1);
					if (!item.isEmpty()) {
						harvestLevel += item.getHarvestLevel(ToolType.PICKAXE, Minecraft.getInstance().player, null);
					}
					break;
				}
			}
		}
		
		return harvestLevel;
	}
	
	@OnlyIn( Dist.CLIENT)
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
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
		
		if(handler != null) {
			ItemStack swordStack = handler.clawsInventory.getItem(0);
			double ageBonus = handler.isDragon() ? (handler.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : handler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0;
			double swordBonus = swordStack.isEmpty() ? 0 : swordStack.getItem() instanceof SwordItem ? ((((SwordItem)swordStack.getItem()).getDamage())) : 0;
			double bonus = Math.max(ageBonus, swordBonus - 1);
			
			if(bonus > 0.0) {
				components.add(new TranslationTextComponent("ds.skill.claws.damage", "+" + bonus));
			}
		}
		
		return components;
	}
}
