package com.okina.network;

import com.okina.main.TestCore;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.Position;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class SimpleTilePacket implements IMessage {

	public int x;
	public int y;
	public int z;
	public PacketType type;
	public Object value;

	public SimpleTilePacket() {}

	public SimpleTilePacket(ISimpleTilePacketUser tile, PacketType type, Object value) {
		this(tile.getPosition(), type, value);
	}

	public SimpleTilePacket(Position p, PacketType type, Object value) {
		x = p.x;
		y = p.y;
		z = p.z;
		this.type = type;
		this.value = value;
		if(!type.valueClass.isAssignableFrom(value.getClass())) throw new IllegalArgumentException();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
			type = PacketType.getFromId(buf.readByte());
			if(type.valueClass == Integer.class){
				value = buf.readInt();
			}else if(type.valueClass == String.class){
				value = ByteBufUtils.readUTF8String(buf);
			}else if(type.valueClass == NBTTagCompound.class){
				value = ByteBufUtils.readTag(buf);
			}
		}catch (Exception e){
			System.err.println("Illegal packet received : " + this);
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeByte(type.id);
			if(type.valueClass == Integer.class){
				buf.writeInt((Integer) value);
			}else if(type.valueClass == String.class){
				ByteBufUtils.writeUTF8String(buf, (String) value);
			}else if(type.valueClass == NBTTagCompound.class){
				ByteBufUtils.writeTag(buf, (NBTTagCompound) value);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return String.format("Simple Tile Packet : x, y, z = %s, %s, %s : type = %s : value = %s", x, y, z, type, value);
	}

	/**server only*/
	public static class SimpleTilePacketHandler implements IMessageHandler<SimpleTilePacket, IMessage> {
		@Override
		public IMessage onMessage(SimpleTilePacket msg, MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.getServerHandler().playerEntity.getDisplayName()));
			if(ctx.getServerHandler().playerEntity.worldObj.getTileEntity(msg.x, msg.y, msg.z) instanceof ISimpleTilePacketUser){
				ISimpleTilePacketUser tile = (ISimpleTilePacketUser) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(msg.x, msg.y, msg.z);
				tile.processCommand(msg.type, msg.value);
				TestCore.packetDispatcher.sendToDimension(msg, ctx.getServerHandler().playerEntity.dimension);
			}
			return null;
		}
	}

	/**client only*/
	public static class SimpleTileReplyPacketHandler implements IMessageHandler<SimpleTilePacket, IMessage> {
		@Override
		public IMessage onMessage(SimpleTilePacket msg, MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.side.SERVER));
			if(Minecraft.getMinecraft().theWorld != null){
				if(Minecraft.getMinecraft().theWorld.getTileEntity(msg.x, msg.y, msg.z) instanceof ISimpleTilePacketUser){
					ISimpleTilePacketUser tile = (ISimpleTilePacketUser) Minecraft.getMinecraft().theWorld.getTileEntity(msg.x, msg.y, msg.z);
					tile.processCommand(msg.type, msg.value);
				}
			}
			return null;
		}
	}

}
