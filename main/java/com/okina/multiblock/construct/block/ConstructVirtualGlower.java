package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class ConstructVirtualGlower extends BlockConstructBase {

	private IIcon top;
	private IIcon side;

	public ConstructVirtualGlower(int grade) {
		super(grade);
		setBlockName("mbm_virtualGrower");
	}

	@Override
	public String getProseccorName() {
		return "virtualGrower";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? top : (side == 0 ? this.side : blockIcon);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.log.getBlockTextureFromSide(2);
		side = Blocks.log.getBlockTextureFromSide(0);
		top = register.registerIcon(TestCore.MODID + ":grass_top");
	}

	@Override
	public int getShiftLines() {
		return 4;
	}

}
