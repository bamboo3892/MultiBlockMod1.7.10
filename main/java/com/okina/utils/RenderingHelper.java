package com.okina.utils;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public final class RenderingHelper {

	public static void renderLaser(TileEntity host, double x, double y, double z, int length, ForgeDirection orientation, int red, int green, int blue, int alpha, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();

		double xStart = x;
		double yStart = y;
		double zStart = z;

		if(orientation == ForgeDirection.DOWN){
			yStart -= length;
		}
		if(orientation == ForgeDirection.NORTH){
			GL11.glRotatef(270, 1, 0, 0);
			yStart = -z - 1;
			zStart = y;
		}else if(orientation == ForgeDirection.SOUTH){
			GL11.glRotatef(90, 1, 0, 0);
			yStart = z;
			zStart = -y - 1;
		}else if(orientation == ForgeDirection.EAST){
			GL11.glRotatef(270, 0, 0, 1);
			yStart = x;
			xStart = -y - 1;
		}else if(orientation == ForgeDirection.WEST){
			GL11.glRotatef(90, 0, 0, 1);
			yStart = -x - 1;
			xStart = y;
		}
		yStart = yStart + 7.0 / 16.0D;

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		double height = length + 2.0 / 16.0D;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		tessellator.startDrawingQuads();
		if(alpha < 26) alpha = 26;
		tessellator.setColorRGBA(red, green, blue, alpha);
		double d18 = 7.0 / 16.0D;
		double d19 = 7.0 / 16.0D;
		double d20 = 9.0 / 16.0D;
		double d21 = 7.0 / 16.0D;
		double d22 = 7.0 / 16.0D;
		double d23 = 9.0 / 16.0D;
		double d24 = 9.0 / 16.0D;
		double d25 = 9.0 / 16.0D;

		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);
		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);

		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public static void renderCubeFrame(int x, int y, int z, Block block, double startX, double startY, double startZ, double sizeX, double sizeY, double sizeZ, double thickness, RenderBlocks renderer) {
		renderer.setRenderBounds(startX, startY, startZ, startX + thickness, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ, startX + sizeX, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY, startZ + sizeZ - thickness, startX + thickness, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ + sizeZ - thickness, startX + sizeX, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);

		renderer.setRenderBounds(startX + thickness, startY, startZ, startX + sizeX - thickness, startY + thickness, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY, startZ + thickness, startX + thickness, startY + thickness, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY, startZ + thickness, startX + sizeX, startY + thickness, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + thickness, startY, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + thickness, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);

		renderer.setRenderBounds(startX + thickness, startY + sizeY - thickness, startZ, startX + sizeX - thickness, startY + sizeY, startZ + thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX, startY + sizeY - thickness, startZ + thickness, startX + thickness, startY + sizeY, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + sizeX - thickness, startY + sizeY - thickness, startZ + thickness, startX + sizeX, startY + sizeY, startZ + sizeZ - thickness);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(startX + thickness, startY + sizeY - thickness, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + sizeY, startZ + sizeZ);
		renderer.renderStandardBlock(block, x, y, z);
	}

	public static void renderConnectedCubeFrame(boolean[] connection, int x, int y, int z, Block block, double thickness, RenderBlocks renderer) {
		if(connection == null || connection.length != 6) return;

		for (int j1 = 0; j1 < 2; j1++){
			for (int j2 = 0; j2 < 2; j2++){
				for (int j3 = 0; j3 < 2; j3++){
					double x1 = j1 == 0 ? 0 : 1 - thickness;
					double x2 = j1 == 0 ? thickness : 1;
					double y1 = j2 == 0 ? 0 : 1 - thickness;
					double y2 = j2 == 0 ? thickness : 1;
					double z1 = j3 == 0 ? 0 : 1 - thickness;
					double z2 = j3 == 0 ? thickness : 1;

					renderer.setRenderBounds(x1, y1, z1, x2, y2, z2);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
		}

		double[] c1 = new double[3];
		double[] c2 = new double[3];
		int[] i = new int[3];

		for (i[0] = -1; i[0] < 2; i[0]++){
			for (i[1] = -1; i[1] < 2; i[1]++){
				if(i[0] == 0 && i[1] == 0) continue;
				for (i[2] = -1; i[2] < 2; i[2]++){
					if(i[2] == 0 && (i[0] == 0 || i[1] == 0)) continue;
					for (int j = 0; j < 3; j++){
						if(i[j] == -1){
							c1[j] = 0;
							c2[j] = thickness;
						}else if(i[j] == 0){
							c1[j] = thickness;
							c2[j] = 1 - thickness;
						}else{
							c1[j] = 1 - thickness;
							c2[j] = 1;
						}
					}
					int side1 = -1;
					int side2 = -1;
					if(i[0] != 0){
						side1 = getDirection(i[0], 0, 0);
					}
					if(i[1] != 0){
						if(side1 == -1){
							side1 = getDirection(0, i[1], 0);
						}else{
							side2 = getDirection(0, i[1], 0);
						}
					}
					if(i[2] != 0){
						side2 = getDirection(0, 0, i[2]);
					}
					if(!(connection[side1] || connection[side2])){
						renderer.setRenderBounds(c1[0], c1[1], c1[2], c2[0], c2[1], c2[2]);
						renderer.renderStandardBlock(block, x, y, z);
					}
				}
			}
		}
	}

	private static int getDirection(int offsetX, int offsetY, int offsetZ) {
		for (int i = 0; i < 6; i++){
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if(dir.offsetX == offsetX && dir.offsetY == offsetY && dir.offsetZ == offsetZ){
				return dir.ordinal();
			}
		}
		return 6;
	}

	public static void renderInvCubeFrame(RenderBlocks renderer, Block block, float startX, float startY, float startZ, float sizeX, float sizeY, float sizeZ, float thickness) {
		renderInvCuboid(renderer, block, startX, startY, startZ, startX + thickness, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ, startX + sizeX, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY, startZ + sizeZ - thickness, startX + thickness, startY + sizeY, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ + sizeZ - thickness, startX + sizeX, startY + sizeY, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY, startZ, startX + sizeX - thickness, startY + thickness, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY, startZ + thickness, startX + thickness, startY + thickness, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY, startZ + thickness, startX + sizeX, startY + thickness, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + thickness, startZ + sizeZ, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY + sizeY - thickness, startZ, startX + sizeX - thickness, startY + sizeY, startZ + thickness, 0);
		renderInvCuboid(renderer, block, startX, startY + sizeY - thickness, startZ + thickness, startX + thickness, startY + sizeY, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + sizeX - thickness, startY + sizeY - thickness, startZ + thickness, startX + sizeX, startY + sizeY, startZ + sizeZ - thickness, 0);
		renderInvCuboid(renderer, block, startX + thickness, startY + sizeY - thickness, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + sizeY, startZ + sizeZ, 0);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		renderInvCuboid(renderer, block, minX, minY, minZ, maxX, maxY, maxZ, 0);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
		Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderInvCuboid(RenderBlocks renderer, Block block, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int meta) {
		Tessellator tessellator = Tessellator.instance;
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static void renderWorldTileCube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		for (int i = 0; i < 6; i++){
			renderWorldTileCube(minX, minY, minZ, maxX, maxY, maxZ, i);
		}
	}

	public static void renderWorldTileCube(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int side) {
		if(side >= 0 && side < 6){
			Tessellator tessellator = Tessellator.instance;
			if(side == 0){
				//y neg
				tessellator.addVertexWithUV(minX, minY, minZ, minX, minZ);
				tessellator.addVertexWithUV(maxX, minY, minZ, minX, maxZ);
				tessellator.addVertexWithUV(maxX, minY, maxZ, maxX, maxZ);
				tessellator.addVertexWithUV(minX, minY, maxZ, maxX, minZ);
			}else if(side == 1){
				//y pos
				tessellator.addVertexWithUV(minX, maxY, minZ, minX, minZ);
				tessellator.addVertexWithUV(minX, maxY, maxZ, minX, maxZ);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxX, maxZ);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxX, minZ);
			}else if(side == 4){
				//z neg
				tessellator.addVertexWithUV(minX, minY, minZ, minX, minX);
				tessellator.addVertexWithUV(minX, maxY, minZ, maxX, minX);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxX, maxY);
				tessellator.addVertexWithUV(maxX, minY, minZ, minX, maxY);
			}else if(side == 5){
				//z pos
				tessellator.addVertexWithUV(minX, minY, maxZ, minX, minY);
				tessellator.addVertexWithUV(maxX, minY, maxZ, maxX, minY);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxX, maxY);
				tessellator.addVertexWithUV(minX, maxY, maxZ, minX, maxY);
			}else if(side == 2){
				//x neg
				tessellator.addVertexWithUV(minX, minY, minZ, minY, minZ);
				tessellator.addVertexWithUV(minX, minY, maxZ, maxY, minZ);
				tessellator.addVertexWithUV(minX, maxY, maxZ, maxY, maxZ);
				tessellator.addVertexWithUV(minX, maxY, minZ, minY, maxZ);
			}else if(side == 3){
				///x pos
				tessellator.addVertexWithUV(maxX, minY, minZ, minY, minZ);
				tessellator.addVertexWithUV(maxX, maxY, minZ, maxY, minZ);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, maxY, maxZ);
				tessellator.addVertexWithUV(maxX, minY, maxZ, minY, maxZ);
			}
		}
	}

}




