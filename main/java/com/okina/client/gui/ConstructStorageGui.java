package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ConstructStorageGui extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/storage.png");

	private IInventory upperChestInventory;
	private IInventory lowerChestInventory;

	/**
	 * window height is calculated with these values; the more rows, the heigher
	 */
	private int inventoryRows;

	public ConstructStorageGui(IInventory invPlayer, IInventory invChest) {
		super(new ContainerChest(invPlayer, invChest));
		upperChestInventory = invPlayer;
		lowerChestInventory = invChest;
		allowUserInput = false;
		short short1 = 222;
		int i = short1 - 108;
		inventoryRows = invChest.getSizeInventory() / 9;
		ySize = i + inventoryRows * 18;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		fontRendererObj.drawString(lowerChestInventory.hasCustomInventoryName() ? lowerChestInventory.getInventoryName() : I18n.format(lowerChestInventory.getInventoryName(), new Object[0]), 8, 6, 4210752);
		fontRendererObj.drawString(upperChestInventory.hasCustomInventoryName() ? upperChestInventory.getInventoryName() : I18n.format(upperChestInventory.getInventoryName(), new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, inventoryRows * 18 + 17);
		drawTexturedModalRect(k, l + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}

}
