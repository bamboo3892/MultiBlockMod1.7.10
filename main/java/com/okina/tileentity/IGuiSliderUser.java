package com.okina.tileentity;

import com.okina.utils.Position;

public interface IGuiSliderUser {

	int getValue();
	int getMinValue();
	int getMaxValue();
	String getContainerName();
	Position getPosition();

}
