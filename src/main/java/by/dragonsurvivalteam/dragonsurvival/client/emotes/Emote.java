package by.dragonsurvivalteam.dragonsurvival.client.emotes;

public class Emote {
	public int duration = -1;
	public double speed = 1;

	public Requirement requirements;

	public Mirror mirror;

	public boolean loops = false;
	public boolean locksHead = false;
	public boolean locksTail = true;
	public boolean thirdPerson = false;
	public boolean blend = false;

	public String id;
	public String name;
	public String animation;
	public Sound sound = null;

	public static class Sound {
		public String key;
		public int interval;

		public float volume = 1F;
		public float pitch = 1F;
	}

	public static class Requirement {
		public String[] type;
		public String[] model;
		public String[] age;
	}

	public static class Mirror {
		public boolean xPos;
		public boolean yPos;
		public boolean zPos;

		public boolean xRot;
		public boolean yRot;
		public boolean zRot;

		public boolean xScale;
		public boolean yScale;
		public boolean zScale;
	}
}