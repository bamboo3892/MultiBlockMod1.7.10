package com.okina.multiblock;

import com.okina.block.ITestModBlock;
import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class DisassemblyTable extends BlockContainer implements ITestModBlock {

	public IIcon sideIcon;

	public DisassemblyTable() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
		setBlockName("mbm_disassemblyTable");
		setBlockBounds(0, 0, 0, 1, 5f / 16f, 1);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity table = (DisassemblyTableTileEntity) world.getTileEntity(x, y, z);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
				table.onRightClickedByWrench(player, side, hitX, hitY, hitZ);
				return true;
			}else{
				table.onRightClicked(player, side, hitX, hitY, hitZ);
				return true;
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity tile = (DisassemblyTableTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DisassemblyTableTileEntity();
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.epic;
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
		return TestCore.DISASSEMBLY_TABLE_RENDER_ID;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (side == 0 || side == 1) ? blockIcon : sideIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(TestCore.MODID + ":disassembly_table");
		sideIcon = register.registerIcon(TestCore.MODID + ":disassembly_table_side");
	}

}
