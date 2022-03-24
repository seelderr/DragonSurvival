package by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities;

import by.dragonsurvivalteam.dragonsurvival.client.emotes.Emote;
import by.dragonsurvivalteam.dragonsurvival.client.emotes.EmoteRegistry;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmoteCap extends SubCap{
	public boolean emoteMenuOpen = false;

	public static final int MAX_EMOTES = 4;
	public CopyOnWriteArrayList<Emote> currentEmotes = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Integer> emoteTicks = new CopyOnWriteArrayList<>();
	public ConcurrentHashMap<String, Integer> emoteKeybinds = new ConcurrentHashMap<>();

	public EmoteCap(DragonStateHandler handler){
		super(handler);
	}

	@Override
	public CompoundNBT writeNBT(){
		CompoundNBT tag = new CompoundNBT();

		tag.putInt("emoteCount", currentEmotes.size());

		for(int i = 0; i < MAX_EMOTES; i++){
			if(currentEmotes.size() > i){
				tag.putString("emote_" + i, currentEmotes.get(i).animation);
				tag.putInt("emote_tick_" + i, emoteTicks.size() > i ? emoteTicks.get(i) : 0);
			}
		}

		tag.putBoolean("emoteOpen", emoteMenuOpen);

		for(Emote emote : EmoteRegistry.EMOTES){
			tag.putInt("emote_keybind_" + emote.id, emoteKeybinds.getOrDefault(emote.id, -1));
		}

		return tag;
	}
	@Override
	public void readNBT(CompoundNBT tag){
		int count = tag.getInt("emoteCount");

		currentEmotes = new CopyOnWriteArrayList<>();
		emoteTicks = new CopyOnWriteArrayList<>();

		for(int i = 0; i < count; i++){
			String emoteId = tag.getString("emote_" + i);
			int emoteTick = tag.getInt("emote_tick_" + i);

			Emote emote = EmoteRegistry.EMOTES.stream().filter((s) -> Objects.equals(s.animation, emoteId)).findFirst().orElseGet(() -> {
				Emote em = new Emote();
				em.animation = emoteId;
				return em;
			});

			currentEmotes.add(0, emote);
			emoteTicks.add(0, emoteTick);
		}

		emoteMenuOpen = tag.getBoolean("emoteOpen");

		for(Emote emote : EmoteRegistry.EMOTES){
			int num = tag.getInt("emote_keybind_" + emote.id);

			if(num != -1){
				emoteKeybinds.put(emote.id, num);
			}
		}
	}
}