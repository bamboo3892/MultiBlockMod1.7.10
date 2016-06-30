package com.okina.multiblock.construct.block;

import com.okina.main.TestCore;

import net.minecraft.client.renderer.texture.IIconRegister;

public class ConstructEventCatcher extends BlockConstructBase {

	public ConstructEventCatcher(int grade) {
		super(grade);
		setBlockName("mbm_eventCatcher");
	}

	@Override
	public String getProseccorName() {
		return "eventCatcher";
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(TestCore.MODID + ":event_catcher");
	}

	@Override
	public int getShiftLines() {
		return 2;
	}

}
