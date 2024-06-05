package by.dragonsurvivalteam.dragonsurvival.common.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class HelmetItem extends BlockItem{
	public HelmetItem(Block pBlock, Properties pProperties){
		super(pBlock, pProperties);
	}

	/*@Override
	public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer){
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions(){
			private final HelmetStackTileEntityRenderer renderer = new HelmetStackTileEntityRenderer();

			@Override
			public HelmetStackTileEntityRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}*/
}