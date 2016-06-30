package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class ConstructAlter extends BlockConstructBase {

	public static IIcon ICON_EFFECT;

	public ConstructAlter(int grade) {
		super(grade);
		setBlockName("mbm_alter");
	}

	@Override
	public String getProseccorName() {
		return "alter";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(TestCore.MODID + ":alter");
		ICON_EFFECT = register.registerIcon(TestCore.MODID + ":alter_effect2");
	}

	@Override
	public int getShiftLines() {
		return 4;
	}

}
