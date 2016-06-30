package com.okina.client.particle;

import com.okina.utils.Bezier;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ParticleBezierDot extends ParticleBase {

	private Bezier bezier;

	public ParticleBezierDot(World world, double startX, double startY, double startZ, double endX, double endY, double endZ, int startSide, int endSide, int color) {
		super(world, startX, startY, startZ, color);
		ForgeDirection startDir = ForgeDirection.getOrientation(startSide);
		ForgeDirection endDir = ForgeDirection.getOrientation(endSide);
		bezier = new Bezier(startX, startY, startZ, endX, endY, endZ, startDir.offsetX * 2, startDir.offsetY * 2, startDir.offsetZ * 2, -endDir.offsetX * 2, -endDir.offsetY * 2, -endDir.offsetZ * 2);
		particleScale = 0.3f;
		particleTextureIndexX = 4;
		particleTextureIndexY = 2;
		textureSizeX = 4;
		textureSizeY = 4;
		particleMaxAge = (int) (bezier.getLength() * 100);
	}

	public ParticleBezierDot(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (Integer) objects[6], (Integer) objects[7], (Integer) objects[8]);
	}

	@Override
	protected void updatePosition(float ageScaled) {
		double[] pos = bezier.getPosition(ageScaled);
		posX = pos[0];
		posY = pos[1];
		posZ = pos[2];
	}

}
