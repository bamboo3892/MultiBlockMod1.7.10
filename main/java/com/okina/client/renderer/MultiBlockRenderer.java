package com.okina.client.renderer;

import com.okina.main.TestCore;
import com.okina.multiblock.MultiBlockCasing;
import com.okina.multiblock.MultiBlockCasingTileEntity;
import com.okina.multiblock.MultiBlockCore;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.block.BlockConstructBase;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class MultiBlockRenderer implements ISimpleBlockRenderingHandler {

	public MultiBlockRenderer() {}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(block instanceof MultiBlockCasing){
			RenderingHelper.renderInvCuboid(renderer, block, 1.0001F / 3F / 16F, 1.0001F / 3F / 16F, 1.0001F / 3F / 16F, (16F - 1.0001F / 3F) / 16F, (16F - 1.0001F / 3F) / 16F, (16F - 1.0001F / 3F) / 16F);
		}else if(block instanceof MultiBlockCore){
			renderer.setOverrideBlockTexture(Blocks.coal_block.getIcon(0, 0));
			RenderingHelper.renderInvCubeFrame(renderer, block, 0, 0, 0, 1, 1, 1, 1F / 16F);
			renderer.setOverrideBlockTexture(BlockConstructBase.iconPane);
			RenderingHelper.renderInvCuboid(renderer, block, 1F / 16F, 1F / 16F, 1F / 16F, 15F / 16F, 15F / 16F, 15F / 16F);
			renderer.clearOverrideBlockTexture();
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(block instanceof MultiBlockCasing){
			renderer.clearOverrideBlockTexture();
			renderer.setRenderBounds(1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			if(world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
				MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(x, y, z);
				if(tile.flagIO != null){
					int[] flagIO = tile.flagIO;
					if(flagIO[0] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[0] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[1] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[1] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[2] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[2] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[3] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[3] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[4] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[4] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[5] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[5] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
				}
			}
		}else if(world.getTileEntity(x, y, z) instanceof MultiBlockCoreTileEntity){
			renderer.setRenderBounds(1F / 16F, 1F / 16F, 1F / 16F, 15F / 16F, 15F / 16F, 15F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(x, y, z);
			if(tile.flagIO != null){
				int[] flagIO = tile.flagIO;
				if(flagIO[0] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[0] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(flagIO[1] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[1] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(flagIO[2] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[2] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(flagIO[3] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[3] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(flagIO[4] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[4] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(flagIO[5] == 0){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
					renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}else if(flagIO[5] == 1){
					renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
					renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
		}
		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.MULTIBLOCK_RENDER_ID;
	}

}
