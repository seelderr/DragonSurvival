package by.dragonsurvivalteam.dragonsurvival.common.items.growth;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/growth/StarHeartItem.java
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SyncGrowthState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncGrowthState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/growth/StarHeartItem.java

import javax.annotation.Nullable;
import java.util.List;

public class StarHeartItem extends Item{
	public StarHeartItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/growth/StarHeartItem.java
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, @Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_)
	{
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.starHeart"));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level  world, Player player, InteractionHand p_77659_3_)
	{
		if(!world.isClientSide) {
			DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
			
			if (handler != null && handler.isDragon()) {
				handler.growing = !handler.growing;
				player.sendMessage(new TranslatableComponent(handler.growing ? "ds.growth.now_growing" : "ds.growth.no_growth"), player.getUUID());
				
				NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new SyncGrowthState(handler.growing));
				return InteractionResultHolder.success(player.getItemInHand(p_77659_3_));
=======

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand p_77659_3_){
		if(!world.isClientSide){
			DragonStateHandler handler = DragonUtils.getHandler(player);

			if(handler != null && handler.isDragon()){
				handler.growing = !handler.growing;
				player.sendMessage(new TranslationTextComponent(handler.growing ? "ds.growth.now_growing" : "ds.growth.no_growth"), player.getUUID());

				NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncGrowthState(handler.growing));
				return ActionResult.success(player.getItemInHand(p_77659_3_));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/growth/StarHeartItem.java
			}
		}

		return super.use(world, player, p_77659_3_);
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslationTextComponent("ds.description.starHeart"));
	}
}