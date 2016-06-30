package com.okina.multiblock.construct.mode;

import com.okina.multiblock.construct.processor.ContainerProcessor;
import com.okina.utils.ColoredString;

public class NormalMode extends ContainerModeBase {

	public NormalMode(ContainerProcessor container) {
		super(container);
	}

	@Override
	public boolean checkTileNewConnection() {
		return false;
	}

	@Override
	public boolean checkTileExistingConnection() {
		return false;
	}

	@Override
	public boolean checkPartDesignatedConnection() {
		return false;
	}

	@Override
	public void progressMode() {

	}

	@Override
	public void reset() {

	}

	@Override
	public int getModeIndex() {
		return 0;
	}

	@Override
	public ColoredString getModeNameForRender() {
		return new ColoredString("NORMAL", 0xffffff);
	}

}
