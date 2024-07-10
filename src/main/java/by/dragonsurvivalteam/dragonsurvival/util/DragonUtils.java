package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import com.google.common.base.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import javax.annotation.Nullable;

public class DragonUtils {

	public static AbstractDragonType getDragonType(Entity entity){
		return DragonStateProvider.getOrGenerateHandler(entity).getType();
	}
	
	public static AbstractDragonType getDragonType(DragonStateHandler handler) {
		return handler.getType();
	}

	public static AbstractDragonBody getDragonBody(Entity entity) {
		return DragonStateProvider.getOrGenerateHandler(entity).getBody();
	}

	public static AbstractDragonBody getDragonBody(DragonStateHandler handler) {
		return handler.getBody();
	}

	public static boolean isDragonType(final Entity entity, final AbstractDragonType typeToCheck) {
		if (!(entity instanceof Player)) {
			return false;
		}

		return isDragonType(DragonStateProvider.getOrGenerateHandler(entity), typeToCheck);
	}
	
	public static boolean isDragonType(final DragonStateHandler playerHandler, final AbstractDragonType typeToCheck) {
		if (playerHandler == null || typeToCheck == null || playerHandler.getType() == null) {
			return false;
		}

		return Objects.equal(playerHandler.getType().getTypeName(), typeToCheck.getTypeName());
	}

	public static boolean isDragonType(final AbstractDragonType playerType, final AbstractDragonType typeToCheck) {
		if (playerType == null || typeToCheck == null) {
			return false;
		}

		return Objects.equal(playerType.getTypeName(), typeToCheck.getTypeName());
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return DragonStateProvider.getOrGenerateHandler(entity).getLevel();
	}

	/** Converts the supplied harvest level to a corresponding vanilla tier */
	public static @Nullable Tier levelToVanillaTier(int level) {
		if (level < 0) {
			return null;
		} else if (level == 0) {
			return Tiers.WOOD;
		} else if (level == 1) {
			return Tiers.STONE;
		} else if (level == 2) {
			return Tiers.IRON;
		} else if (level == 3) {
			return Tiers.DIAMOND;
		}

		return Tiers.NETHERITE;
	}

	/** For client usage */
	public static boolean hasLavaVision() {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player != null) {
			return player.hasEffect(DSEffects.LAVA_VISION);
		}

		return false;
	}
}