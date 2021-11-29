package by.jackraidenph.dragonsurvival.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

	// Movement
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> firstPersonBodyMovement;
	public final ForgeConfigSpec.ConfigValue<DragonBodyMovementType> thirdPersonBodyMovement;

	public final ForgeConfigSpec.BooleanValue dragonNameTags;
	public final ForgeConfigSpec.BooleanValue renderInFirstPerson;
	public final ForgeConfigSpec.BooleanValue notifyWingStatus;
	public final ForgeConfigSpec.BooleanValue clientDebugMessages;
	public final ForgeConfigSpec.BooleanValue dragonInventory;
	
	public final ForgeConfigSpec.IntValue casterBarXPos;
	public final ForgeConfigSpec.IntValue casterBarYPos;
	
	ClientConfig(ForgeConfigSpec.Builder builder) {
		builder.push("client");
		//For people who use first person view mods
		renderInFirstPerson = builder.comment("Render dragon model in first person")
				.define("renderFirstPerson", true);
		notifyWingStatus = builder.comment("Notifies of wing status in chat message").define("notifyWingStatus", true);
		clientDebugMessages = builder.define("Enable client-side debug messages", false);
		
		dragonInventory = builder
				.comment("Should the default inventory be replaced as a dragon?")
				.define("dragonInventory", true);
		
		builder.push("ui");
		casterBarXPos = builder
				.comment("The x position of the cast bar from the center of the screen")
				.defineInRange("casterBarXPos", 49, -1000, 1000);
		
		casterBarYPos = builder
				.comment("The y position of the cast bar from the bottom of the screen")
				.defineInRange("casterBarYPos", 96, -1000, 1000);
		
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
