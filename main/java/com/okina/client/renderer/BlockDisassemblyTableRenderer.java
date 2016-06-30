package com.okina.client.renderer;

import com.okina.main.TestCore;
import com.okina.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockDisassemblyTableRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		RenderingHelper.renderInvCuboid(renderer, block, 0, 0, 0, 1, 5f / 16f, 1, 0);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.setRenderBounds(0, 0, 0, 1, 5f / 16f, 1);
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.DISASSEMBLY_TABLE_RENDER_ID;
	}

}
