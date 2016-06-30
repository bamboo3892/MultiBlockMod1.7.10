package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.server.gui.SliderInputContainer;
import com.okina.tileentity.IGuiSliderUser;
import com.okina.utils.Position;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class GuiSliderInput extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/sliderinput.png");

	public IGuiSliderUser tile;
	public GuiSlider slider;

	public GuiSliderInput(IGuiSliderUser tile) {
		super(new SliderInputContainer(tile));
		this.tile = tile;
		xSize = 146;
		ySize = 40;
	}

	@Override
	public void initGui() {
		super.initGui();
		int i = width - xSize >> 1;
		int j = height - ySize >> 1;

		buttonList.add(new GuiButton(0, i + 4, j + 16, 15, 20, "-"));
		buttonList.add(new GuiButton(1, i + 127, j + 16, 15, 20, "+"));
		slider = new GuiSlider(2, i + 23, j + 16, 100, 20, tile);
		buttonList.add(slider);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(!guiButton.enabled){
			return;
		}
		int value = slider.getValue();
		if(guiButton.id == 0){
			value = value == 0 ? 0 : value - 1;
		}else if(guiButton.id == 1){
			value = value == tile.getMaxValue() ? value : value + 1;
		}
		slider.setValue(value);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		fontRendererObj.drawString(tile.getContainerName(), 6, 6, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		zLevel = 10;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void onGuiClosed() {
		Position p = tile.getPosition();
		TestCore.proxy.sendPacket(new SimpleTilePacket(p, PacketType.SLIDER_INPUT, slider.getValue()));
		super.onGuiClosed();
	}

	@Override
	protected void keyTyped(char p_73869_1_, int key) {
		if(key == 1 || key == mc.gameSettings.keyBindInventory.getKeyCode()){
			mc.thePlayer.closeScreen();
		}
	}

}
