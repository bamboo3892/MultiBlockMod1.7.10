package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;
import com.okina.server.gui.ConstructContainerContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ConstructContainerGui extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/container.png");

	private IInventory containerInv;

	public ConstructContainerGui(InventoryPlayer playerInv, IInventory containerInv) {
		super(new ConstructContainerContainer(playerInv, containerInv));
		this.containerInv = containerInv;
		xSize = 176;
		ySize = 166;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(containerInv.getInventoryName(), 8, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

}
