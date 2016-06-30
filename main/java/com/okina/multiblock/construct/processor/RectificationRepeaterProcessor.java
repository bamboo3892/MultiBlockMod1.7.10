package com.okina.multiblock.construct.processor;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.ISignalReceiver;
import com.okina.network.PacketType;
import com.okina.utils.ColoredString;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;

public class RectificationRepeaterProcessor extends SignalEmitterProcessor implements ISignalReceiver {

	public static final ResourceLocation NEXT_EMIT_PANE = new ResourceLocation(TestCore.MODID + ":textures/blocks/next_emit_pane.png");

	/**use only on server*/
	public int lastEmittedDirection = 0;
	public int partialTick = -1;

	public RectificationRepeaterProcessor(IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		super(pc, isRemote, isTile, x, y, z, grade);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(partialTick != -1){
			partialTick++;
			if(partialTick >= 3){
				int dir = getNextEmitDirection();
				if(dir != -1){
					emitSignal(dir);
					lastEmittedDirection = dir;
					pc.sendPacket(PacketType.EFFECT, 0);
					pc.sendPacket(PacketType.OTHER2, lastEmittedDirection);
				}
				partialTick = -1;
			}
		}
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.OTHER2 && value instanceof Integer){//both side
			lastEmittedDirection = (Integer) value;
		}
		super.processCommand(type, value);
	}

	@Override
	public String getNameForNBT() {
		return "rectificationRepeater";
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		lastEmittedDirection = tag.getInteger("lastEmittedDirection");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("lastEmittedDirection", lastEmittedDirection);
	}

	//non-override////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int getNextEmitDirection() {
		int dir = lastEmittedDirection;
		do{
			dir = dir >= 5 ? 0 : dir + 1;
			if(flagIO[dir] == 1 && connection[dir] != null && connection[dir].hasTile()) return dir;
		}while (dir != lastEmittedDirection);
		if(flagIO[dir] == 1 && connection[dir] != null && connection[dir].hasTile()){
			return dir;
		}
		return -1;
	}

	@Override
	public void onSignalReceived() {
		assert !isRemote;
		if(partialTick == -1){
			partialTick = 0;
		}
	}

	//render//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ColoredString getNameForHUD() {
		return new ColoredString("RECTIFICATION REPEATER", ColorCode[grade]);
	}

	//tile entity//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public List<ColoredString> getHUDStringsForCenter(MovingObjectPosition mop, double renderTicks) {
		ColoredString str = null;
		if(flagIO[mop.sideHit] == 0){
			str = new ColoredString("Input", 0x00BFFF);
		}else if(flagIO[mop.sideHit] == 1 && connection[mop.sideHit] != null && pc.getProcessor(connection[mop.sideHit].x, connection[mop.sideHit].y, connection[mop.sideHit].z) != null){
			str = new ColoredString((mop.sideHit == getNextEmitDirection() ? "Next" : "") + "Output >> " + pc.getProcessor(connection[mop.sideHit].x, connection[mop.sideHit].y, connection[mop.sideHit].z).getNameForHUD().str, 0xFF8C00);
		}else if(flagIO[mop.sideHit] == 1){
			str = new ColoredString("Output", 0xFF8C00);
		}
		List<ColoredString> list = new ArrayList<ColoredString>();
		if(str != null){
			list.add(str);
		}
		return list;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void customRenderTile(float partialTicks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		int dir = getNextEmitDirection();
		Minecraft.getMinecraft().renderEngine.bindTexture(NEXT_EMIT_PANE);
		double d = 1.0D;
		double d2 = 1.99d / 16d;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(1f, 1f, 1f, 1f);
		if(dir == 0){
			//y neg
			tessellator.addVertexWithUV(0, d2, 0, 0, 0);
			tessellator.addVertexWithUV(d, d2, 0, 0, 1);
			tessellator.addVertexWithUV(d, d2, d, 1, 1);
			tessellator.addVertexWithUV(0, d2, d, 1, 0);
		}else if(dir == 1){
			//y pos
			tessellator.addVertexWithUV(0, d - d2, 0, 0, 0);
			tessellator.addVertexWithUV(0, d - d2, d, 0, 1);
			tessellator.addVertexWithUV(d, d - d2, d, 1, 1);
			tessellator.addVertexWithUV(d, d - d2, 0, 1, 0);
		}else if(dir == 4){
			//z neg
			tessellator.addVertexWithUV(d2, 0, 0, 0, 0);
			tessellator.addVertexWithUV(d2, 0, d, 0, 1);
			tessellator.addVertexWithUV(d2, d, d, 1, 1);
			tessellator.addVertexWithUV(d2, d, 0, 1, 0);
		}else if(dir == 5){
			//z pos
			tessellator.addVertexWithUV(d - d2, 0, 0, 0, 0);
			tessellator.addVertexWithUV(d - d2, d, 0, 0, 1);
			tessellator.addVertexWithUV(d - d2, d, d, 1, 1);
			tessellator.addVertexWithUV(d - d2, 0, d, 1, 0);
		}else if(dir == 2){
			//x neg
			tessellator.addVertexWithUV(0, 0, d2, 0, 0);
			tessellator.addVertexWithUV(0, d, d2, 0, 1);
			tessellator.addVertexWithUV(d, d, d2, 1, 1);
			tessellator.addVertexWithUV(d, 0, d2, 1, 0);
		}else if(dir == 3){
			///x pos
			tessellator.addVertexWithUV(0, 0, d - d2, 0, 0);
			tessellator.addVertexWithUV(d, 0, d - d2, 0, 1);
			tessellator.addVertexWithUV(d, d, d - d2, 1, 1);
			tessellator.addVertexWithUV(0, d, d - d2, 1, 0);
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}

	//part////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Block getRenderBlock() {
		return TestCore.constructRectificationRepeater[grade];
	}

}
