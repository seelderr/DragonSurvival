package by.dragonsurvivalteam.dragonsurvival.server.tileentity;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.server.containers.SourceOfMagicContainer;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.HashMap;

public class SourceOfMagicTileEntity extends BaseBlockTileEntity implements Container, MenuProvider, IAnimatable{
	private final AnimationFactory manager = GeckoLibUtil.createFactory(this);
	public static HashMap<Item, Integer> consumables = new HashMap<>();
	public NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);
	private int ticks;
	static{
		consumables.put(DSItems.elderDragonDust, Functions.secondsToTicks(ServerConfig.elderDragonDustTime));
		consumables.put(DSItems.elderDragonBone, Functions.secondsToTicks(ServerConfig.elderDragonBoneTime));
		consumables.put(DSItems.dragonHeartShard, Functions.secondsToTicks(ServerConfig.weakHeartShardTime));
		consumables.put(DSItems.weakDragonHeart, Functions.secondsToTicks(ServerConfig.weakDragonHeartTime));
		consumables.put(DSItems.elderDragonHeart, Functions.secondsToTicks(ServerConfig.elderDragonHeartTime));
	}
	public SourceOfMagicTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(DSTileEntities.sourceOfMagicTileEntity, pWorldPosition, pBlockState);
	}

	public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SourceOfMagicTileEntity pBlockEntity){
		BlockState state = pState;

		if(!state.getValue(SourceOfMagicBlock.FILLED) && !pBlockEntity.isEmpty()){
			pBlockEntity.level.setBlockAndUpdate(pPos, state.setValue(SourceOfMagicBlock.FILLED, true));
		}else if(state.getValue(SourceOfMagicBlock.FILLED) && pBlockEntity.isEmpty()){
			pBlockEntity.level.setBlockAndUpdate(pPos, state.setValue(SourceOfMagicBlock.FILLED, false));
		}

		if(!pBlockEntity.isEmpty()){
			if(pBlockEntity.ticks % 120 == 0){
				pBlockEntity.level.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), pState.getBlock() == DSBlocks.caveSourceOfMagic ? SoundEvents.LAVA_AMBIENT : SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.5f, 1f, true);
			}
		}

		pBlockEntity.ticks += 1;
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
		return ContainerHelper.removeItem(stacks, i, i1);
	}

	@Override
	public ItemStack removeItemNoUpdate(int i){
		return ContainerHelper.takeItem(stacks, 0);
	}

	@Override
	public void setItem(int i, ItemStack itemStack){
		if(i >= 0 && i < stacks.size()){
			stacks.set(i, itemStack);
		}
	}

	@Override
	public boolean stillValid(Player playerEntity){
		return true;
	}

	@Override
	public void load(CompoundTag compound){
		super.load(compound);
		stacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, stacks);
	}

	@Override
	public void saveAdditional(CompoundTag compound){
		ContainerHelper.saveAllItems(compound, stacks);
	}

	@Override
	public int getContainerSize(){
		return 1;
	}

	@Override
	public TextComponent getDisplayName(){
		return new TextComponent("Source Of Magic");
	}

	@Override
	public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_){
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
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