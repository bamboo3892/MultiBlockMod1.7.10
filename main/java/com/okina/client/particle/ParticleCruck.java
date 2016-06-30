package com.okina.client.particle;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author ???
 *	cruck icon
 */
public class ParticleCruck extends ParticleBase {

	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;
	private IIcon icon;

	public ParticleCruck(World world, double x, double y, double z, double vecX, double vecY, double vecZ, IIcon icon) {
		super(world, x, y, z);
		Random rand = world.rand;
		startX = x + rand.nextDouble() * 0.2;
		startY = y + rand.nextDouble() * 0.2;
		startZ = z + rand.nextDouble() * 0.2;
		endX = vecX;
		endY = vecY;
		endZ = vecZ;
		setParticleIcon(icon);
		particleRed = particleGreen = particleBlue = 0.6F;
		particleScale /= 4.0F;
		particleMaxAge = 8;
		noClip = true;
	}

	public ParticleCruck(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (IIcon) objects[6]);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(Tessellator tesselator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float f6 = (particleTextureIndexX + particleTextureJitterX / 4.0F) / 16.0F;
		float f7 = f6 + 0.015609375F;
		float f8 = (particleTextureIndexY + particleTextureJitterY / 4.0F) / 16.0F;
		float f9 = f8 + 0.015609375F;
		float f10 = 0.1F * particleScale;

		if(particleIcon != null){
			f6 = particleIcon.getInterpolatedU(particleTextureJitterX / 4.0F * 16.0F);
			f7 = particleIcon.getInterpolatedU((particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
			f8 = particleIcon.getInterpolatedV(particleTextureJitterY / 4.0F * 16.0F);
			f9 = particleIcon.getInterpolatedV((particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
		}

		float f11 = (float) (prevPosX + (posX - prevPosX) * p_70539_2_ - interpPosX);
		float f12 = (float) (prevPosY + (posY - prevPosY) * p_70539_2_ - interpPosY);
		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * p_70539_2_ - interpPosZ);
		tesselator.setColorOpaque_F(particleRed, particleGreen, particleBlue);
		tesselator.addVertexWithUV(f11 - p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 - p_70539_5_ * f10 - p_70539_7_ * f10, f6, f9);
		tesselator.addVertexWithUV(f11 - p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 - p_70539_5_ * f10 + p_70539_7_ * f10, f6, f8);
		tesselator.addVertexWithUV(f11 + p_70539_3_ * f10 + p_70539_6_ * f10, f12 + p_70539_4_ * f10, f13 + p_70539_5_ * f10 + p_70539_7_ * f10, f7, f8);
		tesselator.addVertexWithUV(f11 + p_70539_3_ * f10 - p_70539_6_ * f10, f12 - p_70539_4_ * f10, f13 + p_70539_5_ * f10 - p_70539_7_ * f10, f7, f9);
	}

	@Override
	protected void updatePosition(float ageScaled) {
		posX = (endX - startX) * ageScaled + startX;
		posY = (endY - startY) * ageScaled + startY;
		posZ = (endZ - startZ) * ageScaled + startZ;
	}
}
