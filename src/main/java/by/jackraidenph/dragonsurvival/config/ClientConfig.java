package by.jackraidenph.dragonsurvival.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

	// Movement
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> firstPersonBodyMovement;
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> thirdPersonBodyMovement;

	public final ForgeConfigSpec.BooleanValue dragonNameTags;
	public final ForgeConfigSpec.BooleanValue renderInFirstPerson;
	
	public final ForgeConfigSpec.BooleanValue notifyWingStatus;
	public final ForgeConfigSpec.BooleanValue jumpToFly;
	public final ForgeConfigSpec.BooleanValue lookAtSkyForFlight;
	public final ForgeConfigSpec.BooleanValue flightZoomEffect;
	public final ForgeConfigSpec.BooleanValue renderOtherPlayerRotation;
	
	public final ForgeConfigSpec.BooleanValue clientDebugMessages;
	
	public final ForgeConfigSpec.BooleanValue alternateHeldItem;
	
	public final ForgeConfigSpec.BooleanValue renderDragonClaws;
	public final ForgeConfigSpec.BooleanValue renderNewbornSkin;
	public final ForgeConfigSpec.BooleanValue renderYoungSkin;
	public final ForgeConfigSpec.BooleanValue renderAdultSkin;
	public final ForgeConfigSpec.BooleanValue renderOtherPlayerSkins;
	
	public final ForgeConfigSpec.BooleanValue dragonInventory;
	public final ForgeConfigSpec.BooleanValue dragonTabs;
	public final ForgeConfigSpec.BooleanValue inventoryToggle;
	
	public final ForgeConfigSpec.IntValue castbarXOffset;
	public final ForgeConfigSpec.IntValue castbarYOffset;
	
	public final ForgeConfigSpec.IntValue skillbarXOffset;
	public final ForgeConfigSpec.IntValue skillbarYOffset;
	
	public final ForgeConfigSpec.IntValue growthXOffset;
	public final ForgeConfigSpec.IntValue growthYOffset;
	
	public final ForgeConfigSpec.IntValue emoteXOffset;
	public final ForgeConfigSpec.IntValue emoteYOffset;
	
	public final ForgeConfigSpec.IntValue spinCooldownXOffset;
	public final ForgeConfigSpec.IntValue spinCooldownYOffset;
	
	ClientConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");
		//For people who use first person view mods
		renderInFirstPerson = builder.comment("Render dragon model in first person. If your own tail scares you, write false")
				.define("renderFirstPerson", true);
		notifyWingStatus = builder.comment("Notifies of wing status in chat message").define("notifyWingStatus", false);
		clientDebugMessages = builder.define("Enable client-side debug messages", false);
		
		jumpToFly = builder.comment("Should flight be activated when jumping in the air").define("jumpToFly", false);
		lookAtSkyForFlight = builder.comment("Is it required to look up to start flying while jumping, requires that jumpToFly is on").define("lookAtSkyForFlight", false);
		flightZoomEffect = builder.comment("Should the zoom effect while gliding as a dragon be enabled").define("flightZoomEffect", true);
		renderOtherPlayerRotation = builder.comment("Should the rotation effect during gliding of other players be shown?").define("renderOtherPlayerRotation", true);
		
		dragonInventory = builder
				.comment("Should the default inventory be replaced as a dragon?")
				.define("dragonInventory", true);
		
		dragonTabs = builder
				.comment("Should dragon tabs be added to the default player inventory?")
				.define("dragonTabs", true);
		
		inventoryToggle = builder
				.comment("Should the buttons for toggeling between dragon and normaly inventory be added?")
				.define("inventoryToggle", true);
		
		alternateHeldItem = builder
				.comment("Should held items be rendered as if you are in third-person even in first person as a dragon?")
				.define("alternateHeldItem", false);
		
		
		renderDragonClaws = builder
				.comment("Should the tools on the claws and teeth be rendered for your dragon?")
				.define("renderDragonClaws", true);
		
		renderNewbornSkin = builder
				.comment("Do you want your dragon skin to be rendered as a newborn dragon?")
				.define("renderNewbornSkin", true);
		
		renderYoungSkin = builder
				.comment("Do you want your dragon skin to be rendered as a young dragon?")
				.define("renderYoungSkin", true);
		
		renderAdultSkin = builder
				.comment("Do you want your dragon skin to be rendered as a adult dragon?")
				.define("renderAdultSkin", true);
		
		renderOtherPlayerSkins = builder
				.comment("Should other player skins be rendered?")
				.define("renderOtherPlayerSkins", true);
		
		
		builder.push("ui");
		castbarXOffset = builder
				.comment("Offset the x position of the cast bar in relation to its normal position")
				.defineInRange("casterBarXPos", 0, -1000, 1000);
		
		castbarYOffset = builder
				.comment("Offset the y position of the cast bar in relation to its normal position")
				.defineInRange("casterBarYPos", 0, -1000, 1000);
		
		skillbarXOffset = builder
				.comment("Offset the x position of the magic skill bar in relation to its normal position")
				.defineInRange("skillbarXOffset", 0, -1000, 1000);
		
		skillbarYOffset = builder
				.comment("Offset the y position of the magic skill bar in relation to its normal position")
				.defineInRange("skillbarYOffset", 0, -1000, 1000);
		
		growthXOffset = builder
				.comment("Offset the x position of the item growth icon in relation to its normal position")
				.defineInRange("growthXOffset", 0, -1000, 1000);
		
		growthYOffset = builder
				.comment("Offset the y position of the item growth icon in relation to its normal position")
				.defineInRange("growthYOffset", 0, -1000, 1000);
		
		emoteXOffset = builder
				.comment("Offset the x position of the emote button in relation to its normal position")
				.defineInRange("emoteXOffset", 0, -1000, 1000);
		
		emoteYOffset = builder
				.comment("Offset the y position of the emote button in relation to its normal position")
				.defineInRange("emoteYOffset", 0, -1000, 1000);
		
		spinCooldownXOffset = builder
				.comment("Offset the x position of the spin cooldown indicator in relation to its normal position")
				.defineInRange("spinCooldownXOffset", 0, -1000, 1000);
		
		spinCooldownYOffset = builder
				.comment("Offset the y position of the spin cooldown indicator in relation to its normal position")
				.defineInRange("spinCooldownYOffset", 0, -1000, 1000);
		
		// Movement
		builder.push("movement");
		firstPersonBodyMovement = builder
				.comment("The type of body movement you use while in first person as a dragon.")
				.defineEnum("firstPersonMovement", DragonBodyMovementType.VANILLA, DragonBodyMovementType.values());
		thirdPersonBodyMovement = builder
				.comment("The type of body movement you use while in third person as a dragon.")
				.defineEnum("thirdPersonMovement", DragonBodyMovementType.DRAGON, DragonBodyMovementType.values());
		builder.pop().push("nametag");
		dragonNameTags = builder
				.comment("Show name tags for dragons.")
				.define("dragonNameTags", false);
		builder.pop();
	}
	
}
