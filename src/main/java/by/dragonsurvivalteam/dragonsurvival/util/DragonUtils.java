package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.EntityStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.google.common.base.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class DragonUtils {
	public static DragonStateHandler getHandler(final Entity entity) {
		if (entity == null) {
			return new DragonStateHandler();
		}

		LazyOptional<DragonStateHandler> cap = DragonStateProvider.getCap(entity);

		return cap.orElse(new DragonStateHandler());
	}

	public static EntityStateHandler getEntityHandler(Entity entity){
		if (entity == null) {
			return new EntityStateHandler();
		}

		LazyOptional<EntityStateHandler> cap = (LazyOptional<EntityStateHandler>) DragonStateProvider.getEntityCap(entity);

		return cap.orElse(new EntityStateHandler());
	}

	public static boolean isDragon(Entity entity){
		if (!(entity instanceof Player)) {
			return false;
		}

		return DragonStateProvider.getCap(entity).filter(DragonStateHandler::isDragon).isPresent();
	}

	public static AbstractDragonType getDragonType(Entity entity){
		return getHandler(entity).getType();
	}

	public static boolean isDragonType(Entity entity, AbstractDragonType dragonType){
		if (!(entity instanceof Player)) {
			return false;
		}

		return isDragonType(getHandler(entity), dragonType);
	}
	
	public static boolean isDragonType(DragonStateHandler stateHandler, AbstractDragonType dragonType){
		return (stateHandler != null && stateHandler.isDragon()) == (dragonType != null) && Objects.equal(stateHandler.getType(), dragonType);
	}

	public static DragonLevel getDragonLevel(Entity entity){
		return getHandler(entity).getLevel();
	}

	/** Converts the supplied {@link Tier#getLevel} to vanilla values (0 to 4) */
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
}