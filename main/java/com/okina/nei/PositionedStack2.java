package com.okina.nei;

import codechicken.nei.PositionedStack;

public class PositionedStack2 extends PositionedStack {

	public double z;

	public PositionedStack2(PositionedStack stack, double z) {
		super(stack.item, stack.relx, stack.rely);
		this.z = z;
	}

	public PositionedStack2(Object object, int x, int y, double z) {
		super(object, x, y);
		this.z = z;
	}

	public PositionedStack2(Object object, int x, int y, double z, boolean genPerms) {
		super(object, x, y, genPerms);
		this.z = z;
	}

}
