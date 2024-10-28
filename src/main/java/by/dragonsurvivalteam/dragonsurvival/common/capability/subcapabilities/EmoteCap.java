package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.emotes.Emote;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEmotes;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class EmoteCap extends SubCap {
	public static final int MAX_EMOTES = 4;
	public boolean emoteMenuOpen = false;

	public Emote[] currentEmotes = new Emote[MAX_EMOTES];
	public Integer[] emoteTicks = new Integer[MAX_EMOTES];
	public ConcurrentHashMap<String, Integer> emoteKeybinds = new ConcurrentHashMap<>();

	public EmoteCap(DragonStateHandler handler) {
		super(handler);
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();

		for (int i = 0; i < MAX_EMOTES; i++) {
			if (currentEmotes[i] != null) {
				tag.putString("emote_" + i, currentEmotes[i].animation);
			}

			if (emoteTicks[i] != null) {
				tag.putInt("emote_tick_" + i, emoteTicks[i]);
			}
		}

		tag.putBoolean("emoteOpen", emoteMenuOpen);

		for (Emote emote : DSEmotes.EMOTES) {
			tag.putInt("emote_keybind_" + emote.id, emoteKeybinds.getOrDefault(emote.id, -1));
		}

		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		for (int i = 0; i < MAX_EMOTES; i++) {
			String emoteId = tag.contains("emote_" + i) ? tag.getString("emote_" + i) : null;
			int emoteTick = tag.contains("emote_tick_" + i) ? tag.getInt("emote_tick_" + i) : 0;
			Emote emote = null;

			if (emoteId != null) {
				emote = DSEmotes.EMOTES.stream().filter(s -> Objects.equals(s.animation, emoteId)).findFirst().orElseGet(() -> {
					Emote em = new Emote();
					em.animation = emoteId;
					return em;
				});
			}

			currentEmotes[i] = emote;
			emoteTicks[i] = emoteTick;
		}

		emoteMenuOpen = tag.getBoolean("emoteOpen");

		for (Emote emote : DSEmotes.EMOTES) {
			int num = tag.getInt("emote_keybind_" + emote.id);
			emoteKeybinds.put(emote.id, num);
		}
	}
}