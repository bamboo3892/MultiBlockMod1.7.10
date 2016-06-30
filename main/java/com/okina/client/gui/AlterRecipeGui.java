package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.register.AlterRecipeRegister.AlterRecipe;
import com.okina.server.gui.DummyContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class AlterRecipeGui extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation("gui:textures/gui/container/base.png");
	private int xSize1 = 176;
	private int ySize1 = 166;

	public AlterRecipeGui(AlterRecipe recipe) {
		super(new DummyContainer());
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialRenderTick, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (width - xSize1) / 2;
		int y = (height - ySize1) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize1, ySize1);
	}

}
