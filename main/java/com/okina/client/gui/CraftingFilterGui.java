package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.inventory.CraftingFilterInventory;
import com.okina.main.TestCore;
import com.okina.server.gui.CraftingFilterContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CraftingFilterGui extends GuiContainer {

	private static final ResourceLocation designTableGuiTextures = new ResourceLocation(TestCore.MODID + ":textures/gui/container/crafting_filter.png");

	public CraftingFilterGui(InventoryPlayer inventoryPlayer, CraftingFilterInventory filter, World world) {
		super(new CraftingFilterContainer(inventoryPlayer, filter, world));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString("Crafting Filter", 28, 6, 4210752);
		fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(designTableGuiTextures);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

}
