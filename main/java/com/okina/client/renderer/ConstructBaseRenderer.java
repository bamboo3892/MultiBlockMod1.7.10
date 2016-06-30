package com.okina.client.renderer;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockBaseFrame;
import com.okina.multiblock.construct.ProcessorContainerTileEntity;
import com.okina.multiblock.construct.block.BlockConstructBase;
import com.okina.multiblock.construct.block.ConstructEnergyProvider;
import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.multiblock.construct.processor.EnergyProviderProcessor;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class ConstructBaseRenderer implements ISimpleBlockRenderingHandler {

	public ConstructBaseRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(block instanceof BlockConstructBase){
			RenderingHelper.renderInvCuboid(renderer, block, 2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F, 4);
			int grade = ((BlockConstructBase) block).grade;
			if(TestCore.effectProperties.renderPartsFancy){
				renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
				RenderingHelper.renderInvCubeFrame(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 1F / 16F);
			}else{
				renderer.setOverrideBlockTexture(BlockConstructBase.FRAME_ICON_FAST[grade]);
				RenderingHelper.renderInvCuboid(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F);
			}
			renderer.setOverrideBlockTexture(BlockConstructBase.iconPane);
			RenderingHelper.renderInvCuboid(renderer, block, 1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
		}else if(block instanceof BlockBaseFrame){
			RenderingHelper.renderInvCubeFrame(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 1F / 16F);
		}
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(block instanceof BlockConstructBase){
			if(world.getTileEntity(x, y, z) instanceof ProcessorContainerTileEntity){
				ProcessorContainerTileEntity tile = (ProcessorContainerTileEntity) world.getTileEntity(x, y, z);
				renderer.setRenderBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
				renderer.renderStandardBlock(block, x, y, z);
				int grade = ((BlockConstructBase) block).grade;
				boolean[] connection = tile.isNeighberBaseBlock;
				if(TestCore.effectProperties.renderPartsFancy){
					renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
					RenderingHelper.renderConnectedCubeFrame(connection, x, y, z, block, 1F / 16F, renderer);
				}else{
					renderer.setOverrideBlockTexture(BlockConstructBase.FRAME_ICON_FAST[grade]);
					renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
					renderer.renderStandardBlock(block, x, y, z);
				}
				renderer.setOverrideBlockTexture(BlockConstructBase.iconPane);
				renderer.setRenderBounds(1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
				renderer.renderStandardBlock(block, x, y, z);
				ProcessorBase processor = tile.getContainProcessor();
				if(processor != null){
					//					int[] flagIO = processor.flagIO;
					//					if(flagIO != null){
					//						if(flagIO[0] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[0] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//						if(flagIO[1] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[1] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//						if(flagIO[2] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[2] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//						if(flagIO[3] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[3] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//						if(flagIO[4] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[4] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//						if(flagIO[5] == 0){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					//							renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}else if(flagIO[5] == 1){
					//							renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					//							renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
					//							renderer.renderStandardBlock(block, x, y, z);
					//						}
					//					}
					if(processor instanceof EnergyProviderProcessor){
						renderEnergyProviderBlock(tile, (EnergyProviderProcessor) processor, x, y, z, block, renderer);
					}else if(processor instanceof ContainerProcessor){
						renderContainerBlock(tile, (ContainerProcessor) processor, x, y, z, block, renderer);
					}
				}
			}
		}else if(block instanceof BlockBaseFrame){
			RenderingHelper.renderCubeFrame(x, y, z, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 1F / 16F, renderer);
		}
		renderer.clearOverrideBlockTexture();
		return true;
	}

	private void renderEnergyProviderBlock(ProcessorContainerTileEntity tile, EnergyProviderProcessor processor, int x, int y, int z, Block block, RenderBlocks renderer) {
		int energy = processor.getEnergyLevel();
		renderer.setOverrideBlockTexture(ConstructEnergyProvider.energyIcon[energy]);
		renderer.setRenderBounds(0.999F / 16F, 0.999F / 16F, 0.999F / 16F, 15.001F / 16F, 15.001F / 16F, 15.001F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
	}

	private void renderContainerBlock(ProcessorContainerTileEntity tile, ContainerProcessor processor, int x, int y, int z, Block block, RenderBlocks renderer) {
		//connection box
		processor.mode2.renderConnectionBox(x, y, z, block, renderer);
		//		if(processor.mode == ContainerProcessor.CRUSHER_MODE){
		//			int dir = processor.connectDirection;
		//			if(dir != -1){
		//				renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
		//				if(dir == 0){
		//					RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, -15F / 16F, 1F / 16F, 14F / 16F, 46F / 16F, 14F / 16F, 1F / 16F, renderer);
		//				}else if(dir == 2){
		//					RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, 1F / 16F, -15F / 16F, 14F / 16F, 14F / 16F, 46F / 16F, 1F / 16F, renderer);
		//				}else if(dir == 4){
		//					RenderingHelper.renderCubeFrame(x, y, z, block, -15F / 16F, 1F / 16F, 1F / 16F, 46F / 16F, 14F / 16F, 14F / 16F, 1F / 16F, renderer);
		//				}
		//			}
		//		}else if(processor.mode == ContainerProcessor.GROWER_MODE){
		//			renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
		//			RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, -15F / 16F, 1F / 16F, 14F / 16F, 30F / 16F, 14F / 16F, 1F / 16F, renderer);
		//		}else if(processor.mode == ContainerProcessor.ENERGY_PROVIDER_MODE){
		//			ForgeDirection dir = ForgeDirection.getOrientation(processor.connectDirection);
		//			if(dir != ForgeDirection.UNKNOWN){
		//				renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
		//				float startX = dir.offsetX == -1 ? -15F / 16F : 1F / 16F;
		//				float startY = dir.offsetY == -1 ? -15F / 16F : 1F / 16F;
		//				float startZ = dir.offsetZ == -1 ? -15F / 16F : 1F / 16F;
		//				float sizeX = dir.offsetX != 0 ? 30F / 16F : 14F / 16F;
		//				float sizeY = dir.offsetY != 0 ? 30F / 16F : 14F / 16F;
		//				float sizeZ = dir.offsetZ != 0 ? 30F / 16F : 14F / 16F;
		//				RenderingHelper.renderCubeFrame(x, y, z, block, startX, startY, startZ, sizeX, sizeY, sizeZ, 1F / 16F, renderer);
		//			}
		//		}
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.CONSTRUCTBASE_RENDER_ID;
	}

}
