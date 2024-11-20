package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

public record GrowthComponent(Item item, int growth) implements TooltipComponent { }
