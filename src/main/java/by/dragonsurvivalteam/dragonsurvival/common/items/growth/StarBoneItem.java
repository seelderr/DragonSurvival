package by.dragonsurvivalteam.dragonsurvival.common.items.growth;

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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class StarBoneItem extends Item{
	public StarBoneItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		LazyOptional<DragonStateHandler> playerStateProvider = playerIn.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
		if(playerStateProvider.isPresent()){
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
								((ServerPlayerEntity)playerIn).connection.send(new SSetPassengersPacket(playerIn));
								NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)playerIn), new CompleteDataSync(playerIn));
							}
						}
					}

					playerIn.refreshDimensions();
					return ActionResult.consume(playerIn.getItemInHand(handIn));
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