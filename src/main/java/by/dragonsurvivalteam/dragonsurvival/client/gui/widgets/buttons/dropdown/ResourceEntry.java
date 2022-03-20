package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ResourceEntry{
	public Entity ent;
	public String id;
	public String mod;

	public List<ItemStack> displayItems;
	private ItemStack cachedDisplay = ItemStack.EMPTY;

	private int index;
	private int tick;

	public ResourceEntry(String id, List<ItemStack> displayItems){
		this.id = id;
		this.displayItems = displayItems;

		String[] split = id.split(":");
		mod = split[0];
	}

	public boolean isEmpty(){
		return displayItems == null || displayItems.isEmpty();
	}

	public void tick(){
		tick++;

		if(cachedDisplay == null || cachedDisplay.isEmpty()){
			if(displayItems != null && !displayItems.isEmpty()){
				cachedDisplay = displayItems.get(0);
			}
		}

		if(displayItems != null && displayItems.size() > 1){
			if(tick % 120 == 0){
				index++;

				if(index >= displayItems.size()){
					index = 0;
				}

				cachedDisplay = displayItems.get(index);
			}
		}else{
			if(index > 0){
				cachedDisplay = displayItems.get(0);
				index = 0;
			}
		}
	}

	public ItemStack getDisplayItem(){
		return cachedDisplay;
	}
}