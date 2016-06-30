package com.okina.client.particle;

import com.okina.multiblock.construct.mode.AlterMode;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleAlterDot extends ParticleBase {

	public float baseScale = 30f;
	private int dyingCount = 0;
	private AlterMode alter;

	public ParticleAlterDot(World world, double x, double y, double z, float baseSize, AlterMode alter) {
		super(world, x, y, z, 0x202020);
		baseScale = baseSize;
		this.alter = alter;
		this.particleAlpha = 0.75f;
		particleMaxAge = 100;
		particleTextureIndexX = 4;
		particleTextureIndexY = 2;
		textureSizeX = 4;
		textureSizeY = 4;
	}

	public ParticleAlterDot(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Float) objects[3], (AlterMode) objects[4]);
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		//		p_70539_4_ = p_70539_4_ + 2;

		float ageScaled = (particleAge + partialTicks) / particleMaxAge;

		updateColor(ageScaled);
		updateTexture(ageScaled);
		updateScale(ageScaled);

		float f6 = particleTextureIndexX / 16.0F;
		float f7 = f6 + textureSizeX / 16F;
		float f8 = particleTextureIndexY / 16.0F;
		float f9 = f8 + textureSizeY / 16F;
		float f10 = 0.1F * particleScale;

		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
		tessellator.addVertexWithUV(f11 - p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 - p_70539_5_ * f10 - p_70539_7_ * f10, f7, f9);
		tessellator.addVertexWithUV(f11 - p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 - p_70539_5_ * f10 + p_70539_7_ * f10, f7, f8);
		tessellator.addVertexWithUV(f11 + p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 + p_70539_5_ * f10 + p_70539_7_ * f10, f6, f8);
		tessellator.addVertexWithUV(f11 + p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 + p_70539_5_ * f10 - p_70539_7_ * f10, f6, f9);
	}

	/**called on every partial tick*/
	@Override
	protected void updateScale(float ageScaled) {
		particleScale = baseScale * dyingCount / 60f;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		particleAge = 0;
		if(alter.processingTicks == -1 || !alter.container.isValid || alter.container.mode2 != alter){
			dyingCount--;
		}else if(dyingCount < 60){
			dyingCount++;
		}
		if(dyingCount < 0){
			setDead();
		}
	}

}
