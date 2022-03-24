package by.dragonsurvivalteam.dragonsurvival.common.items;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/WingGrantItem.java
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
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
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/WingGrantItem.java

import javax.annotation.Nullable;
import java.util.List;

public class WingGrantItem extends Item{
	public WingGrantItem(Properties p_i48487_1_){
		super(p_i48487_1_);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/WingGrantItem.java
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand p_77659_3_)
	{
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		if (handler != null && handler.isDragon()) {
			if(!world.isClientSide) {
=======
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand p_77659_3_){
		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(handler != null && handler.isDragon()){
			if(!world.isClientSide){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/WingGrantItem.java
				handler.setHasWings(!handler.hasWings());
				NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new CompleteDataSync(player));

				if(!player.isCreative()){
					player.getItemInHand(p_77659_3_).shrink(1);
				}
			}

			player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 0F);
			return InteractionResultHolder.success(player.getItemInHand(p_77659_3_));
		}

		return super.use(world, player, p_77659_3_);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/items/WingGrantItem.java
	public void appendHoverText(ItemStack p_77624_1_, @Nullable Level p_77624_2_, List<Component> p_77624_3_, TooltipFlag p_77624_4_)
	{
=======
	public void appendHoverText(ItemStack p_77624_1_,
		@Nullable
			World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/items/WingGrantItem.java
		super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
		p_77624_3_.add(new TranslatableComponent("ds.description.wing_grant"));
	}
}