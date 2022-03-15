package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.common.util.DragonUtils;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;

public class DragonPressurePlates extends AbstractPressurePlateBlock
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
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
		super(p_i48445_1_);
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
							return DragonUtils.getDragonType(entity) == DragonType.SEA ? 15 : 0;
						
						case FOREST:
							return DragonUtils.getDragonType(entity) == DragonType.FOREST ? 15 : 0;
						
						case CAVE:
							return DragonUtils.getDragonType(entity) == DragonType.CAVE ? 15 : 0;
							
					}
				}
			}
		}
		return 0;
	}
	
	protected void playOnSound(IWorld pLevel, BlockPos pPos) {
		pLevel.playSound((PlayerEntity)null, pPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
	}
	
	protected void playOffSound(IWorld pLevel, BlockPos pPos) {
		pLevel.playSound((PlayerEntity)null, pPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
	}
	
	protected int getSignalForState(BlockState pState) {
		return pState.getValue(POWERED) ? 15 : 0;
	}
	
	protected BlockState setSignalForState(BlockState pState, int pStrength) {
		return pState.setValue(POWERED, Boolean.valueOf(pStrength > 0));
	}
	
	protected int getPressedTime() {
		return 10;
	}
	
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(POWERED);
	}
}
