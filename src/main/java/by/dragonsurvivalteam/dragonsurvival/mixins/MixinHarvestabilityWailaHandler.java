package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.wthitharvestability.WailaHandler;

import java.util.List;

@Mixin(WailaHandler.class)
public class MixinHarvestabilityWailaHandler {
    private Player player;
    private BlockState blockState;

    /** Get {@link net.minecraft.world.entity.player.Player} and {@link net.minecraft.world.level.block.state.BlockState} */
    @Inject(method = "getHarvestability", at = @At("HEAD"), remap = false)
    public void getPlayer(final List<Component> stringList, final Player player, final BlockState blockState, final BlockPos pos, final mcp.mobius.waila.api.IPluginConfig config, boolean minimalLayout, CallbackInfo callback) {
        this.player = player;
        this.blockState = blockState;
    }

    /** Give WTHIT the relevant dragon claw harvest tool or a fake tool based on the dragon harvest level */
    @ModifyVariable(method = "getHarvestability", at = @At(value = "STORE"), name = "heldStack", remap = false)
    public ItemStack change(final ItemStack itemStack) {
        // TODO :: Can this be safely cached?
        DragonStateHandler handler = DragonUtils.getHandler(player);

        if (handler.isDragon()) {
            Tier tier = handler.getDragonHarvestTier(blockState);
            ItemStack clawStack = ClawToolHandler.getDragonHarvestTool(player);

            // Main hand is not a tool or its tier is lower than the base harvest level of the dragon
            if (!(clawStack.getItem() instanceof TieredItem tieredItem) || TierSortingRegistry.getTiersLowerThan(tier).contains(tieredItem.getTier())) {
                return handler.getFakeTool(blockState);
            }

            return clawStack;
        }

        return itemStack;
    }
}
