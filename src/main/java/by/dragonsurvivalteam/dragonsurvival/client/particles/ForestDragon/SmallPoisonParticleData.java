package by.dragonsurvivalteam.dragonsurvival.client.particles.ForestDragon;

import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public class SmallPoisonParticleData implements IParticleData{
	public static final IDeserializer<SmallPoisonParticleData> DESERIALIZER = new IDeserializer<SmallPoisonParticleData>(){
		public SmallPoisonParticleData fromCommand(ParticleType<SmallPoisonParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			reader.expect(' ');
			boolean swirls = reader.readBoolean();
			return new SmallPoisonParticleData(duration, swirls);
		}

		public SmallPoisonParticleData fromNetwork(ParticleType<SmallPoisonParticleData> particleTypeIn, PacketBuffer buffer){
			return new SmallPoisonParticleData(buffer.readFloat(), buffer.readBoolean());
		}
	};

	private final float duration;
	private final boolean swirls;

	public static Codec<SmallPoisonParticleData> CODEC(ParticleType<SmallPoisonParticleData> particleType){
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SmallPoisonParticleData::getDuration), Codec.BOOL.fieldOf("swirls").forGetter(SmallPoisonParticleData::getSwirls)).apply(codecBuilder, SmallPoisonParticleData::new));
	}

	public SmallPoisonParticleData(float duration, boolean spins){
		this.duration = duration;
		this.swirls = spins;
	}	@Override
	public void writeToNetwork(PacketBuffer buffer){
		buffer.writeFloat(this.duration);
		buffer.writeBoolean(this.swirls);
	}

	@OnlyIn( Dist.CLIENT )
	public float getDuration(){
		return this.duration;
	}	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %b", Registry.PARTICLE_TYPE.getKey(this.getType()), this.duration, this.swirls);
	}

	@OnlyIn( Dist.CLIENT )
	public boolean getSwirls(){
		return this.swirls;
	}	@Override
	public ParticleType<SmallPoisonParticleData> getType(){
		return DSParticles.POISON.get();
	}






}