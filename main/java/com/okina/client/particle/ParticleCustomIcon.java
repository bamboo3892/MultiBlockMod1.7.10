package com.okina.client.particle;

import com.okina.multiblock.construct.block.ConstructAlter;
import com.okina.multiblock.construct.mode.AlterMode;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleCustomIcon extends ParticleBase {

	public float baseScale = 0.3f;

	public ParticleCustomIcon(World world, AlterMode alter, double x, double y, double z, float baseSize) {
		super(world, x, y, z);
		baseScale = baseSize;
		particleScale = baseSize;
		particleMaxAge = 300;
		this.setParticleIcon(ConstructAlter.ICON_EFFECT);
	}

	public ParticleCustomIcon(World world, Object[] objects) {
		this(world, (AlterMode) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Float) objects[4]);
	}

	//	@Override
	//	public void renderParticle(Tessellator tessellator, float partialTicks, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	//		float ageScaled = (particleAge + partialTicks) / particleMaxAge;
	//
	//		updateColor(ageScaled);
	//		updateTexture(ageScaled);
	//		updateScale(ageScaled);
	//
	//		//		float f6 = particleTextureIndexX / 16.0F;
	//		//		float f7 = f6 + textureSizeX / 16F;
	//		//		float f8 = particleTextureIndexY / 16.0F;
	//		//		float f9 = f8 + textureSizeY / 16F;
	//		float f6 = 0;
	//		float f7 = 1;
	//		float f8 = 0;
	//		float f9 = 1;
	//		float f10 = 0.1F * particleScale;
	//
	//		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
	//		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
	//		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
	//		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
	//		tessellator.addVertexWithUV(f11 - p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 - p_70539_5_ * f10 - p_70539_7_ * f10, f7, f9);
	//		tessellator.addVertexWithUV(f11 - p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 - p_70539_5_ * f10 + p_70539_7_ * f10, f7, f8);
	//		tessellator.addVertexWithUV(f11 + p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 + p_70539_5_ * f10 + p_70539_7_ * f10, f6, f8);
	//		tessellator.addVertexWithUV(f11 + p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 + p_70539_5_ * f10 - p_70539_7_ * f10, f6, f9);
	//	}

	@Override
	public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {

		float ageScaled = (particleAge + p_70539_2_) / particleMaxAge;
		updateColor(ageScaled);
		updateTexture(ageScaled);
		updateScale(ageScaled);

		float f6 = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
		float f7 = f6 + 0.015609375F;
		float f8 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
		float f9 = f8 + 0.015609375F;
		float f10 = 0.1F * this.particleScale;

		if(this.particleIcon != null){
			f6 = this.particleIcon.getInterpolatedU(0);
			f7 = this.particleIcon.getInterpolatedU(16);
			f8 = this.particleIcon.getInterpolatedV(0);
			f9 = this.particleIcon.getInterpolatedV(16);
			//			f6 = (this.particleTextureJitterX / 4.0F * 16.0F);
			//			f7 = ((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
			//			f8 = (this.particleTextureJitterY / 4.0F * 16.0F);
			//			f9 = ((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
		}

		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * p_70539_2_ - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * p_70539_2_ - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * p_70539_2_ - interpPosZ);
		p_70539_1_.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
		p_70539_1_.addVertexWithUV(f11 - p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 - p_70539_5_ * f10 - p_70539_7_ * f10, f6, f9);
		p_70539_1_.addVertexWithUV(f11 - p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 - p_70539_5_ * f10 + p_70539_7_ * f10, f6, f8);
		p_70539_1_.addVertexWithUV(f11 + p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 + p_70539_5_ * f10 + p_70539_7_ * f10, f7, f8);
		p_70539_1_.addVertexWithUV(f11 + p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 + p_70539_5_ * f10 - p_70539_7_ * f10, f7, f9);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	/**called on every partial tick*/
	@Override
	protected void updateColor(float ageScaled) {

	}

	/**called on every partial tick*/
	@Override
	protected void updateScale(float ageScaled) {
		//		particleScale = (float) Math.cos(ageScaled * Math.PI / 2d) * baseScale;
	}

}
