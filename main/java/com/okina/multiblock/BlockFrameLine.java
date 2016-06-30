package com.okina.multiblock;

import com.okina.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrameLine extends Block {

	public BlockFrameLine() {
		super(Material.iron);
		setBlockName("blockFrameLine");
		setBlockTextureName("redstone_block");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		check(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		check(world, x, y, z);
	}

	private void check(World world, int x, int y, int z) {
		int meta = 0;
		for (ForgeDirection dir : ForgeDirection.values()){
			if(dir.ordinal() >= 6) break;
			int newX = x + dir.offsetX;
			int newY = y + dir.offsetY;
			int newZ = z + dir.offsetZ;
			if(world.getBlock(newX, newY, newZ) == this){
				meta = dir.ordinal() / 2 + 1;
			}
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int meta) {
		return true;
	}

	/*
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta){
		return Blocks.glass.getIcon(0, 0);
	}
	*/

	@Override
	public int getRenderType() {
		return TestCore.BLOCKFRAMELINE_RENDER_ID;
	}

}
