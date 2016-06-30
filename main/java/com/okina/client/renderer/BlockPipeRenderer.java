package com.okina.client.renderer;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class BlockPipeRenderer implements ISimpleBlockRenderingHandler {

	public BlockPipeRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		GL11.glScalef(2F, 2F, 2F);
		renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
		RenderingHelper.renderInvCubeFrame(renderer, block, 5F / 16F, 5F / 16F, 5F / 16F, 6F / 16F, 6F / 16F, 6F / 16F, 1F / 16F);
		renderer.setOverrideBlockTexture(TestCore.pipe.getIcon(0, metadata));
		RenderingHelper.renderInvCuboid(renderer, block, 6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F);
		renderer.clearOverrideBlockTexture();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		if(TestCore.effectProperties.renderPartsFancy){
			renderer.setRenderBounds(6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			if(meta == 0){
				renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
			}else if(meta == 1){
				renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
			}else if(meta == 2){
				renderer.setOverrideBlockTexture(Blocks.diamond_block.getBlockTextureFromSide(0));
			}else if(meta == 3){
				renderer.setOverrideBlockTexture(Blocks.emerald_block.getBlockTextureFromSide(0));
			}
			RenderingHelper.renderCubeFrame(x, y, z, block, 5F / 16F, 5F / 16F, 5F / 16F, 6F / 16F, 6F / 16F, 6F / 16F, 1F / 16F, renderer);
		}else{
			int meta = world.getBlockMetadata(x, y, z);
			if(meta == 0){
				renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
			}else if(meta == 1){
				renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
			}else if(meta == 2){
				renderer.setOverrideBlockTexture(Blocks.diamond_block.getBlockTextureFromSide(0));
			}else if(meta == 3){
				renderer.setOverrideBlockTexture(Blocks.emerald_block.getBlockTextureFromSide(0));
			}
			renderer.setRenderBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}
		renderer.clearOverrideBlockTexture();

		//		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return false;
		//		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		//		if(tile.connection == null) return true;
		//
		//		renderer.setOverrideBlockTexture(Blocks.coal_block.getBlockTextureFromSide(0));
		//		if(tile.connection[0]){
		//			renderer.setRenderBounds(6F / 16F, 0F / 16F, 6F / 16F, 10F / 16F, 6F / 16F, 10F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		if(tile.connection[1]){
		//			renderer.setRenderBounds(6F / 16F, 10F / 16F, 6F / 16F, 10F / 16F, 16F / 16F, 10F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		if(tile.connection[2]){
		//			renderer.setRenderBounds(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 6F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		if(tile.connection[3]){
		//			renderer.setRenderBounds(6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F, 16F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		if(tile.connection[4]){
		//			renderer.setRenderBounds(0F / 16F, 6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		if(tile.connection[5]){
		//			renderer.setRenderBounds(10F / 16F, 6F / 16F, 6F / 16F, 16F / 16F, 10F / 16F, 10F / 16F);
		//			renderer.renderStandardBlock(block, x, y, z);
		//		}
		//		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.BLOCKPIPE_RENDER_ID;
	}

}
