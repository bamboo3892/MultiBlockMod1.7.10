package com.okina.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiTextureButton extends GuiButton {

	private final ResourceLocation texture;
	private final int offsetX;
	private final int offsetY;
	public boolean riseUp;
	private static final String __OBFID = "CL_00000743";

	protected GuiTextureButton(int buttonId, int startX, int startY, ResourceLocation texture, int offsetX, int offsetY) {
		super(buttonId, startX, startY, 20, 20, "");
		this.texture = texture;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(visible){
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int k = getHoverState(field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if(riseUp){
				drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
				drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
				minecraft.getTextureManager().bindTexture(texture);
				drawTexturedModalRect(xPosition + 2, yPosition + 2, offsetX, offsetY, width - 4, height - 4);
			}else{
				drawTexturedModalRect(xPosition, yPosition, 0, 46, width / 2, height);
				drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46, width / 2, height);
				minecraft.getTextureManager().bindTexture(texture);
				drawTexturedModalRect(xPosition + 2, yPosition + 2, offsetX, offsetY + 16, width - 4, height - 4);
			}

			mouseDragged(minecraft, mouseX, mouseY);
		}
	}

}
