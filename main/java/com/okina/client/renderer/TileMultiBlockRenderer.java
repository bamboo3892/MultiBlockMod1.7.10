package com.okina.client.renderer;

import org.lwjgl.opengl.GL11;

import com.okina.client.model.MultiBlockModel;
import com.okina.main.TestCore;
import com.okina.multiblock.MultiBlockCoreTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileMultiBlockRenderer extends TileEntitySpecialRenderer {

	public static MultiBlockModel model1 = new MultiBlockModel();
	public static MultiBlockModel model2 = new MultiBlockModel();
	public static ResourceLocation textures = new ResourceLocation(TestCore.MODID + ":textures/blocks/multi_block.png");
	public static ResourceLocation background = new ResourceLocation(TestCore.MODID + ":textures/blocks/multi_block_background.png");

	/**coordinates is calculated from view point*/
	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
		if(tile instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity core = (MultiBlockCoreTileEntity) tile;
			Tessellator tessellator = Tessellator.instance;
			GL11.glPushMatrix();

			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			//			RenderHelper.disableStandardItemLighting();
			//			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(false);
			//			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			bindTexture(background);

			double d;
			double d2;
			tessellator.startDrawing(7);
			tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.5F);
			if(core.connected){
				d = 2.8D;
				d2 = 2.8D;
				tileX -= 0.9D;
				tileY -= 0.9D;
				tileZ -= 0.9D;
			}else{
				d = 1.0D - 0.2D / 3.0D;
				d2 = 1.0D - 0.2D / 3.0D;
				tileX += 0.1D / 3.0D;
				tileY += 0.1D / 3.0D;
				tileZ += 0.1D / 3.0D;
			}
			//y neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, d2, 0);
			//y pos
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, d2, 0);
			//z neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, d2, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, d2);
			//z pos
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, 0);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, 0, d2);
			//x neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, d2, 0);
			///x pos
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, 0);
			tessellator.draw();

			if(core.connected){
				tileX += 0.9D;
				tileY += 0.9D;
				tileZ += 0.9D;
			}else{
				tileX -= 0.1D / 3.0D;
				tileY -= 0.1D / 3.0D;
				tileZ -= 0.1D / 3.0D;
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopAttrib();
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			int maxLength = Math.max(core.xSize, Math.max(core.ySize, core.zSize));
			double ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
			GL11.glTranslatef((float) tileX + 0.5f, (float) tileY + 0.5f, (float) tileZ + 0.5f);
			Minecraft.getMinecraft().renderEngine.bindTexture(textures);
			if(core.connected){
				model1.render(null, 0, 0, 0, 0, 0, 3f / 16f);
				GL11.glScalef(2f / maxLength, 2f / maxLength, 2f / maxLength);
			}else{
				model2.render(null, 0, 0, 0, 0, 0, 1f / 16f);
				GL11.glScalef(0.5f / maxLength, 0.5f / maxLength, 0.5f / maxLength);
			}
			if(core.renderDetail){
				GL11.glRotatef(tile.getBlockMetadata() * -90, 0, 1, 0);
				GL11.glTranslatef(-(float) core.xSize / 2f + 0.5f, -(float) core.ySize / 2f + 0.5f, -(float) core.zSize / 2f + 0.5f);
				for (int i = 0; i < core.xSize; i++){
					for (int j = 0; j < core.ySize; j++){
						for (int k = 0; k < core.zSize; k++){
							if(core.getPart(i, j, k, true) != null){
								GL11.glPushMatrix();
								GL11.glTranslatef(i, j, k);
								core.getPart(i, j, k, true).renderPart(ticks);
								GL11.glTranslatef(-i, -j, -k);
								GL11.glPopMatrix();
							}
						}
					}
				}
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

}
