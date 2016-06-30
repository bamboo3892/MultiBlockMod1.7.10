package com.okina.multiblock;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFrame extends BlockContainer {

	public BlockFrame() {
		super(Material.iron);
		setBlockName("mbm_blockFrame");
		setBlockTextureName("stone");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
			if(world.getTileEntity(x, y, z) instanceof BlockFrameTileEntity){
				((BlockFrameTileEntity) world.getTileEntity(x, y, z)).tryConstruct(player);
			}
			return true;
		}
		return false;
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
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
		return true;
	}

	@Override
	public int getRenderType() {
		return TestCore.BLOCKFRAME_RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new BlockFrameTileEntity();
	}

}
