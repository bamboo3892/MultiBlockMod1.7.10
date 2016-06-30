package com.okina.multiblock;

import java.util.List;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * metadata
 * 0 : pipe
 */
public class BlockPipe extends BlockContainer {

	public BlockPipe() {
		super(Material.rock);
		setCreativeTab(TestCore.testCreativeTab);
		setBlockName("mbm_pipe");
		setLightOpacity(0);
		setHardness(1.5F);
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float htiY, float hitZ, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		if(world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof BlockPipeTileEntity){
			return world.getBlockMetadata(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		}
		return 0;
	}

	/**when game starting , this is called before TIleEntity.readFromNBT*/
	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		super.onPostBlockPlaced(world, x, y, z, meta);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		tile.checkConnection();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
			if(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity){
				int meta = world.getBlockMetadata(x, y, z);
				if(player.isSneaking()){
					world.setBlockMetadataWithNotify(x, y, z, meta == 0 ? 3 : meta - 1, 3);
				}else{
					world.setBlockMetadataWithNotify(x, y, z, meta == 3 ? 0 : meta + 1, 3);
				}
				((BlockPipeTileEntity) world.getTileEntity(x, y, z)).checkConnection();
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
					if(world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof BlockPipeTileEntity){
						((BlockPipeTileEntity) world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)).checkConnection();
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		tile.checkConnection();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity){
			BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, @SuppressWarnings("rawtypes") List list, Entity entity) {
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		if(tile.connection == null) return;
		if(tile.connection[0]){
			setBlockBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[1]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[2]){
			setBlockBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[3]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[4]){
			setBlockBounds(0F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[5]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		setBlockBoundsForItemRender();
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
		return TestCore.BLOCKPIPE_RENDER_ID;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		blockIcon = Blocks.redstone_block.getIcon(0, 0);
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.redstone_block.getIcon(0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockPipeTileEntity();
	}

}
