package by.jackraidenph.dragonsurvival.emotes;

public class Emote
{
	public int duration = -1;
	
	public boolean loops = false;
	public boolean locksHead = false;
	public boolean thirdPerson = false;
	
	public String name;
	public String animation;
	public Sound sound = null;
	
	
	public static class Sound{
		public String key;
		public int interval;
		
		public float volume = 1F;
		public float pitch = 1F;
	}
}
