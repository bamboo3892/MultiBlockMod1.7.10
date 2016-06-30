package com.okina.client.renderer;

import com.okina.main.TestCore;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class BlockFrameLineRenderer implements ISimpleBlockRenderingHandler {

	public BlockFrameLineRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(Blocks.coal_block.getBlockTextureFromSide(0));
		RenderingHelper.renderInvCubeFrame(renderer, block, 0, 0, 0, 1, 1, 1, 3F / 16F);
		renderer.setOverrideBlockTexture(Blocks.redstone_block.getBlockTextureFromSide(0));
		RenderingHelper.renderInvCuboid(renderer, block, 2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		renderer.setOverrideBlockTexture(Blocks.coal_block.getBlockTextureFromSide(0));
		if(meta == 0){
			RenderingHelper.renderCubeFrame(x, y, z, block, 0, 0, 0, 1, 1, 1, 3F / 16F, renderer);
			renderer.setOverrideBlockTexture(Blocks.redstone_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}else if(meta == 1){
			renderer.setRenderBounds(0F / 16F, -0.01F / 16F, 0F / 16F, 3F / 16F, 16.01F / 16F, 3F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(13F / 16F, -0.01F / 16F, 0F / 16F, 16F / 16F, 16.01F / 16F, 3F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0F / 16F, -0.01F / 16F, 13F / 16F, 3F / 16F, 16.01F / 16F, 16F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(13F / 16F, -0.01F / 16F, 13F / 16F, 16F / 16F, 16.01F / 16F, 16F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(Blocks.redstone_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(2F / 16F, 0F / 16F, 2F / 16F, 14F / 16F, 16F / 16F, 14F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}else if(meta == 2){
			renderer.setRenderBounds(0F / 16F, 0F / 16F, -0.01F / 16F, 3F / 16F, 3F / 16F, 16.01F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(13F / 16F, 0F / 16F, -0.01F / 16F, 16F / 16F, 3F / 16F, 16.01F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0F / 16F, 13F / 16F, -0.01F / 16F, 3F / 16F, 16F / 16F, 16.01F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(13F / 16F, 13F / 16F, -0.01F / 16F, 16F / 16F, 16F / 16F, 16.01F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(Blocks.redstone_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(2F / 16F, 2F / 16F, 0F / 16F, 14F / 16F, 14F / 16F, 16F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}else if(meta == 3){
			renderer.setRenderBounds(-0.01F / 16F, 0F / 16F, 0F / 16F, 16.01F / 16F, 3F / 16F, 3F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(-0.01F / 16F, 0F / 16F, 13F / 16F, 16.01F / 16F, 3F / 16F, 16F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(-0.01F / 16F, 13F / 16F, 0F / 16F, 16.01F / 16F, 16F / 16F, 3F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(-0.01F / 16F, 13F / 16F, 13F / 16F, 16.01F / 16F, 16F / 16F, 16F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(Blocks.redstone_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(0F / 16F, 2F / 16F, 2F / 16F, 16F / 16F, 14F / 16F, 14F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}
		renderer.clearOverrideBlockTexture();
		return true;
	}

	private void renderBasePiller(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer) {
		renderer.setRenderBounds(0F / 16F, 0F / 16F, 0F / 16F, 3F / 16F, 16F / 16F, 3F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(13F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 3F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0F / 16F, 0F / 16F, 13F / 16F, 3F / 16F, 16F / 16F, 16F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(13F / 16F, 0F / 16F, 13F / 16F, 16F / 16F, 16F / 16F, 16F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setOverrideBlockTexture(Blocks.glass.getBlockTextureFromSide(0));
		renderer.setRenderBounds(2F / 16F, 0F / 16F, 2F / 16F, 14F / 16F, 16F / 16F, 14F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.BLOCKFRAMELINE_RENDER_ID;
	}

}
