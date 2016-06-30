package com.okina.client.renderer;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.okina.inventory.IFilterUser;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.utils.RenderingHelper;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileConstructBaseRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation WOOL_BLUE = new ResourceLocation("textures/blocks/wool_colored_light_blue.png");
	private static final ResourceLocation WOOL_ORANGE = new ResourceLocation("textures/blocks/wool_colored_orange.png");
	private FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);

	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {

		if(tile instanceof ProcessorContainerTileEntity){
			ProcessorContainerTileEntity baseTile = (ProcessorContainerTileEntity) tile;
			float ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
			Tessellator tessellator = Tessellator.instance;

			//io box
			ProcessorBase processor = baseTile.getContainProcessor();
			if(processor != null){
				int[] flagIO = processor.flagIO;
				if(flagIO != null){
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glTranslatef((float) tileX, (float) tileY, (float) tileZ);
					tessellator.setColorRGBA_F(1f, 1f, 1f, 1f);
					if(flagIO[0] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						tessellator.draw();
					}else if(flagIO[0] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						tessellator.draw();
					}
					if(flagIO[1] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						tessellator.draw();
					}else if(flagIO[1] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						tessellator.draw();
					}
					if(flagIO[2] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						tessellator.draw();
					}else if(flagIO[2] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						tessellator.draw();
					}
					if(flagIO[3] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						tessellator.draw();
					}else if(flagIO[3] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						tessellator.draw();
					}
					if(flagIO[4] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						tessellator.draw();
					}else if(flagIO[4] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						tessellator.draw();
					}
					if(flagIO[5] == 0){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_BLUE);
						RenderingHelper.renderWorldTileCube(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						tessellator.draw();
					}else if(flagIO[5] == 1){
						tessellator.startDrawingQuads();
						bindTexture(WOOL_ORANGE);
						RenderingHelper.renderWorldTileCube(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						tessellator.draw();
					}
					GL11.glTranslatef((float) -tileX, (float) -tileY, (float) -tileZ);
					GL11.glPopMatrix();
				}
			}

			{
				if(baseTile.restRenderTicks != 0){
					//connection check box
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glDepthMask(false);
					GL11.glDisable(GL11.GL_CULL_FACE);
					float pastWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
					GL11.glLineWidth(3.0f);

					double d;
					double d2;
					d = 1.0D + 0.2D / 3.0D;
					d2 = 1.0D + 0.2D / 3.0D;
					tileX -= 0.1D / 3.0D;
					tileY -= 0.1D / 3.0D;
					tileZ -= 0.1D / 3.0D;
					float hue = (float) ((ticks % 100D) / 100D);
					Color color = Color.getHSBColor(hue, 1f, 1f);
					float[] rgb = null;
					rgb = color.getRGBColorComponents(rgb);
					float alpha = baseTile.restRenderTicks < 20 ? baseTile.restRenderTicks / 20f : 1f;

					tessellator.startDrawing(GL11.GL_LINES);
					tessellator.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
					//y neg
					tessellator.addVertex(tileX, tileY, tileZ);
					tessellator.addVertex(tileX, tileY, tileZ + d);
					tessellator.addVertex(tileX, tileY, tileZ + d);
					tessellator.addVertex(tileX + d, tileY, tileZ + d);
					tessellator.addVertex(tileX + d, tileY, tileZ + d);
					tessellator.addVertex(tileX + d, tileY, tileZ);
					tessellator.addVertex(tileX + d, tileY, tileZ);
					tessellator.addVertex(tileX, tileY, tileZ);
					//y pos
					tessellator.addVertex(tileX, tileY + d, tileZ);
					tessellator.addVertex(tileX, tileY + d, tileZ + d);
					tessellator.addVertex(tileX, tileY + d, tileZ + d);
					tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
					tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
					tessellator.addVertex(tileX + d, tileY + d, tileZ);
					tessellator.addVertex(tileX + d, tileY + d, tileZ);
					tessellator.addVertex(tileX, tileY + d, tileZ);
					//virtical
					tessellator.addVertex(tileX, tileY, tileZ);
					tessellator.addVertex(tileX, tileY + d, tileZ);
					tessellator.addVertex(tileX + d, tileY, tileZ);
					tessellator.addVertex(tileX + d, tileY + d, tileZ);
					tessellator.addVertex(tileX + d, tileY, tileZ + d);
					tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
					tessellator.addVertex(tileX, tileY, tileZ + d);
					tessellator.addVertex(tileX, tileY + d, tileZ + d);
					tessellator.draw();

					tileX += 0.1D / 3.0D;
					tileY += 0.1D / 3.0D;
					tileZ += 0.1D / 3.0D;

					if(baseTile.renderSide != -1){
						alpha = baseTile.restRenderTicks < 20 ? baseTile.restRenderTicks / 40f : 0.5f;
						d = 1.0D;
						d2 = 1.0D;
						tessellator.startDrawing(7);
						tessellator.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
						if(baseTile.renderSide == 0){
							//y neg
							tessellator.addVertex(tileX, tileY - 0.1D / 3.0D, tileZ);
							tessellator.addVertex(tileX, tileY - 0.1D / 3.0D, tileZ + d);
							tessellator.addVertex(tileX + d, tileY - 0.1D / 3.0D, tileZ + d);
							tessellator.addVertex(tileX + d, tileY - 0.1D / 3.0D, tileZ);
						}else if(baseTile.renderSide == 1){
							//y pos
							tessellator.addVertex(tileX, tileY + d + 0.1D / 3.0D, tileZ);
							tessellator.addVertex(tileX + d, tileY + d + 0.1D / 3.0D, tileZ);
							tessellator.addVertex(tileX + d, tileY + d + 0.1D / 3.0D, tileZ + d);
							tessellator.addVertex(tileX, tileY + d + 0.1D / 3.0D, tileZ + d);
						}else if(baseTile.renderSide == 4){
							//z neg
							tessellator.addVertex(tileX - 0.1D / 3.0D, tileY, tileZ);
							tessellator.addVertex(tileX - 0.1D / 3.0D, tileY + d, tileZ);
							tessellator.addVertex(tileX - 0.1D / 3.0D, tileY + d, tileZ + d);
							tessellator.addVertex(tileX - 0.1D / 3.0D, tileY, tileZ + d);
						}else if(baseTile.renderSide == 5){
							//z pos
							tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY, tileZ);
							tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY, tileZ + d);
							tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY + d, tileZ + d);
							tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY + d, tileZ);
						}else if(baseTile.renderSide == 2){
							//x neg
							tessellator.addVertex(tileX, tileY, tileZ - 0.1D / 3.0D);
							tessellator.addVertex(tileX + d, tileY, tileZ - 0.1D / 3.0D);
							tessellator.addVertex(tileX + d, tileY + d, tileZ - 0.1D / 3.0D);
							tessellator.addVertex(tileX, tileY + d, tileZ - 0.1D / 3.0D);
						}else if(baseTile.renderSide == 3){
							///x pos
							tessellator.addVertex(tileX, tileY, tileZ + d + 0.1D / 3.0D);
							tessellator.addVertex(tileX, tileY + d, tileZ + d + 0.1D / 3.0D);
							tessellator.addVertex(tileX + d, tileY + d, tileZ + d + 0.1D / 3.0D);
							tessellator.addVertex(tileX + d, tileY, tileZ + d + 0.1D / 3.0D);
						}
						tessellator.draw();
					}
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glLineWidth(pastWidth);
					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}

			if(processor != null){
				GL11.glPushMatrix();
				GL11.glTranslatef((float) tileX, (float) tileY, (float) tileZ);
				processor.customRenderTile(partialTicks);
				if(processor instanceof IFilterUser){
					renderFilterUser(baseTile, (IFilterUser) processor, partialTicks);
				}
				GL11.glTranslatef((float) -tileX, (float) -tileY, (float) -tileZ);
				GL11.glPopMatrix();
			}
			//			if(processor instanceof ContainerProcessor){
			//				renderContainer(baseTile, (ContainerProcessor) processor, partialTicks);
			//			}else if(processor instanceof CrusherProcessor){
			//				renderCrusher(baseTile, (CrusherProcessor) processor, partialTicks);
			//			}
		}
	}

	private void renderFilterUser(ProcessorContainerTileEntity tile, IFilterUser filterUser, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		for (int side = 0; side < 6; side++){
			if(filterUser.getFilter(side) != null){
				bindTexture(filterUser.getFilter(side).getFilterIcon());
				double d = 1.0D;
				double d2 = 1.99d / 16d;
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_F(1f, 1f, 1f, 1f);
				if(side == 0){
					//y neg
					tessellator.addVertexWithUV(0, d2, 0, 0, 0);
					tessellator.addVertexWithUV(d, d2, 0, 0, 1);
					tessellator.addVertexWithUV(d, d2, d, 1, 1);
					tessellator.addVertexWithUV(0, d2, d, 1, 0);
				}else if(side == 1){
					//y pos
					tessellator.addVertexWithUV(0, d - d2, 0, 0, 0);
					tessellator.addVertexWithUV(0, d - d2, d, 0, 1);
					tessellator.addVertexWithUV(d, d - d2, d, 1, 1);
					tessellator.addVertexWithUV(d, d - d2, 0, 1, 0);
				}else if(side == 4){
					//z neg
					tessellator.addVertexWithUV(d2, 0, 0, 0, 0);
					tessellator.addVertexWithUV(d2, 0, d, 0, 1);
					tessellator.addVertexWithUV(d2, d, d, 1, 1);
					tessellator.addVertexWithUV(d2, d, 0, 1, 0);
				}else if(side == 5){
					//z pos
					tessellator.addVertexWithUV(d - d2, 0, 0, 0, 0);
					tessellator.addVertexWithUV(d - d2, d, 0, 0, 1);
					tessellator.addVertexWithUV(d - d2, d, d, 1, 1);
					tessellator.addVertexWithUV(d - d2, 0, d, 1, 0);
				}else if(side == 2){
					//x neg
					tessellator.addVertexWithUV(0, 0, d2, 0, 0);
					tessellator.addVertexWithUV(0, d, d2, 0, 1);
					tessellator.addVertexWithUV(d, d, d2, 1, 1);
					tessellator.addVertexWithUV(d, 0, d2, 1, 0);
				}else if(side == 3){
					///x pos
					tessellator.addVertexWithUV(0, 0, d - d2, 0, 0);
					tessellator.addVertexWithUV(d, 0, d - d2, 0, 1);
					tessellator.addVertexWithUV(d, d, d - d2, 1, 1);
					tessellator.addVertexWithUV(0, d, d - d2, 1, 0);
				}
				tessellator.draw();
			}
		}
		GL11.glPopMatrix();
	}

	//	private void renderContainer(ProcessorContainerTileEntity tile, ContainerProcessor container, float partialTicks) {
	//		if(container.getStackInSlot(0) != null){
	//			float ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
	//
	//			GL11.glPushMatrix();
	//			GL11.glDisable(GL11.GL_CULL_FACE);
	//			GL11.glTranslatef(0.5F, 0.3F, 0.5F);
	//			GL11.glScalef(2F, 2F, 2F);
	//			GL11.glRotatef(0.1F * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
	//
	//			EntityItem entityitem = null;
	//			ItemStack is = container.getStackInSlot(0).copy();
	//			is.stackSize = 1;
	//			entityitem = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
	//			entityitem.hoverStart = 0.0F;
	//
	//			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
	//
	//			if(container.getStackInSlot(0).stackSize >= 2){
	//				if(Block.getBlockFromItem(container.getStackInSlot(0).getItem()) != Blocks.air){
	//					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
	//				}else{
	//					GL11.glTranslatef(0.05F, 0.05F, 0.02F);
	//					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
	//				}
	//			}
	//			GL11.glEnable(GL11.GL_CULL_FACE);
	//			GL11.glPopMatrix();
	//		}
	//	}
	//
	//	private void renderCrusher(ProcessorContainerTileEntity tile, CrusherProcessor crusher, float partialTicks) {
	//		if(crusher.getStackInSlot(0) != null){
	//			float ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
	//			float rotateSpeed = 3;
	//			float progress = 0;
	//			ForgeDirection dir = ForgeDirection.getOrientation(crusher.getDirection());
	//			if(crusher.pc.getProcessor(crusher.xCoord + dir.offsetX, crusher.yCoord + dir.offsetY, crusher.zCoord + dir.offsetZ) instanceof ContainerProcessor){
	//				ContainerProcessor container = (ContainerProcessor) crusher.pc.getProcessor(crusher.xCoord + dir.offsetX, crusher.yCoord + dir.offsetY, crusher.zCoord + dir.offsetZ);
	//				if(container.mode2 instanceof CrusherMode){
	//					CrusherMode crusherMode = (CrusherMode) container.mode2;
	//					if(crusherMode.processingTicks != -1 && (crusherMode.connectDirection == dir.ordinal() || crusherMode.connectDirection == dir.getOpposite().ordinal())){
	//						rotateSpeed = 12;
	//						progress = crusherMode.processingTicks + partialTicks;
	//					}
	//				}
	//			}
	//			GL11.glPushMatrix();
	//			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	//			int meta = tile.getBlockMetadata();
	//			if(meta == 0){
	//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
	//				GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}else if(meta == 1){
	//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}else if(meta == 2){
	//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
	//				GL11.glRotatef(270F, 1.0F, 0.0F, 0.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}else if(meta == 3){
	//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
	//				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}else if(meta == 4){
	//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
	//				GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}else if(meta == 5){
	//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
	//				GL11.glRotatef(270F, 0.0F, 0.0F, 1.0F);
	//				GL11.glScalef(2F, 3F, 2F);
	//			}
	//			GL11.glTranslatef(0, (float) (0.078 * (1 - Math.cos(progress / 10 * Math.PI * 2))) - 0.055F, 0);
	//			EntityItem entity = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
	//			entity.hoverStart = 0.0F;
	//			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
	//			GL11.glPopMatrix();
	//		}
	//	}

	private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
		this.field_147528_b.clear();
		this.field_147528_b.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		this.field_147528_b.flip();
		return this.field_147528_b;
	}

}
