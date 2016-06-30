package com.okina.multiblock;

import com.okina.main.TestCore;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockCasing extends BlockContainer {

	private IIcon iconNull;

	public MultiBlockCasing() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setBlockName("mbm_multiBlockCasing");
		setLightOpacity(0);
		setHardness(1F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
			MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(x, y, z);
			if(player.getHeldItem() == null){
				if(!player.isSneaking()){
					return tile.onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
				}else{
					return tile.onShiftRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
				}
			}else if(player.getHeldItem().getItem() instanceof IToolWrench){
				return tile.onRightClickedByWrench(world, player, side, hitX, hitY, hitZ);
			}else{
				return tile.onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(world, player, true);
		if(mop.blockX == x && mop.blockY == y && mop.blockZ == z && world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
			((MultiBlockCasingTileEntity) world.getTileEntity(x, y, z)).onLeftClicked(player, mop.sideHit, mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
		}
	}

	//	@Override
	//	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
	//		super.onPostBlockPlaced(world, x, y, z, meta);
	//		for (int i = -1; i < 2; i++){
	//			for (int j = -1; j < 2; j++){
	//				for (int k = -1; k < 2; k++){
	//					if(i == 0 && j == 0 && k == 0) continue;
	//					TileEntity tile = world.getTileEntity(x + i, y + j, z + k);
	//					if(tile instanceof MultiBlockCoreTileEntity){
	//						if(((MultiBlockCoreTileEntity) tile).connect()) return;
	//					}
	//				}
	//			}
	//		}
	//	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(!(block instanceof MultiBlockCasing || block instanceof MultiBlockCore) && block != null && block.canProvidePower()){
			if(world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
				MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(x, y, z);
				if(tile.isConnected()){
					for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
						if(!(world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCasingTileEntity || world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCoreTileEntity)){
							int power = world.getIndirectPowerLevelTo(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite().ordinal());
							tile.getCoreTile().changeSidePowered(dir.ordinal(), power != 0);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	//	@Override
	//	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
	//		if(!world.isRemote){
	//			for (int i = -1; i < 2; i++){
	//				for (int j = -1; j < 2; j++){
	//					for (int k = -1; k < 2; k++){
	//						if(i == 0 && j == 0 && k == 0) continue;
	//						TileEntity tile = world.getTileEntity(x + i, y + j, z + k);
	//						if(tile instanceof MultiBlockCoreTileEntity){
	//							((MultiBlockCoreTileEntity) tile).disconnect();
	//						}
	//					}
	//				}
	//			}
	//		}
	//		super.breakBlock(world, x, y, z, block, meta);
	//	}

	@Override
	public int getMobilityFlag() {
		return 2;
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

	@Override
	public int getRenderType() {
		return TestCore.MULTIBLOCK_RENDER_ID;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if(world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCasing || world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCore){
			return iconNull;
		}else{
			if(world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
				MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(x, y, z);
				if(tile.isConnected()){
					return tile.getCoreTile().hasInterfaceConnection(side) ? blockIcon : iconNull;
				}
			}
		}
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(TestCore.MODID + ":construct_pane");
		iconNull = register.registerIcon(TestCore.MODID + ":null");

	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MultiBlockCasingTileEntity();
	}

}
