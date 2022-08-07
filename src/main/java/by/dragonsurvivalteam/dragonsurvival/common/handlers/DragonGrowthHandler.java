package by.dragonsurvivalteam.dragonsurvival.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static by.dragonsurvivalteam.dragonsurvival.util.DragonLevel.*;

@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID )
public class DragonGrowthHandler{
	public static long newbornToYoung = TimeUnit.SECONDS.convert(3, TimeUnit.HOURS);
	public static long youngToAdult = TimeUnit.SECONDS.convert(15, TimeUnit.HOURS);
	public static long adultToMax = TimeUnit.SECONDS.convert(24, TimeUnit.HOURS);
	public static long beyond = TimeUnit.SECONDS.convert(30, TimeUnit.DAYS);

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event){
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();

		Player player = event.getPlayer();
		Level world = player.getCommandSenderWorld();

		DragonStateProvider.getCap(player).ifPresent(handler -> {
			if(!handler.isDragon()){
				return;
			}

			double size = handler.getSize();

			if(size >= ServerConfig.maxGrowthSize){
				return;
			}

			boolean canContinue = false;

			List<Item> newbornList = ConfigHandler.configList(Item.class, ServerConfig.growNewborn);
			List<Item> youngList = ConfigHandler.configList(Item.class, ServerConfig.growYoung);
			List<Item> adultList = ConfigHandler.configList(Item.class, ServerConfig.growAdult);

			List<Item> allowedItems = new ArrayList<>();

			switch(handler.getLevel()){
				case NEWBORN:
					if(newbornList.contains(item)){
						canContinue = true;
					}else if(youngList.contains(item) || adultList.contains(item)){
						allowedItems = newbornList;
					}

					break;
				case YOUNG:
					if(youngList.contains(item)){
						canContinue = true;
					}else if(newbornList.contains(item) || adultList.contains(item)){
						allowedItems = youngList;
					}

					break;
				case ADULT:
					if(adultList.contains(item)){
						canContinue = true;
					}else if(newbornList.contains(item) || youngList.contains(item)){
						allowedItems = adultList;
					}

					break;
			}

			if(!canContinue){
				if(!allowedItems.isEmpty() && world.isClientSide()){
					List<String> displayData = allowedItems.stream().map(i -> new ItemStack(i).getDisplayName().getString()).toList();
					StringBuilder result = new StringBuilder();

					for(int i = 0; i < displayData.size(); i++){
						String entry = displayData.get(i);

						result.append(entry).append(i + 1 < displayData.size() ? ", " : "");
					}

					player.displayClientMessage(new TranslatableComponent("ds.invalid_grow_item", result), false);
				}

				return;
			}

			int increment = getIncrement(item, handler.getLevel());
			size += increment;
			handler.setSize(size, player);

			if(!player.isCreative()){
				event.getItemStack().shrink(1);
			}

			if(world.isClientSide){
				return;
			}

			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSize(player.getId(), size));
			player.refreshDimensions();
		});
	}

	public static int getIncrement(Item item, DragonLevel level){
		List<Item> newbornList = ConfigHandler.configList(Item.class, ServerConfig.growNewborn);
		List<Item> youngList = ConfigHandler.configList(Item.class, ServerConfig.growYoung);
		List<Item> adultList = ConfigHandler.configList(Item.class, ServerConfig.growAdult);

		int increment = 0;

		if(item == DSItems.starBone){
			return -2;
		}

		switch(level){
			case NEWBORN:
				if(adultList.contains(item)){
					increment = 3;
				}else if(youngList.contains(item)){
					increment = 2;
				}else if(newbornList.contains(item)){
					increment = 1;
				}
				break;
			case YOUNG:
				if(adultList.contains(item)){
					increment = 2;
				}else if(youngList.contains(item)){
					increment = 1;
				}
				break;

			case ADULT:
				if(adultList.contains(item)){
					increment = 1;
				}
				break;
		}
		return increment;
	}

	@SubscribeEvent
	public static void onPlayerUpdate(TickEvent.PlayerTickEvent event){
		if(!ServerConfig.alternateGrowing){
			return;
		}

		Player player = event.player;
		Level world = player.getCommandSenderWorld();

		if(world.isClientSide || event.phase == Phase.END){
			return;
		}

		if(!DragonUtils.isDragon(player)){
			return;
		}

		if(player.tickCount % (60 * 20) != 0){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(handler -> {
			if(handler.growing){
                /*
                    1. Newborn - young = 3-4 h
                    2. Young - adult = 15-20h
                    3. Adult - maximum growth = 24h
                    4. After maximum growth. = 30 days for max growth
                 */

				double d = 0;
				double timeIncrement = 60 * 20;

				if(handler.getSize() < YOUNG.size){
					d = (((YOUNG.size - NEWBORN.size) / ((newbornToYoung * 20.0))) * timeIncrement) * ServerConfig.newbornGrowthModifier;
				}else if(handler.getSize() < ADULT.size){
					d = (((ADULT.size - YOUNG.size) / ((youngToAdult * 20.0))) * timeIncrement) * ServerConfig.youngGrowthModifier;
				}else if(handler.getSize() < 40){
					d = (((40 - ADULT.size) / ((adultToMax * 20.0))) * timeIncrement) * ServerConfig.adultGrowthModifier;
				}else{
					d = (((60 - 40) / ((beyond * 20.0))) * timeIncrement) * ServerConfig.maxGrowthModifier;
				}

				double size = handler.getSize() + d;
				size = Math.min(size, ServerConfig.maxGrowthSize);

				if(handler.getSize() != size){
					handler.setSize(size, player);

					NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSize(player.getId(), size));
					player.refreshDimensions();
				}
			}
		});
	}
}