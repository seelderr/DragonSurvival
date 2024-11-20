package by.dragonsurvivalteam.dragonsurvival.common.items.growth;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.items.TooltipItem;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncGrowthState;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class StarHeartItem extends TooltipItem {
    @Translation(type = Translation.Type.MISC, comments = "Gradual growth is §2active§r")
    private static final String GROWTH = Translation.Type.GUI.wrap("message.growth");

    @Translation(type = Translation.Type.MISC, comments = "Gradual growth is §coff§r")
    private static final String NO_GROWTH = Translation.Type.GUI.wrap("message.no_growth");

    public StarHeartItem(final Properties properties, final String key){
        super(properties, key);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            DSAdvancementTriggers.USE_STAR_HEART.get().trigger(serverPlayer);
            DragonStateHandler handler = DragonStateProvider.getData(player);

            if (handler.isDragon()) {
                handler.isGrowing = !handler.isGrowing;
                player.sendSystemMessage(Component.translatable(handler.isGrowing ? GROWTH : NO_GROWTH));
                PacketDistributor.sendToPlayer(serverPlayer, new SyncGrowthState.Data(handler.isGrowing));
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }

        return super.use(level, player, hand);
    }
}