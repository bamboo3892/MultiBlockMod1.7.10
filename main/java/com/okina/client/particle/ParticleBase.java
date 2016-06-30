package com.okina.client.particle;

import java.awt.Color;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleBase extends EntityFX {

	protected int textureSizeX = 1;
	protected int textureSizeY = 1;
	protected Color color;

	protected ParticleBase(World world, double x, double y, double z) {
		this(world, x, y, z, 0xFFFFFF);
	}

	protected ParticleBase(World world, double x, double y, double z, int color) {
		super(world, x, y, z);
		this.color = new Color(color);
		particleRed = this.color.getRed() / 256f;
		particleGreen = this.color.getGreen() / 256f;
		particleBlue = this.color.getBlue() / 256f;
		particleMaxAge = 20;
		noClip = true;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
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

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if(particleAge++ >= particleMaxAge){
			setDead();
		}
		updatePosition(((float) particleAge) / (float) particleMaxAge);
	}

	/**called on every partial tick*/
	protected void updateColor(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateTexture(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateScale(float ageScaled) {

	}

	/**called on every tick*/
	protected void updatePosition(float ageScaled) {

	}

	/**return color multiply?*/
	@Override
	public int getBrightnessForRender(float partialTick) {
		int i = super.getBrightnessForRender(partialTick);
		float ageScaled = (float) particleAge / (float) particleMaxAge;
		ageScaled *= ageScaled;
		ageScaled *= ageScaled;
		int j = i & 255;
		int alpha = i >> 16 & 255;
		alpha += (int) (ageScaled * 15.0F * 16.0F);

		if(alpha > 240){
			alpha = 240;
		}

		return j | alpha << 16;
	}

	/**
	 * Gets how bright this entity is.
	 */
	@Override
	public float getBrightness(float partialTicks) {
		float brightness = super.getBrightness(partialTicks);
		float ageScaled = (float) particleAge / (float) particleMaxAge;
		ageScaled = ageScaled * ageScaled * ageScaled * ageScaled;
		return brightness * (1.0F - ageScaled) + ageScaled;
	}

}
