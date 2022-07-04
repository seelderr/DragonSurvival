package by.dragonsurvivalteam.dragonsurvival.client.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncDragonAbilitySlot;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class KeyInputHandler{

	@ConfigOption(side = ConfigSide.CLIENT, key = "alternateCastMode", comment = "Should the cast mode where you click the keybind to cast be used?")
	public static Boolean alternateCastMode = false;

	public static KeyMapping TOGGLE_WINGS;
	public static KeyMapping DRAGON_INVENTORY;

	public static KeyMapping NEXT_ABILITY;
	public static KeyMapping PREV_ABILITY;

	public static KeyMapping USE_ABILITY;
	public static KeyMapping TOGGLE_ABILITIES;

	public static KeyMapping ABILITY1;
	public static KeyMapping ABILITY2;
	public static KeyMapping ABILITY3;
	public static KeyMapping ABILITY4;

	public static KeyMapping SPIN_ABILITY;
	public static KeyMapping FREE_LOOK;


	public static void setupKeybinds(){
		TOGGLE_WINGS = new KeyMapping("ds.keybind.wings", GLFW.GLFW_KEY_G, "Dragon Survival");
		TOGGLE_WINGS.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(TOGGLE_WINGS);

		DRAGON_INVENTORY = new KeyMapping("ds.keybind.dragon_inv", GLFW.GLFW_KEY_UNKNOWN, "Dragon Survival");
		DRAGON_INVENTORY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(DRAGON_INVENTORY);

		USE_ABILITY = new KeyMapping("ds.keybind.use_ability", GLFW.GLFW_KEY_C, "Dragon Survival");
		USE_ABILITY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(USE_ABILITY);

		TOGGLE_ABILITIES = new KeyMapping("ds.keybind.toggle_abilities", GLFW.GLFW_KEY_X, "Dragon Survival");
		TOGGLE_ABILITIES.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(TOGGLE_ABILITIES);

		NEXT_ABILITY = new KeyMapping("ds.keybind.next_ability", GLFW.GLFW_KEY_R, "Dragon Survival");
		NEXT_ABILITY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(NEXT_ABILITY);

		PREV_ABILITY = new KeyMapping("ds.keybind.prev_ability", GLFW.GLFW_KEY_F, "Dragon Survival");
		PREV_ABILITY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(PREV_ABILITY);

		ABILITY1 = new KeyMapping("ds.keybind.ability1", GLFW.GLFW_KEY_UNKNOWN, "Dragon Survival");
		ABILITY1.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(ABILITY1);

		ABILITY2 = new KeyMapping("ds.keybind.ability2", GLFW.GLFW_KEY_UNKNOWN, "Dragon Survival");
		ABILITY2.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(ABILITY2);

		ABILITY3 = new KeyMapping("ds.keybind.ability3", GLFW.GLFW_KEY_UNKNOWN, "Dragon Survival");
		ABILITY3.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(ABILITY3);

		ABILITY4 = new KeyMapping("ds.keybind.ability4", GLFW.GLFW_KEY_UNKNOWN, "Dragon Survival");
		ABILITY4.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(ABILITY4);

		SPIN_ABILITY = new KeyMapping("ds.keybind.spin", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "Dragon Survival");
		SPIN_ABILITY.setKeyConflictContext(KeyConflictContext.GUI);
		ClientRegistry.registerKeyBinding(SPIN_ABILITY);

		FREE_LOOK = new KeyMapping("ds.keybind.free_look", GLFW.GLFW_KEY_LEFT_ALT, "Dragon Survival");
		FREE_LOOK.setKeyConflictContext(KeyConflictContext.IN_GAME);
		ClientRegistry.registerKeyBinding(FREE_LOOK);
	}

	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent keyInputEvent){
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = Minecraft.getInstance().player;

		if(player == null || !DragonUtils.isDragon(minecraft.player))
			return;

		DragonStateHandler dragonStateHandler = DragonUtils.getHandler(player);

		if(DRAGON_INVENTORY.consumeClick()){
			if(minecraft.screen == null){
				NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
			}else{
				player.closeContainer();
			}

		}else if(TOGGLE_ABILITIES.consumeClick()){
			dragonStateHandler.getMagic().setRenderAbilities(!dragonStateHandler.getMagic().isRenderAbilities());
			NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));

		}else if(NEXT_ABILITY.consumeClick()){
			int nextSlot = dragonStateHandler.getMagic().getSelectedAbilitySlot() == 3 ? 0 : dragonStateHandler.getMagic().getSelectedAbilitySlot() + 1;
			dragonStateHandler.getMagic().setSelectedAbilitySlot(nextSlot);
			NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));

		}else if(PREV_ABILITY.consumeClick()){
			int nextSlot = dragonStateHandler.getMagic().getSelectedAbilitySlot() == 0 ? 3 : dragonStateHandler.getMagic().getSelectedAbilitySlot() - 1;
			dragonStateHandler.getMagic().setSelectedAbilitySlot(nextSlot);
			NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));

		}


		if(!alternateCastMode){
			if(ABILITY1.consumeClick()){
				dragonStateHandler.getMagic().setSelectedAbilitySlot(0);
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
			}else if(ABILITY2.consumeClick()){
				dragonStateHandler.getMagic().setSelectedAbilitySlot(1);
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
			}else if(ABILITY3.consumeClick()){
				dragonStateHandler.getMagic().setSelectedAbilitySlot(2);
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
			}else if(ABILITY4.consumeClick()){
				dragonStateHandler.getMagic().setSelectedAbilitySlot(3);
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
			}
		}else{
			if(ABILITY1.isDown()){
				if(dragonStateHandler.getMagic().getSelectedAbilitySlot() != 0){
					dragonStateHandler.getMagic().setSelectedAbilitySlot(0);
					NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
				}
			}else if(ABILITY2.isDown()){
				if(dragonStateHandler.getMagic().getSelectedAbilitySlot() != 1){
					dragonStateHandler.getMagic().setSelectedAbilitySlot(1);
					NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
				}
			}else if(ABILITY3.isDown()){
				if(dragonStateHandler.getMagic().getSelectedAbilitySlot() != 2){
					dragonStateHandler.getMagic().setSelectedAbilitySlot(2);
					NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
				}
			}else if(ABILITY4.isDown()){
				if(dragonStateHandler.getMagic().getSelectedAbilitySlot() != 3){
					dragonStateHandler.getMagic().setSelectedAbilitySlot(3);
					NetworkHandler.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getMagic().getSelectedAbilitySlot(), dragonStateHandler.getMagic().isRenderAbilities()));
				}
			}
		}
	}
}