package com.okina.client.renderer;

import org.lwjgl.opengl.GL11;

import com.okina.multiblock.MultiBlockCasingTileEntity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileCasingRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
		if(tile instanceof MultiBlockCasingTileEntity){
			MultiBlockCasingTileEntity casing = (MultiBlockCasingTileEntity) tile;
			Tessellator tessellator = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			for (int side = 0; side < 6; side++){
				if(casing.getFilter(side) != null){
					this.bindTexture(casing.getFilter(side).getFilterIcon());
					double d = 1.0D;
					double d2 = 1.99d / 16d;
					tessellator.startDrawingQuads();
					tessellator.setColorRGBA_F(1f, 1f, 1f, 1f);
					if(side == 0){
						//y neg
						tessellator.addVertexWithUV(tileX, tileY + d2, tileZ, 0, 0);
						tessellator.addVertexWithUV(tileX + d, tileY + d2, tileZ, 0, 1);
						tessellator.addVertexWithUV(tileX + d, tileY + d2, tileZ + d, 1, 1);
						tessellator.addVertexWithUV(tileX, tileY + d2, tileZ + d, 1, 0);
					}else if(side == 1){
						//y pos
						tessellator.addVertexWithUV(tileX, tileY + d - d2, tileZ, 0, 0);
						tessellator.addVertexWithUV(tileX, tileY + d - d2, tileZ + d, 0, 1);
						tessellator.addVertexWithUV(tileX + d, tileY + d - d2, tileZ + d, 1, 1);
						tessellator.addVertexWithUV(tileX + d, tileY + d - d2, tileZ, 1, 0);
					}else if(side == 4){
						//z neg
						tessellator.addVertexWithUV(tileX + d2, tileY, tileZ, 0, 0);
						tessellator.addVertexWithUV(tileX + d2, tileY, tileZ + d, 0, 1);
						tessellator.addVertexWithUV(tileX + d2, tileY + d, tileZ + d, 1, 1);
						tessellator.addVertexWithUV(tileX + d2, tileY + d, tileZ, 1, 0);
					}else if(side == 5){
						//z pos
						tessellator.addVertexWithUV(tileX + d - d2, tileY, tileZ, 0, 0);
						tessellator.addVertexWithUV(tileX + d - d2, tileY + d, tileZ, 0, 1);
						tessellator.addVertexWithUV(tileX + d - d2, tileY + d, tileZ + d, 1, 1);
						tessellator.addVertexWithUV(tileX + d - d2, tileY, tileZ + d, 1, 0);
					}else if(side == 2){
						//x neg
						tessellator.addVertexWithUV(tileX, tileY, tileZ + d2, 0, 0);
						tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d2, 0, 1);
						tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d2, 1, 1);
						tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d2, 1, 0);
					}else if(side == 3){
						///x pos
						tessellator.addVertexWithUV(tileX, tileY, tileZ + d - d2, 0, 0);
						tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d - d2, 0, 1);
						tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d - d2, 1, 1);
						tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d - d2, 1, 0);
					}
					tessellator.draw();
				}
			}
			GL11.glPopMatrix();
		}

	}

}
