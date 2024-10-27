package by.dragonsurvivalteam.dragonsurvival.common.items.growth;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class StarBoneItem extends Item{
	public StarBoneItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn){
		DragonStateHandler handler = DragonStateProvider.getData(playerIn);

		if (handler.isDragon()) {
			double size = handler.getSize();
			if (size > 14) {
				size -= 2;
				size = Math.max(size, DragonLevel.NEWBORN.size);
				handler.setSize(size, playerIn);

				if (!playerIn.isCreative()) {
					playerIn.getItemInHand(handIn).shrink(1);
				}

				if (!worldIn.isClientSide) {
					PacketDistributor.sendToPlayersTrackingEntityAndSelf(playerIn, new SyncSize.Data(playerIn.getId(), size));
					DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer)playerIn, handler.getSize(), handler.getTypeName());
				}

				playerIn.refreshDimensions();
				return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
			}
		}

		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag){
		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		pTooltipComponents.add(Component.translatable("ds.description.starBone"));
	}
}