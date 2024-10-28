package by.dragonsurvivalteam.dragonsurvival.common.items;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SpinGrantItem extends Item {
	public SpinGrantItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand p_77659_3_) {
		DragonStateHandler handler = DragonStateProvider.getData(player);

		if (handler.isDragon()) {
			if (!world.isClientSide()) {
				handler.getMovementData().spinLearned = !handler.getMovementData().spinLearned;
				PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));

				if (!player.isCreative()) {
					player.getItemInHand(p_77659_3_).shrink(1);
				}
			}

			player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0F);
			return InteractionResultHolder.success(player.getItemInHand(p_77659_3_));
		}

		return super.use(world, player, p_77659_3_);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
		super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
		pTooltipComponents.add(Component.translatable("ds.description.spin_grant"));
	}
}