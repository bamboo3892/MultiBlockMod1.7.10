package com.okina.client.renderer;

import com.okina.main.TestCore;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class BlockFrameRenderer implements ISimpleBlockRenderingHandler {

	public BlockFrameRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(Blocks.coal_block.getBlockTextureFromSide(0));
		RenderingHelper.renderInvCubeFrame(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 3F / 16F);
		renderer.setOverrideBlockTexture(Blocks.stone_slab.getBlockTextureFromSide(0));
		RenderingHelper.renderInvCuboid(renderer, block, 2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(Blocks.coal_block.getBlockTextureFromSide(0));
		RenderingHelper.renderCubeFrame(x, y, z, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 3F / 16F, renderer);
		renderer.setOverrideBlockTexture(Blocks.stone_slab.getBlockTextureFromSide(0));
		renderer.setRenderBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.clearOverrideBlockTexture();
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.BLOCKFRAME_RENDER_ID;
	}

}
