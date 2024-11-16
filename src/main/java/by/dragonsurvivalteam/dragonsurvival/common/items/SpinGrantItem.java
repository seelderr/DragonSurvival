package by.dragonsurvivalteam.dragonsurvival.common.items;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SpinGrantItem extends TooltipItem {
    public SpinGrantItem(final Properties properties, final String key) {
        super(properties, key);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        DragonStateHandler handler = DragonStateProvider.getData(player);

        if (handler.isDragon()) {
            if (!world.isClientSide()) {
                handler.getMovementData().spinLearned = !handler.getMovementData().spinLearned;
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSpinStatus.Data(player.getId(), handler.getMovementData().spinAttack, handler.getMovementData().spinCooldown, handler.getMovementData().spinLearned));

                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
            }

            player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0F);
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return super.use(world, player, hand);
    }
}