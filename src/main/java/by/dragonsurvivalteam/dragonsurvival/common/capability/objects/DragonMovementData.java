package by.dragonsurvivalteam.dragonsurvival.common.capability.objects;

public class DragonMovementData{
	public double bodyYaw = 0;
	public double headYaw = 0;
	public double headPitch = 0;

	public double headYawLastFrame = 0;
	public double headPitchLastFrame = 0;
	public double bodyYawLastFrame = 0;
	
	public float prevXRot;
	public float prevZRot;
	public float rotLastTick;

	public boolean isFirstPerson = false;
	public boolean bite = false;
	public boolean dig = false;

	public boolean spinLearned;
	public int spinCooldown;
	public int spinAttack;

	public DragonMovementData(){}
}