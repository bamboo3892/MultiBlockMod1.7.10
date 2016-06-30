package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;
import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.server.gui.ConstructFilterContainer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ConstructFilterGui extends GuiContainer {

	private final static ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/filter.png");
	private static final ResourceLocation buttonTextures = new ResourceLocation(TestCore.MODID + ":textures/gui/container/filter_buttons.png");
	private IInventory player;
	private IFilterUser tile;
	private FilterInventory filter;
	private int side;
	private GuiTextureButton[] buttons = new GuiTextureButton[4];
	private int priority;

	public ConstructFilterGui(IInventory player, IFilterUser tile, int side) {
		super(new ConstructFilterContainer(player, (FilterInventory) tile.getFilter(side)));
		this.player = player;
		this.tile = tile;
		filter = (FilterInventory) tile.getFilter(side);
		priority = filter.priority;
		this.side = side;
		xSize = 176;
		ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		int i = width - xSize >> 1;
		int j = height - ySize >> 1;

		buttons[0] = new GuiTextureButton(0, i + 121, j + 21, buttonTextures, 0, 0);
		buttons[1] = new GuiTextureButton(1, i + 146, j + 21, buttonTextures, 16, 0);
		buttons[2] = new GuiTextureButton(2, i + 121, j + 45, buttonTextures, 32, 0);
		buttons[3] = new GuiTextureButton(3, i + 146, j + 45, buttonTextures, 48, 0);
		buttons[0].riseUp = filter.useDamage;
		buttons[1].riseUp = filter.useNBT;
		buttons[2].riseUp = filter.useOreDictionary;
		buttons[3].riseUp = filter.filterBan;
		buttonList.add(buttons[0]);
		buttonList.add(buttons[1]);
		buttonList.add(buttons[2]);
		buttonList.add(buttons[3]);

		buttonList.add(new GuiButton(4, i + 8, j + 39, 15, 20, "-"));
		buttonList.add(new GuiButton(5, i + 42, j + 39, 15, 20, "+"));
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(!guiButton.enabled){
			return;
		}
		if(guiButton instanceof GuiTextureButton){
			GuiTextureButton button = (GuiTextureButton) guiButton;
			button.riseUp = !button.riseUp;
		}
		if(guiButton.id == 4){
			priority = priority <= 1 ? 1 : priority - 1;
		}else if(guiButton.id == 5){
			priority = priority >= 6 ? 6 : priority + 1;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		fontRendererObj.drawString(StatCollector.translateToLocal(filter.getInventoryName()), 8, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.filter"), 8, ySize - 96 + 2, 0x404040);
		fontRendererObj.drawString("Priority", 12, 22, 0x404040);
		fontRendererObj.drawString("" + priority, 30, 46, 0xFF4040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialRenderTick, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	public void onGuiClosed() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("filterId", 1);
		tag.setInteger("side", side);

		NBTTagCompound flags = new NBTTagCompound();
		flags.setBoolean("ban", buttons[3].riseUp);
		flags.setBoolean("nbt", buttons[1].riseUp);
		flags.setBoolean("damage", buttons[0].riseUp);
		flags.setBoolean("ore", buttons[2].riseUp);
		flags.setInteger("priority", priority);
		tag.setTag("flags", flags);
		TestCore.proxy.sendPacket(new SimpleTilePacket(tile.getPosition(), PacketType.FILTER_NBT_FROM_GUI, tag));
		super.onGuiClosed();
		//Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char p_73869_1_, int key) {
		if(key == 1 || key == mc.gameSettings.keyBindInventory.getKeyCode()){
			mc.thePlayer.closeScreen();
		}
	}

}
