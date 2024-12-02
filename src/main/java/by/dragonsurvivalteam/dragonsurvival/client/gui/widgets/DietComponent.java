package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

public record DietComponent(Holder<DragonType> type, Item item) implements TooltipComponent { }
