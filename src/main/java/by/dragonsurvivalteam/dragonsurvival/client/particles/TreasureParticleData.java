package by.dragonsurvivalteam.dragonsurvival.client.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/TreasureParticleData.java
public class TreasureParticleData  implements ParticleOptions
{
=======
public class TreasureParticleData implements IParticleData{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/TreasureParticleData.java
	public static final Codec<TreasureParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> {
		return p_239803_0_.group(Codec.FLOAT.fieldOf("r").forGetter((p_239807_0_) -> {
			return p_239807_0_.r;
		}), Codec.FLOAT.fieldOf("g").forGetter((p_239806_0_) -> {
			return p_239806_0_.g;
		}), Codec.FLOAT.fieldOf("b").forGetter((p_239805_0_) -> {
			return p_239805_0_.b;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_239804_0_) -> {
			return p_239804_0_.scale;
		})).apply(p_239803_0_, TreasureParticleData::new);
	});
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/TreasureParticleData.java
	public static final ParticleOptions.Deserializer<TreasureParticleData> DESERIALIZER = new ParticleOptions.Deserializer<TreasureParticleData>() {
		public TreasureParticleData fromCommand(ParticleType<TreasureParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException
		{
=======
	public static final IParticleData.IDeserializer<TreasureParticleData> DESERIALIZER = new IParticleData.IDeserializer<TreasureParticleData>(){
		public TreasureParticleData fromCommand(ParticleType<TreasureParticleData> p_197544_1_, StringReader p_197544_2_) throws CommandSyntaxException{
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/TreasureParticleData.java
			p_197544_2_.expect(' ');
			float f = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f1 = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f2 = (float)p_197544_2_.readDouble();
			p_197544_2_.expect(' ');
			float f3 = (float)p_197544_2_.readDouble();
			return new TreasureParticleData(f, f1, f2, f3);
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/TreasureParticleData.java
		
		public TreasureParticleData fromNetwork(ParticleType<TreasureParticleData> p_197543_1_, FriendlyByteBuf p_197543_2_) {
=======

		public TreasureParticleData fromNetwork(ParticleType<TreasureParticleData> p_197543_1_, PacketBuffer p_197543_2_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/TreasureParticleData.java
			return new TreasureParticleData(p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat(), p_197543_2_.readFloat());
		}
	};
	private final float r;
	private final float g;
	private final float b;
	private final float scale;

	public TreasureParticleData(float p_i47950_1_, float p_i47950_2_, float p_i47950_3_, float p_i47950_4_){
		this.r = p_i47950_1_;
		this.g = p_i47950_2_;
		this.b = p_i47950_3_;
		this.scale = Mth.clamp(p_i47950_4_, 0.01F, 4.0F);
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/particles/TreasureParticleData.java
	
	public void writeToNetwork(FriendlyByteBuf p_197553_1_) {
=======

	@OnlyIn( Dist.CLIENT )
	public float getR(){
		return this.r;
	}	public void writeToNetwork(PacketBuffer p_197553_1_){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/particles/TreasureParticleData.java
		p_197553_1_.writeFloat(this.r);
		p_197553_1_.writeFloat(this.g);
		p_197553_1_.writeFloat(this.b);
		p_197553_1_.writeFloat(this.scale);
	}

	@OnlyIn( Dist.CLIENT )
	public float getG(){
		return this.g;
	}	public String writeToString(){
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.r, this.g, this.b, this.scale);
	}

	@OnlyIn( Dist.CLIENT )
	public float getB(){
		return this.b;
	}	public ParticleType<TreasureParticleData> getType(){
		return DSParticles.TREASURE.get();
	}

	@OnlyIn( Dist.CLIENT )
	public float getScale(){
		return this.scale;
	}






}