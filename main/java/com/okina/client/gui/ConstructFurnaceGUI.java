package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;
import com.okina.server.gui.ConstructFurnaceContainer;
import com.okina.server.gui.ConstructFurnaceContainer.IFurnaceGuiUser;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class ConstructFurnaceGUI extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/furnace.png");

	private IFurnaceGuiUser tileFurnace;
	private static final String __OBFID = "CL_00000758";

	public ConstructFurnaceGUI(InventoryPlayer inventoryPlayer, IFurnaceGuiUser tile) {
		super(new ConstructFurnaceContainer(inventoryPlayer, tile));
		tileFurnace = tile;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = tileFurnace.hasCustomInventoryName() ? tileFurnace.getInventoryName() : I18n.format(tileFurnace.getInventoryName(), new Object[0]);
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

		if(tileFurnace.isBurning()){
			int i1 = tileFurnace.getBurnTimeRemainingScaled(13);
			drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			i1 = tileFurnace.getCookProgressScaled(24);
			drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
		}
	}

}
