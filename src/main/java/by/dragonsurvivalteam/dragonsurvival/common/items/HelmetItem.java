package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.client.render.item.HelmetStackTileEntityRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class HelmetItem extends BlockItem{
	public HelmetItem(Block pBlock, Properties pProperties){
		super(pBlock, pProperties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer){
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions(){
			private final HelmetStackTileEntityRenderer renderer = new HelmetStackTileEntityRenderer();

			public HelmetStackTileEntityRenderer getRenderer() {
				return renderer;
			}
		});
	}
}