package com.okina.multiblock;

import java.util.ArrayList;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockCore extends BlockContainer {

	public static IIcon iconNull;
	public static IIcon background;

	public MultiBlockCore() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setBlockName("mbm_multiBlockCore");
		setLightOpacity(0);
		setHardness(1F);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
		int l = MathHelper.floor_double(livingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, l, 3);
		if(world.getBlock(x, y, z) == this){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag == null){
				FMLLog.severe("Tag deleted itemstack", new Object[0]);
				tag = new NBTTagCompound();
			}
			MultiBlockCoreTileEntity tile = MultiBlockCoreTileEntity.createTileFromNBT(tag);
			world.setTileEntity(x, y, z, tile);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(x, y, z);
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
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		ItemStack stack = new ItemStack(TestCore.multiBlockCore, 1);
		if(world.getTileEntity(x, y, z) instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(x, y, z);
			tile.writeToNBTForItemDrop(stack);
		}
		world.spawnEntityInWorld(new EntityItem(world, x, y, z, stack));
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
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

	@Override
	public void registerBlockIcons(IIconRegister register) {
		background = register.registerIcon(TestCore.MODID + ":multi_block_background");
		blockIcon = register.registerIcon(TestCore.MODID + ":construct_pane");
		iconNull = register.registerIcon(TestCore.MODID + ":null");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if(world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCasing || world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ) instanceof MultiBlockCore){
			return iconNull;
		}else{
			if(world.getTileEntity(x, y, z) instanceof MultiBlockCoreTileEntity){
				MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(x, y, z);
				if(tile.connected){
					return iconNull;
				}else{
					return tile.hasInterfaceConnection(side) ? blockIcon : iconNull;
				}
			}
		}
		return blockIcon;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MultiBlockCoreTileEntity();
	}

}
