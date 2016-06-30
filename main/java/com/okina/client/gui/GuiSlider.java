package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.tileentity.IGuiSliderUser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider extends GuiButton {

	private float ratio;
	private int value;
	private final IGuiSliderUser tile;
	public boolean isPressed;
	private static final String __OBFID = "CL_00000680";

	public GuiSlider(int id, int startX, int startY, int sizeX, int sizeY, IGuiSliderUser tile) {
		super(id, startX, startY, sizeX, sizeY, "");
		this.tile = tile;
		ratio = normalize(tile.getValue());
		value = tile.getValue();
		displayString = "Interval : " + tile.getValue();
	}

	public void setValue(int value) {
		ratio = normalize(value);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xPosition + (int) (ratio * (width - 8)), yPosition, 0, 66, 4, 20);
		drawTexturedModalRect(xPosition + (int) (ratio * (width - 8)) + 4, yPosition, 196, 66, 4, 20);
		displayString = "Interval : " + denormalize(ratio);
	}

	public int getValue() {
		return denormalize(ratio);
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
	 * this button.
	 */
	@Override
	public int getHoverState(boolean hoge) {
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
	@Override
	protected void mouseDragged(Minecraft minecraft, int x, int y) {
		if(visible){
			if(isPressed){
				ratio = (float) (x - (xPosition + 4)) / (float) (width - 8);

				if(ratio < 0.0F){
					ratio = 0.0F;
				}

				if(ratio > 1.0F){
					ratio = 1.0F;
				}
				displayString = "Interval : " + denormalize(ratio);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(xPosition + (int) (ratio * (width - 8)), yPosition, 0, 66, 4, 20);
			drawTexturedModalRect(xPosition + (int) (ratio * (width - 8)) + 4, yPosition, 196, 66, 4, 20);
		}
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	@Override
	public boolean mousePressed(Minecraft minecraft, int x, int y) {
		if(super.mousePressed(minecraft, x, y)){
			ratio = (float) (x - (xPosition + 4)) / (float) (width - 8);

			if(ratio < 0.0F){
				ratio = 0.0F;
			}

			if(ratio > 1.0F){
				ratio = 1.0F;
			}

			/*
			minecraft.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.ratio));
			this.displayString = minecraft.gameSettings.getKeyBinding(this.options);
			*/
			isPressed = true;
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int x, int y) {
		isPressed = false;
	}

	private int denormalize(float ratio) {
		if(this.ratio > 1.0F){
			this.ratio = 1.0F;
		}
		if(this.ratio < 0.0F){
			this.ratio = 0.0F;
		}
		return (int) ((ratio * (tile.getMaxValue() - tile.getMinValue())) + tile.getMinValue());
	}

	private float normalize(int value) {
		if(value < tile.getMinValue()){
			value = tile.getMinValue();
		}else if(value > tile.getMaxValue()){
			value = tile.getMaxValue();
		}
		return (float) (value - tile.getMinValue()) / (tile.getMaxValue() - tile.getMinValue());
	}

}
