package by.dragonsurvivalteam.dragonsurvival.common.items.growth;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.items.TooltipItem;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class StarBoneItem extends TooltipItem {
    public StarBoneItem(final Properties properties, final String key) {
        super(properties, key);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        DragonStateHandler handler = DragonStateProvider.getData(player);

        if (handler.isDragon() && !DragonLevel.isSmallest(level.registryAccess(), handler.getLevel(), handler.getSize())) {
            handler.setSize(handler.getSize() - 2, player);

            if (!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }

            if (!level.isClientSide()) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncSize.Data(player.getId(), handler.getSize()));
                DSAdvancementTriggers.BE_DRAGON.get().trigger((ServerPlayer) player, handler.getSize(), handler.getTypeName());
            }

            player.refreshDimensions();
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        return super.use(level, player, hand);
    }
}