package by.dragonsurvivalteam.dragonsurvival.client.render.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber
public class AnimationTimer {
	public static CopyOnWriteArrayList<AnimationTimer> timers = new CopyOnWriteArrayList<>();
	protected ConcurrentHashMap<String, Double> animationTimes = new ConcurrentHashMap<>();

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void renderTick(final ClientTickEvent event) {
		if (event.phase == Phase.START) {
			return;
		}

		for (AnimationTimer timer : timers) {
			timer.animationTimes.keySet().forEach(key -> {
				timer.animationTimes.computeIfPresent(key, (animation, tick) -> tick - 1);

				if (timer.animationTimes.get(key) <= 0) {
					timer.animationTimes.remove(key);
				}
			});

			if (timer.animationTimes.isEmpty()) {
				timers.remove(timer);
			}
		}
	}

	public double getDuration(final String animation) {
		return animationTimes.getOrDefault(animation, 0.0);
	}

	public void putAnimation(final String animation, final Double ticks) {
		putDuration(animation, ticks);

		if (!timers.contains(this)) {
			timers.add(this);
		}
	}

	protected void putDuration(final String animation, final Double ticks) {
		animationTimes.put(animation, ticks);
	}
}