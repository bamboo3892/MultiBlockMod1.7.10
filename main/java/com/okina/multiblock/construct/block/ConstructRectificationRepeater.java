package com.okina.multiblock.construct.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;

public class ConstructRectificationRepeater extends BlockConstructBase {

	public ConstructRectificationRepeater(int grade) {
		super(grade);
		setBlockName("mbm_rectification_repeater");
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.quartz_block.getIcon(0, 1);
	}

	@Override
	public String getProseccorName() {
		return "rectificationRepeater";
	}

	@Override
	public int getShiftLines() {
		return 2;
	}

}
