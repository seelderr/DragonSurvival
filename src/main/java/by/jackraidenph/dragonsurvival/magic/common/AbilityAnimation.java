package by.jackraidenph.dragonsurvival.magic.common;

public class AbilityAnimation
{
	public String animationKey;
	public int duration;
	public boolean locksNeck;
	
	public AbilityAnimation(String animationKey, int duration, boolean locksNeck)
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
