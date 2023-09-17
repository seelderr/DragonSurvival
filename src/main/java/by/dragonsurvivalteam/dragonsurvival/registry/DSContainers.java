package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import by.dragonsurvivalteam.dragonsurvival.server.containers.SourceOfMagicContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD )
public class DSContainers{

	public static MenuType<SourceOfMagicContainer> nestContainer;
	public static MenuType<DragonContainer> dragonContainer;

	//MenuType
	@SubscribeEvent
	public static void registerContainers(RegisterEvent event){
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.MENU_TYPES))
			return;

		nestContainer = IForgeMenuType.create(SourceOfMagicContainer::new);
		event.register(ForgeRegistries.Keys.MENU_TYPES, new ResourceLocation(DragonSurvivalMod.MODID, "dragon_nest"), ()->nestContainer);

		dragonContainer = IForgeMenuType.create((windowId, inv, data) -> new DragonContainer(windowId, inv));
		event.register(ForgeRegistries.Keys.MENU_TYPES, new ResourceLocation(DragonSurvivalMod.MODID, "dragon_container"), ()->dragonContainer);
	}
}