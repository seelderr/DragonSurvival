package by.jackraidenph.dragonsurvival.magic.Abilities.Passives;

import by.jackraidenph.dragonsurvival.magic.common.PassiveDragonAbility;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class AthleticsAbility extends PassiveDragonAbility
{
	public AthleticsAbility(String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(abilityId, icon, minLevel, maxLevel);
	}
	
	public int getDuration(){
		return getLevel();
	}
	
	@Override
	public AthleticsAbility createInstance()
	{
		return new AthleticsAbility(id, icon, minLevel, maxLevel);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> list = super.getInfo();
		
		/*
		List<Block> blocks = SpecificsHandler.DRAGON_SPEEDUP_BLOCKS.get(DragonStateProvider.getCap(Minecraft.getInstance().player).map(cap -> cap.getType()).get());
		String blocksString = "";
		
		for (Block block : blocks) {
			ITextComponent component = new ItemStack(block).getDisplayName();
			String text = component.getString();
			
			if(!text.isEmpty()) {
				blocksString += text + "; ";
			}
		}
		
		ITextComponent component = new TranslationTextComponent("ds.skill.athletics.blocks", blocksString);
		list.add(component);
		*/
		
		return list;
	}
}
