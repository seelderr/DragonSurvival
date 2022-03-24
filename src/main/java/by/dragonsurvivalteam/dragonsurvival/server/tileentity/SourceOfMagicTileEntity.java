package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.common.items.DSItems;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.server.containers.SourceOfMagicContainer;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.HashMap;

public class SourceOfMagicTileEntity extends BaseBlockTileEntity implements ITickableTileEntity, INamedContainerProvider, IAnimatable, IInventory{
	private final AnimationFactory manager = new AnimationFactory(this);
	public static HashMap<Item, Integer> consumables = new HashMap<>();
	public DragonType type = DragonType.NONE;
	public NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
	private int ticks;
	static{
		consumables.put(DSItems.elderDragonDust, Functions.secondsToTicks(ConfigHandler.SERVER.elderDragonDustTime.get()));
		consumables.put(DSItems.elderDragonBone, Functions.secondsToTicks(ConfigHandler.SERVER.elderDragonBoneTime.get()));
		consumables.put(DSItems.dragonHeartShard, Functions.secondsToTicks(ConfigHandler.SERVER.weakHeartShardTime.get()));
		consumables.put(DSItems.weakDragonHeart, Functions.secondsToTicks(ConfigHandler.SERVER.weakDragonHeartTime.get()));
		consumables.put(DSItems.elderDragonHeart, Functions.secondsToTicks(ConfigHandler.SERVER.elderDragonHeartTime.get()));
	}


	public SourceOfMagicTileEntity(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
	}

	@Override
	public void tick(){
		if(getBlockState().getBlock() == DSBlocks.seaSourceOfMagic){
			type = DragonType.SEA;
		}else if(getBlockState().getBlock() == DSBlocks.forestSourceOfMagic){
			type = DragonType.FOREST;
		}else if(getBlockState().getBlock() == DSBlocks.caveSourceOfMagic){
			type = DragonType.CAVE;
		}

		BlockState state = getBlockState();

		if(!state.getValue(SourceOfMagicBlock.FILLED) && !isEmpty()){
			level.setBlockAndUpdate(getBlockPos(), state.setValue(SourceOfMagicBlock.FILLED, true));
		}else if(state.getValue(SourceOfMagicBlock.FILLED) && isEmpty()){
			level.setBlockAndUpdate(getBlockPos(), state.setValue(SourceOfMagicBlock.FILLED, false));
		}

		if(!isEmpty()){
			if(ticks % 120 == 0){
				level.playLocalSound(getX(), getY(), getZ(), type == DragonType.CAVE ? SoundEvents.LAVA_AMBIENT : SoundEvents.WATER_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1f, true);
			}
		}

		ticks += 1;
	}

	@Override
	public boolean isEmpty(){
		return stacks.isEmpty() || getItem(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int i){
		return stacks.get(i);
	}

	@Override
	public ItemStack removeItem(int i, int i1){
		return ItemStackHelper.removeItem(stacks, i, i1);
	}

	@Override
	public ItemStack removeItemNoUpdate(int i){
		return ItemStackHelper.takeItem(this.stacks, 0);
	}

	@Override
	public void setItem(int i, ItemStack itemStack){
		if(i >= 0 && i < this.stacks.size()){
			this.stacks.set(i, itemStack);
		}
	}

	@Override
	public boolean stillValid(PlayerEntity playerEntity){
		return true;
	}

	@Override
	public void load(BlockState state, CompoundNBT compound){
		super.load(state, compound);
		type = DragonType.valueOf(compound.getString("Type"));
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.stacks);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound){
		compound.putString("Type", type.name());
		ItemStackHelper.saveAllItems(compound, stacks);
		return super.save(compound);
	}

	@Override
	public int getContainerSize(){
		return 1;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new StringTextComponent("Source Of Magic");
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_){
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		buffer.writeBlockPos(worldPosition);
		return new SourceOfMagicContainer(p_createMenu_1_, p_createMenu_2_, buffer);
	}

	@Override
	public void registerControllers(AnimationData data){
	}

	@Override
	public AnimationFactory getFactory(){
		return manager;
	}

	@Override
	public void clearContent(){
		stacks.clear();
	}
}