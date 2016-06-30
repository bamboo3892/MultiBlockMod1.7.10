package com.okina.multiblock.construct.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;

public class ConstructClockPulser extends BlockConstructBase {

	public ConstructClockPulser(int grade) {
		super(grade);
		setBlockName("mbm_clockPulser");
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = Blocks.redstone_block.getIcon(0, 0);
	}

	@Override
	public String getProseccorName() {
		return "clockPulser";
	}

	@Override
	public int getShiftLines() {
		return 2;
	}


}
