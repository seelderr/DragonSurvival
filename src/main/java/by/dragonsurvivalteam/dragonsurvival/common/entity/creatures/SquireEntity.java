package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures;

import by.dragonsurvivalteam.dragonsurvival.common.entity.goals.AlertExceptHunters;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class Squire extends Hunter{
	public Squire(EntityType<? extends Mob> entityType, Level world){
		super(entityType, world);
	}

	@Override
	protected void registerGoals(){
		super.registerGoals();
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		goalSelector.addGoal(8, new AlertExceptHunters<>(this, HunterHound.class, Knight.class, Shooter.class));
	}

	protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance){
		setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
	}
}