package by.dragonsurvivalteam.dragonsurvival.common.items;

import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.util.BlockPosHelper;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RoyalSummonItem extends Item
{
	private Supplier<EntityType<? extends Mob>> entityType;
	
	public RoyalSummonItem(Supplier<EntityType<? extends Mob>> entityType, Properties pProperties)
	{
		super(pProperties);
		this.entityType = entityType;
	}
	private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		if(entityType != null) {
			ItemStack itemstack = pPlayer.getItemInHand(pHand);
			HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.ANY);
			if (hitresult.getType() == HitResult.Type.MISS) {
				return InteractionResultHolder.pass(itemstack);
			} else {
				Vec3 vec3 = pPlayer.getViewVector(1.0F);
				List<Entity> list = pLevel.getEntities(pPlayer, pPlayer.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
				if (!list.isEmpty()) {
					Vec3 vec31 = pPlayer.getEyePosition();
					
					for (Entity entity : list) {
						AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
						if (aabb.contains(vec31)) {
							return InteractionResultHolder.pass(itemstack);
						}
					}
				}
				
				if (hitresult.getType() == HitResult.Type.BLOCK) {
					Mob ent = entityType.get().create(pLevel);
					ent.setPos(hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
					ent.addEffect(new MobEffectInstance(DSEffects.ROYAL_DEPARTURE, Functions.minutesToTicks(ServerConfig.royalDisappearInMinutes)));
					if (!pLevel.isClientSide()) {
						ent.finalizeSpawn((ServerLevelAccessor)pLevel, pLevel.getCurrentDifficultyAt(ent.blockPosition()), MobSpawnType.SPAWN_EGG, (SpawnGroupData)null, (CompoundTag)null);
						pLevel.addFreshEntity(ent);
						pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, BlockPosHelper.get(hitresult.getLocation()));
						if (!pPlayer.getAbilities().instabuild) {
							itemstack.shrink(1);
						}
					}
					
					pPlayer.awardStat(Stats.ITEM_USED.get(this));
					return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
				} else {
					return InteractionResultHolder.pass(itemstack);
				}
			}
		}else{
			return super.use(pLevel, pPlayer, pHand);
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable
	Level world, List<Component> list, TooltipFlag tooltipFlag){
		super.appendHoverText(stack, world, list, tooltipFlag);
		String langKey = "ds.description." + ResourceHelper.getKey(this).getPath();
		list.add(Component.translatable(langKey));
	}
}