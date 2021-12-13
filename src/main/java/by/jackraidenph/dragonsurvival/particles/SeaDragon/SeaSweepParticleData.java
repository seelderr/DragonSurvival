package by.jackraidenph.dragonsurvival.particles.SeaDragon;

import by.jackraidenph.dragonsurvival.registration.ParticleRegistry;
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

public class SeaSweepParticleData implements IParticleData
{
	public static final IDeserializer<SeaSweepParticleData> DESERIALIZER = new IDeserializer<SeaSweepParticleData>()
	{
		public SeaSweepParticleData fromCommand(ParticleType<SeaSweepParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException
		{
			reader.expect(' ');
			float duration = (float)reader.readDouble();
			return new SeaSweepParticleData(duration);
		}
		
		public SeaSweepParticleData fromNetwork(ParticleType<SeaSweepParticleData> particleTypeIn, PacketBuffer buffer)
		{
			return new SeaSweepParticleData(buffer.readFloat());
		}
	};
	
	private final float duration;
	
	public SeaSweepParticleData(float duration)
	{
		this.duration = duration;
	}
	
	@Override
	public void writeToNetwork(PacketBuffer buffer)
	{
		buffer.writeFloat(this.duration);
	}
	
	@SuppressWarnings( "deprecation" )
	@Override
	public String writeToString()
	{
		return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.duration);
	}
	
	@Override
	public ParticleType<SeaSweepParticleData> getType()
	{
		return ParticleRegistry.SEA_SWEEP.get();
	}
	
	@OnlyIn( Dist.CLIENT )
	public float getDuration()
	{
		return this.duration;
	}
	
	public static Codec<SeaSweepParticleData> CODEC(ParticleType<SeaSweepParticleData> particleType)
	{
		return RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(Codec.FLOAT.fieldOf("duration").forGetter(SeaSweepParticleData::getDuration)).apply(codecBuilder, SeaSweepParticleData::new));
	}
}
