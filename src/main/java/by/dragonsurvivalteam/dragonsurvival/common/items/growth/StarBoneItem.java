package by.dragonsurvivalteam.dragonsurvival.common.items.growth;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/growth/StarBoneItem.java
import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SyncSize;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/growth/StarBoneItem.java
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class StarBoneItem extends Item{
	public StarBoneItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/growth/StarBoneItem.java
	
	@Override
	public void appendHoverText(ItemStack p_77624_1_, @Nullable
			Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_)
	{
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.starBone"));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level  worldIn, Player playerIn, InteractionHand handIn) {
		LazyOptional<DragonStateHandler> playerStateProvider = playerIn.getCapability(Capabilities.DRAGON_CAPABILITY);
		if (playerStateProvider.isPresent()) {
=======

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		LazyOptional<DragonStateHandler> playerStateProvider = playerIn.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
		if(playerStateProvider.isPresent()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/growth/StarBoneItem.java
			DragonStateHandler dragonStateHandler = playerStateProvider.orElse(null);
			if(dragonStateHandler.isDragon()){
				double size = dragonStateHandler.getSize();
				if(size > 14){
					size -= 2;
					size = Math.max(size, DragonLevel.BABY.size);
					dragonStateHandler.setSize(size, playerIn);


					if(!playerIn.isCreative()){
						playerIn.getItemInHand(handIn).shrink(1);
					}

					if(!worldIn.isClientSide){
						NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn), new SyncSize(playerIn.getId(), size));
						if(dragonStateHandler.getPassengerId() != 0){
							Entity mount = worldIn.getEntity(dragonStateHandler.getPassengerId());
							if(mount != null){
								mount.stopRiding();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/growth/StarBoneItem.java
								((ServerPlayer)playerIn).connection.send(new ClientboundSetPassengersPacket(playerIn));
								NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new SynchronizeDragonCap(playerIn.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
=======
								((ServerPlayerEntity)playerIn).connection.send(new SSetPassengersPacket(playerIn));
								NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)playerIn), new CompleteDataSync(playerIn));
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/growth/StarBoneItem.java
							}
						}
					}

					playerIn.refreshDimensions();
					return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
				}
			}
		}

		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_){
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslationTextComponent("ds.description.starBone"));
	}
}