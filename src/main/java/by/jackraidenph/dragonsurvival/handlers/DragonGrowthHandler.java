package by.jackraidenph.dragonsurvival.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.SyncSize;
import by.jackraidenph.dragonsurvival.network.SynchronizeDragonCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID)
public class DragonGrowthHandler {
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();

        DragonStateProvider.getCap(player).ifPresent(handler -> {
            if (!handler.isDragon())
                return;

            float size = handler.getSize();

            if (size >= 40)
                return;

            boolean canContinue = false;

            List<Item> newbornList = parseConfigList(ConfigHandler.SERVER.growNewborn.get());
            List<Item> youngList = parseConfigList(ConfigHandler.SERVER.growYoung.get());
            List<Item> adultList = parseConfigList(ConfigHandler.SERVER.growAdult.get());

            List<Item> allowedItems = new ArrayList<>();

            switch (handler.getLevel()) {
                case BABY:
                    if (newbornList.contains(item))
                        canContinue = true;
                    else if (youngList.contains(item) || adultList.contains(item))
                        allowedItems = newbornList;

                    break;
                case YOUNG:
                    if (youngList.contains(item))
                        canContinue = true;
                    else if (newbornList.contains(item) || adultList.contains(item))
                        allowedItems = youngList;

                    break;
                case ADULT:
                    if (adultList.contains(item))
                        canContinue = true;
                    else if (newbornList.contains(item) || youngList.contains(item))
                        allowedItems = adultList;

                    break;
            }

            if (!canContinue) {
                if (!allowedItems.isEmpty() && world.isClientSide()) {
                    List<String> displayData = allowedItems.stream()
                            .map(i -> new ItemStack(i).getDisplayName().getString())
                            .collect(Collectors.toList());
                    StringBuilder result = new StringBuilder();

                    for (int i = 0; i < displayData.size(); i++) {
                        String entry = displayData.get(i);

                        result.append(entry).append(i + 1 < displayData.size() ? ", " : "");
                    }

                    player.displayClientMessage(new TranslationTextComponent("ds.invalid_grow_item", result), false);
                }

                return;
            }

            handler.setSize(++size, player);
            event.getItemStack().shrink(1);

            if (world.isClientSide)
                return;

            DragonSurvivalMod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSize(player.getId(), size));

            if (player.getVehicle() == null || !(player.getVehicle() instanceof ServerPlayerEntity))
                return;

            ServerPlayerEntity vehicle = (ServerPlayerEntity) player.getVehicle();

            DragonStateProvider.getCap(vehicle).ifPresent(vehicleCap -> {
                player.stopRiding();

                vehicle.connection.send(new SSetPassengersPacket(vehicle));

                DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> vehicle), new SynchronizeDragonCap(vehicle.getId(),
                        vehicleCap.isHiding(), vehicleCap.getType(), vehicleCap.getSize(), vehicleCap.hasWings(), vehicleCap.getLavaAirSupply(), 0));
            });

            player.refreshDimensions();
        });
    }

    private static List<Item> parseConfigList(List<? extends String> values) {
        List<Item> result = new ArrayList<>();

        for (String entry : values.toArray(new String[0])) {
            final String[] sEntry = entry.split(":");
            final ResourceLocation rlEntry = new ResourceLocation(sEntry[0], sEntry[1]);

            if (sEntry[0].equalsIgnoreCase("tag")) {
                final ITag<Item> tag = ItemTags.getAllTags().getTag(rlEntry);

                if (tag != null)
                    result.addAll(tag.getValues());
            } else
                result.add(ForgeRegistries.ITEMS.getValue(rlEntry));
        }

        return result;
    }
}