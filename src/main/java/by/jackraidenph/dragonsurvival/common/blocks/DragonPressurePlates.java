package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext pContext)
	{
		return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder)
	{
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(FACING);
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
