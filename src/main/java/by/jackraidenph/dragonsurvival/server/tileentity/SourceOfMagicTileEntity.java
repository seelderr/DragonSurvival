package by.jackraidenph.dragonsurvival.server.tileentity;

import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.server.containers.SourceOfMagicContainer;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.ItemStackHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class SourceOfMagicTileEntity extends BaseBlockTileEntity implements ITickableTileEntity, INamedContainerProvider, IAnimatable
{
    public int energy = 0;
    public int damageCooldown;
    public boolean regenerationMode = true;
    public DragonType type = DragonType.NONE;
    public ItemStackHandler regenItem = new ItemStackHandler(1);
    
    private final AnimationFactory manager = new AnimationFactory(this);
    
    public SourceOfMagicTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    //TODO Drop items when broken
    //TODO Consume items for effect
    
    @Override
    public void tick() {
        if(getBlockState().getBlock() == DSBlocks.seaSourceOfMagic){
            type = DragonType.SEA;
        }else if(getBlockState().getBlock() == DSBlocks.forestSourceOfMagic){
            type = DragonType.FOREST;
        }else if(getBlockState().getBlock() == DSBlocks.caveSourceOfMagic){
            type = DragonType.CAVE;
        }
        
        if (damageCooldown > 0) {
            damageCooldown--;
        }
        ItemStack itemStack = regenItem.getStackInSlot(0);
        if (!itemStack.isEmpty()) {
//            int value = regenValue.get(itemStack.getItem());
//            if (energy < 64 - value) {
//                energy = Math.min(64, energy + value);
//                itemStack.shrink(1);
//            }
        }

//        if (!level.isClientSide) {
//        	level.blockEvent(worldPosition, getBlockState().getBlock(), 0, energy);
//        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 0) {
            energy = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("Health", energy);
        compound.putInt("Damage cooldown", damageCooldown);
        compound.putString("Type", type.name());
        compound.putBoolean("Regenerating", regenerationMode);
        compound.put("Item", regenItem.serializeNBT());
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        energy = compound.getInt("Health");
        damageCooldown = compound.getInt("Damage cooldown");
        type = DragonType.valueOf(compound.getString("Type"));
        regenerationMode = compound.getBoolean("Regenerating");
        regenItem.deserializeNBT(compound.getCompound("Item"));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Source Of Magic");
    }


    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeBlockPos(worldPosition);
        return new SourceOfMagicContainer(p_createMenu_1_, p_createMenu_2_, buffer);
    }
    
    @Override
    public void registerControllers(AnimationData data)
    {
    }
    
    @Override
    public AnimationFactory getFactory()
    {
        return manager;
    }
}
