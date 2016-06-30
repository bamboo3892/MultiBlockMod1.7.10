package com.okina.block;

import java.util.List;

import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockDenseCactus2 extends Block implements IToolTipUser {

	public IIcon[] icons = new IIcon[3];

	public BlockDenseCactus2() {
		super(Material.cactus);
		setHardness(1.0F);
		setBlockName("mbm_dense_cactus_2");
		setCreativeTab(TestCore.testCreativeTab);
	}

	@Override
	public int damageDropped(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
		for (int i = 0; i < 3; ++i){
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return icons[(meta >= 0 && meta < 3) ? meta : 0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < 3; ++i){
			icons[i] = register.registerIcon(TestCore.MODID + ":dense_cactus_2_" + i);
		}
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
