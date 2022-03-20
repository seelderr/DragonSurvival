package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.AlertExceptHunters;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class SquireEntity extends Hunter{
	public SquireEntity(EntityType<? extends CreatureEntity> entityType, World world){
		super(entityType, world);
	}

	@Override
	protected void registerGoals(){
		super.registerGoals();
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		goalSelector.addGoal(8, new AlertExceptHunters<>(this, HunterHoundEntity.class, KnightEntity.class, ShooterEntity.class));
	}

	protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance){
		setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
	}
}