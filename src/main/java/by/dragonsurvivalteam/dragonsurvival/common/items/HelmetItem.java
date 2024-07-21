package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.client.render.item.HelmetStackTileEntityRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

public class HelmetItem extends BlockItem{
	public HelmetItem(Block pBlock, Properties pProperties){
		super(pBlock, pProperties);
	}

	public static void registerClientExtensions(RegisterClientExtensionsEvent event){
		event.registerItem(new IClientItemExtensions(){
			private final HelmetStackTileEntityRenderer renderer = new HelmetStackTileEntityRenderer();

			@Override
			public @NotNull HelmetStackTileEntityRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}
}