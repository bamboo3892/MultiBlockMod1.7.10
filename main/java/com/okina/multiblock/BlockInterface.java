package com.okina.multiblock;

import java.util.List;

import com.okina.block.ITestModBlock;
import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInterface extends BlockContainer implements ITestModBlock, IToolTipUser {

	public BlockInterface() {
		super(Material.iron);
		setBlockName("mbm_interface");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof BlockInterfaceTileEntity){
			if(player.getHeldItem() == null){
				if(!player.isSneaking()){
					return ((BlockInterfaceTileEntity) world.getTileEntity(x, y, z)).onRightClicked(player, side, hitX, hitY, hitZ);
				}else{
					return ((BlockInterfaceTileEntity) world.getTileEntity(x, y, z)).onShiftRightClicked(player, side, hitX, hitY, hitZ);
				}
			}else if(player.getHeldItem().getItem() instanceof IToolWrench){
				return ((BlockInterfaceTileEntity) world.getTileEntity(x, y, z)).onRightClickedByWrench(player, side, hitX, hitY, hitZ);
			}else{
				return ((BlockInterfaceTileEntity) world.getTileEntity(x, y, z)).onRightClicked(player, side, hitX, hitY, hitZ);
			}
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockInterfaceTileEntity();
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.enchanting_table.getIcon(1, 0);
	}

	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}
	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.rare;
	}

}
