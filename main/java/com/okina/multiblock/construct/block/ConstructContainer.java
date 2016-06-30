package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import net.minecraft.client.renderer.texture.IIconRegister;

public class ConstructContainer extends BlockConstructBase {

	public ConstructContainer(int grade) {
		super(grade);
		setBlockName("mbm_container");
	}

	@Override
	public String getProseccorName() {
		return "container";
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(TestCore.MODID + ":null");
	}

	@Override
	public int getShiftLines() {
		return 3;
	}


}
