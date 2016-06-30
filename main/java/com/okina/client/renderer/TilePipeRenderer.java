package com.okina.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.utils.RenderingHelper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TilePipeRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation COAL = new ResourceLocation("textures/blocks/coal_block.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
		if(tile instanceof BlockPipeTileEntity){
			BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
			Tessellator tessellator = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glTranslatef((float) tileX, (float) tileY, (float) tileZ);
			bindTexture(COAL);
			if(pipe.connection[0]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(6F / 16F, 0F / 16F, 6F / 16F, 10F / 16F, 6F / 16F, 10F / 16F);
				tessellator.draw();
			}
			if(pipe.connection[1]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(6F / 16F, 10F / 16F, 6F / 16F, 10F / 16F, 16F / 16F, 10F / 16F);
				tessellator.draw();
			}
			if(pipe.connection[2]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 6F / 16F);
				tessellator.draw();
			}
			if(pipe.connection[3]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F, 16F / 16F);
				tessellator.draw();
			}
			if(pipe.connection[4]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(0F / 16F, 6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F);
				tessellator.draw();
			}
			if(pipe.connection[5]){
				tessellator.startDrawingQuads();
				RenderingHelper.renderWorldTileCube(10F / 16F, 6F / 16F, 6F / 16F, 16F / 16F, 10F / 16F, 10F / 16F);
				tessellator.draw();
			}
			GL11.glTranslatef((float) -tileX, (float) -tileY, (float) -tileZ);
			GL11.glPopMatrix();
		}
	}

}
