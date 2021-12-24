package by.jackraidenph.dragonsurvival.common.magic.common;

public class AbilityAnimation
{
	public String animationKey;
	public double duration;
	public boolean locksNeck;
	
	public AbilityAnimation(String animationKey, double duration, boolean locksNeck)
	{
		this(animationKey, locksNeck);
		this.duration = duration;
	}
	
	public AbilityAnimation(String animationKey, boolean locksNeck)
	{
		this.animationKey = animationKey;
		this.locksNeck = locksNeck;
	}
}
