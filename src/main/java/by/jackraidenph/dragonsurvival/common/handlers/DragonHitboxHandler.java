package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class DragonHitboxHandler
{
	public static ConcurrentHashMap<Integer, Integer> dragonHitboxes = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent playerTickEvent){
		PlayerEntity player = playerTickEvent.player;
		
		if(!player.level.isClientSide) {
			if (DragonStateProvider.isDragon(player)) {
				if (dragonHitboxes.containsKey(player.getId())) {
					int id = dragonHitboxes.get(player.getId());
					Entity ent = player.level.getEntity(id);
					
					if (ent == null || !(ent instanceof DragonHitBox) || !ent.isAlive() || ent.removed) {
						dragonHitboxes.remove(player.getId());
						addPlayerHitbox(player);
					}
				}else{
					dragonHitboxes.remove(player.getId());
					addPlayerHitbox(player);
				}
			}
		}
	}
	
	private static void addPlayerHitbox(PlayerEntity player){
		DragonHitBox hitbox = DSEntities.DRAGON_HITBOX.create(player.level);
		hitbox.copyPosition(player);
		hitbox.player = player;
		hitbox.setPlayerId(player.getId());
		player.level.addFreshEntity(hitbox);
		dragonHitboxes.put(player.getId(), hitbox.getId());
	}
	
	@SubscribeEvent
	public static void getDragonSize(EntityEvent.Size event)
	{
		if (!(event.getEntity() instanceof DragonHitboxPart)) return;
		
		DragonHitboxPart part = (DragonHitboxPart)event.getEntity();
		
		if (part != null && part.getParent() != null && part.getParent().player != null) {
			PlayerEntity player = part.getParent().player;
			
			DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
				if (!dragonStateHandler.isDragon()) return;
				double size = dragonStateHandler.getSize();
				double height = DragonSizeHandler.calculateDragonHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
				double width = DragonSizeHandler.calculateDragonWidth(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
				
				switch (part.name) {
					case "body":
						width *= 2;
						break;
					case "head":
						height = width;
						break;
					case "tail1":
						height /= 3;
						break;
					case "tail2":
						height /= 3;
						width *= 0.8;
						break;
					case "tail3":
						height /= 3;
						width *= 0.6;
						break;
				}
				
				event.setNewSize(new EntitySize((float)(Math.round(width * 100.0D) / 100.0D), (float)(Math.round(height * 100.0D) / 100.0D), false));
				player.refreshDimensions();
			});
		}
	}
}
