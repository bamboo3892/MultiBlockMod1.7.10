package com.okina.multiblock.construct.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;

public class ConstructRepeater extends BlockConstructBase {

	public ConstructRepeater(int grade) {
		super(grade);
		setBlockName("mbm_repeater");
	}

	@Override
	public String getProseccorName() {
		return "repeater";
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.quartz_block.getIcon(2, 0);
	}

	@Override
	public int getShiftLines() {
		return 2;
	}

}
