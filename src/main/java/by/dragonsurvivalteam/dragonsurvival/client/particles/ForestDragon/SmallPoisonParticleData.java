package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

import by.dragonsurvivalteam.dragonsurvival.registry.DSParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class SmallPoisonParticleData implements ParticleOptions{
	public static final Deserializer<SmallPoisonParticleData> DESERIALIZER = new Deserializer<SmallPoisonParticleData>(){
		@Override
		public SmallPoisonParticleData fromCommand(ParticleType<SmallPoisonParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new SmallPoisonParticleData(duration, swirls);
		}

		@Override
		public SmallPoisonParticleData fromNetwork(ParticleType<SmallPoisonParticleData> particleTypeIn, FriendlyByteBuf buffer){
			return new SmallPoisonParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<SmallPoisonParticleData> CODEC(ParticleType<SmallPoisonParticleData> particleType){
		return RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SmallPoisonParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(SmallPoisonParticleData::getSwirls)).apply(codecBuilder, SmallPoisonParticleData::new));
	}

	public SmallPoisonParticleData(float duration, boolean spins){
		this.duration = duration;
		swirls = spins;
	}

	@OnlyIn( Dist.CLIENT )
	public float getDuration(){
		return duration;
	}

	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls(){
		return swirls;
	}	@Override
	public void writeToNetwork(FriendlyByteBuf buffer){
		buffer.writeFloat(duration);
		buffer.writeBoolean(swirls);
	}



	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %b", ForgeRegistries.PARTICLE_TYPES.getKey(getType()), duration, swirls);
	}


	@Override
	public ParticleType<SmallPoisonParticleData> getType(){
		return DSParticles.POISON.get();
	}
}