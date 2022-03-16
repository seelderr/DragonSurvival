package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class DragonPressurePlates extends PressurePlateBlock
{
	public PressurePlateType type;
	
	public enum PressurePlateType{
		DRAGON,
		HUMAN,
		SEA,
		CAVE,
		FOREST
	}
	
	protected DragonPressurePlates(Properties p_i48445_1_, PressurePlateType type)
	{
		super(Sensitivity.EVERYTHING, p_i48445_1_);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
		
		this.type = type;
	}
	
	protected int getSignalStrength(World pLevel, BlockPos pPos) {
		AxisAlignedBB axisalignedbb = TOUCH_AABB.move(pPos);
		List<? extends Entity> list = pLevel.getEntities((Entity)null, axisalignedbb);
		
		if (!list.isEmpty()) {
			for(Entity entity : list) {
				if (!entity.isIgnoringBlockTriggers()) {
					switch (type){
						case DRAGON:
							return DragonUtils.isDragon(entity) ? 15 : 0;
							
						case HUMAN:
							return !DragonUtils.isDragon(entity) ? 15 : 0;
						
						case SEA:
							return DragonUtils.isDragon(entity) && DragonUtils.getDragonType(entity) == DragonType.SEA ? 15 : 0;
						
						case FOREST:
							return DragonUtils.isDragon(entity) && DragonUtils.getDragonType(entity) == DragonType.FOREST ? 15 : 0;
						
						case CAVE:
							return DragonUtils.isDragon(entity)&& DragonUtils.getDragonType(entity) == DragonType.CAVE ? 15 : 0;
							
					}
				}
			}
		}
		return 0;
	}
}
