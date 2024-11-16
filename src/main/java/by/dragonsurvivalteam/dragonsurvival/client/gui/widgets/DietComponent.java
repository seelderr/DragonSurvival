package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

public record DietComponent(AbstractDragonType type, Item item) implements TooltipComponent { }
