package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.item.TestItem;
import com.okina.server.gui.DummyContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class InformationGui extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation("gui:textures/gui/container/base.png");
	private int xSize1;
	private int ySize1;
	private EntityPlayer player;

	public InformationGui(EntityPlayer player) {
		super(new DummyContainer());
		xSize1 = 176;
		ySize1 = 166;
		this.player = player;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		Item item = player.getCurrentEquippedItem().getItem();
		if(!(item instanceof TestItem)) return;
		TestItem item2 = (TestItem) item;
		String[] messages = item2.getMessage(player.worldObj, player);
		for (int i = 0; i < messages.length; i++){
			fontRendererObj.drawString(messages[i], 8, 6 + 10 * i, 0x404040);
		}
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
