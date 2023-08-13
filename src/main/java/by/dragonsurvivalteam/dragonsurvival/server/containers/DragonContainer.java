package by.dragonsurvivalteam.dragonsurvival.server.containers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSContainers;
import by.dragonsurvivalteam.dragonsurvival.server.containers.slots.ClawToolSlot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DragonContainer extends AbstractContainerMenu{ // TODO - tool slots sometimes show when logging in but not the rest of the container
	private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = {InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
	public final CraftingContainer craftMatrix = new CraftingContainer(this, 3, 3);
	public final ResultContainer craftResult = new ResultContainer();
	public final boolean isLocalWorld;
	public final Player player;
	private final int craftingSlot;
	public List<Slot> craftingSlots = new ArrayList<>();
	public List<Slot> inventorySlots = new ArrayList<>();
	public Inventory playerInventory;
	public int menuStatus = 0;
	protected final ContainerData dataStatus = new ContainerData(){
		@Override
		public int get(int p_221476_1_){
			if(p_221476_1_ == 0){
				return menuStatus;
			}
			return 0;
		}

		@Override
		public void set(int p_221477_1_, int p_221477_2_){
			if(p_221477_1_ == 0){
				menuStatus = p_221477_2_;
			}
		}

		@Override
		public int getCount(){
			return 1;
		}
	};

	public DragonContainer(int id, Inventory playerInventory, boolean localWorld){
		super(DSContainers.dragonContainer, id);
		isLocalWorld = localWorld;
		player = playerInventory.player;
		this.playerInventory = playerInventory;

		addDataSlots(dataStatus);

		for(int k = 0; k < 4; ++k){
			EquipmentSlot equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
			addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18){
				@Override
				public boolean mayPlace(ItemStack stack){
					return stack.canEquip(equipmentslottype, player);
				}

				@Override
				@OnlyIn( Dist.CLIENT )
				public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(){
					return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
				}

				@Override
				public boolean mayPickup(Player playerIn){
					ItemStack itemstack = getItem();
					return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
				}
			});

			broadcastChanges();
		}

		//main inventory
		for(int l = 0; l < 3; ++l){
			for(int j1 = 0; j1 < 9; ++j1){
				Slot s = new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 84 + l * 18);
				addSlot(s);
				inventorySlots.add(s);
			}
		}
		//hotbar
		for(int i1 = 0; i1 < 9; ++i1){
			Slot s = new Slot(playerInventory, i1, 8 + i1 * 18, 142);
			addSlot(s);
			inventorySlots.add(s);
		}

		DragonStateProvider.getCap(player).ifPresent(cap -> {
			for(int i = 0; i < 4; ++i){
				ClawToolSlot s = new ClawToolSlot(this, cap.getClawToolData().getClawsInventory(), i, -50, 35 + i * 18, i);
				addSlot(s);
				inventorySlots.add(s);
			}
		});

		//Offhand
		addSlot(new Slot(playerInventory, 40, 26, 62));

		addSlot(new ResultSlot(playerInventory.player, craftMatrix, craftResult, 0, 178, 33));
		craftingSlot = slots.size();

		for(int i = 0; i < craftMatrix.getWidth(); ++i){
			for(int j = 0; j < craftMatrix.getHeight(); ++j){
				Slot s = new Slot(craftMatrix, j + i * 3, 111 + j * 18, 15 + i * 18);
				addSlot(s);
				craftingSlots.add(s);
			}
		}

		update();
	}

	public void update(){
		DragonStateProvider.getCap(player).ifPresent(cap -> {
			menuStatus = cap.getClawToolData().isClawsMenuOpen() ? 1 : 0;
		});

		broadcastChanges();
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index){
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if(slot != null && slot.hasItem()){
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			EquipmentSlot equipmentslottype = Mob.getEquipmentSlotForItem(itemstack);

			if(index == craftingSlot){
				if(!moveItemStackTo(itemstack1, 4, 46, true)){
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			}else if(equipmentslottype.getType() == Type.ARMOR && !slots.get(3 - equipmentslottype.getIndex()).hasItem()){
				int i = 3 - equipmentslottype.getIndex();
				if(!moveItemStackTo(itemstack1, i, i + 1, false)){
					return ItemStack.EMPTY;
				}
			}else if(index < 4){
				if(!moveItemStackTo(itemstack1, 4, 40, false)){
					return ItemStack.EMPTY;
				}
			}else if(index < 9){
				if(!moveItemStackTo(itemstack1, 31, 40, false)){
					return ItemStack.EMPTY;
				}
			}else if(equipmentslottype == EquipmentSlot.OFFHAND && !slots.get(44).hasItem()){
				if(!moveItemStackTo(itemstack1, 44, 46, false)){
					return ItemStack.EMPTY;
				}
			}else if(index >= 4 && index < 31){
				if(!moveItemStackTo(itemstack1, 31, 40, false)){
					return ItemStack.EMPTY;
				}
			}else if(index >= 31 && index < 40){
				if(!moveItemStackTo(itemstack1, 4, 31, false)){
					return ItemStack.EMPTY;
				}
			}else{
				if(!moveItemStackTo(itemstack1, 4, 31, false)){
					return ItemStack.EMPTY;
				}
			}

			if(itemstack1.isEmpty()){
				slot.set(ItemStack.EMPTY);
			}else{
				slot.setChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()){
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
			if(index == 0){
				playerIn.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	@Override
	public boolean canTakeItemForPickAll(
		@Nonnull
			ItemStack stack, Slot slotIn){
		return slotIn.container != craftResult && super.canTakeItemForPickAll(stack, slotIn);
	}

	@Override
	public void removed(Player playerEntity){
		super.removed(playerEntity);
		clearContainer(playerEntity, craftMatrix);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void slotsChanged(Container inventory){
		if(!player.level.isClientSide){
			ServerPlayer serverplayerentity = (ServerPlayer)player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = player.level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMatrix, player.level);
			if(optional.isPresent()){
				CraftingRecipe icraftingrecipe = optional.get();
				if(craftResult.setRecipeUsed(player.level, serverplayerentity, icraftingrecipe)){
					itemstack = icraftingrecipe.assemble(craftMatrix);
				}
			}

			craftResult.setItem(45, itemstack);
			setRemoteSlot(45, itemstack);
			serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 45, itemstack));
		}
	}

	@Override
	public boolean stillValid(Player p_75145_1_){
		return true;
	}
}