package by.jackraidenph.dragonsurvival.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
	public final ForgeConfigSpec.BooleanValue dragonNameTags;
	public final ForgeConfigSpec.BooleanValue renderInFirstPerson;
	public final ForgeConfigSpec.BooleanValue firstPersonRotation;
	public final ForgeConfigSpec.BooleanValue renderFirstPersonFlight;
	
	public final ForgeConfigSpec.BooleanValue notifyWingStatus;
	public final ForgeConfigSpec.BooleanValue jumpToFly;
	public final ForgeConfigSpec.BooleanValue lookAtSkyForFlight;
	public final ForgeConfigSpec.BooleanValue renderOtherPlayerRotation;
	
	public final ForgeConfigSpec.BooleanValue flightZoomEffect;
	public final ForgeConfigSpec.BooleanValue flightCameraMovement;
	
	public final ForgeConfigSpec.BooleanValue ownSpinParticles;
	public final ForgeConfigSpec.BooleanValue othersSpinParticles;
	
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
	
	public final ForgeConfigSpec.IntValue manabarXOffset;
	public final ForgeConfigSpec.IntValue manabarYOffset;
	
	public final ForgeConfigSpec.IntValue growthXOffset;
	public final ForgeConfigSpec.IntValue growthYOffset;
	
	public final ForgeConfigSpec.IntValue emoteXOffset;
	public final ForgeConfigSpec.IntValue emoteYOffset;
	
	public final ForgeConfigSpec.IntValue spinCooldownXOffset;
	public final ForgeConfigSpec.IntValue spinCooldownYOffset;
	
	ClientConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client").push("firstperson");
		//For people who use first person view mods
		renderInFirstPerson = builder.comment("Render dragon model in first person. If your own tail scares you, write false").define("renderFirstPerson", true);
		renderFirstPersonFlight = builder.comment("Render dragon model in first person while gliding").define("renderFirstPersonFlight", false);
		firstPersonRotation = builder.comment("Use rotation of your tail in first person, otherwise the tail is always opposite of your camera").define("firstPersonRotation", true);
		
		builder.pop().push("flight");
		notifyWingStatus = builder.comment("Notifies of wing status in chat message").define("notifyWingStatus", false);
		
		jumpToFly = builder.comment("Should flight be activated when jumping in the air").define("jumpToFly", false);
		lookAtSkyForFlight = builder.comment("Is it required to look up to start flying while jumping, requires that jumpToFly is on").define("lookAtSkyForFlight", false);
		renderOtherPlayerRotation = builder.comment("Should the rotation effect during gliding of other players be shown?").define("renderOtherPlayerRotation", true);
		
		flightZoomEffect = builder.comment("Should the zoom effect while gliding as a dragon be enabled").define("flightZoomEffect", true);
		flightCameraMovement = builder.comment("Should the camera movement while gliding as a dragon be enabled").define("flightCameraMovement", true);
		
		ownSpinParticles = builder.comment("Should particles from your own spin attack be displayed for you?").define("ownSpinParticles", true);
		othersSpinParticles = builder.comment("Should other players particles from spin attack be shown for you?").define("othersSpinParticles", true);
		
		builder.pop();
		
		clientDebugMessages = builder.comment("Enable client-side debug messages").define("clientDebugMessages", false);
		
		builder.push("inventory");
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
		
		
		builder.pop().push("rendering");
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
		
		
		builder.pop().push("ui");
		builder.push("magic");
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
		
		manabarXOffset = builder
				.comment("Offset the x position of the mana bar in relation to its normal position")
				.defineInRange("manabarXOffset", 0, -1000, 1000);
		
		manabarYOffset = builder
				.comment("Offset the y position of the mana bar in relation to its normal position")
				.defineInRange("manabarYOffset", 0, -1000, 1000);
		
		builder.pop().push("growth");
		
		growthXOffset = builder
				.comment("Offset the x position of the item growth icon in relation to its normal position")
				.defineInRange("growthXOffset", 0, -1000, 1000);
		
		growthYOffset = builder
				.comment("Offset the y position of the item growth icon in relation to its normal position")
				.defineInRange("growthYOffset", 0, -1000, 1000);
		
		builder.pop().push("emotes");
		
		emoteXOffset = builder
				.comment("Offset the x position of the emote button in relation to its normal position")
				.defineInRange("emoteXOffset", 0, -1000, 1000);
		
		emoteYOffset = builder
				.comment("Offset the y position of the emote button in relation to its normal position")
				.defineInRange("emoteYOffset", 0, -1000, 1000);
		
		builder.pop().push("spin");
		
		spinCooldownXOffset = builder
				.comment("Offset the x position of the spin cooldown indicator in relation to its normal position")
				.defineInRange("spinCooldownXOffset", 0, -1000, 1000);
		
		spinCooldownYOffset = builder
				.comment("Offset the y position of the spin cooldown indicator in relation to its normal position")
				.defineInRange("spinCooldownYOffset", 0, -1000, 1000);
		
		builder.pop().pop().push("nametag");
		
		dragonNameTags = builder
				.comment("Show name tags for dragons.")
				.define("dragonNameTags", false);
		builder.pop();
	}
}
