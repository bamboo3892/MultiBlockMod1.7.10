package com.okina.network;

import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.utils.Position;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class MultiBlockPacket implements IMessage {

	public Position tilePos;
	public Position partPos;
	public PacketType type;
	public Object value;

	public MultiBlockPacket() {}

	public MultiBlockPacket(Position tilePos, Position partPos, PacketType type, Object value) {
		this.tilePos = tilePos;
		this.partPos = partPos;
		this.type = type;
		this.value = value;
		if(!type.valueClass.isAssignableFrom(value.getClass())) throw new IllegalArgumentException();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			buf.writeInt(tilePos.x);
			buf.writeInt(tilePos.y);
			buf.writeInt(tilePos.z);
			buf.writeInt(partPos.x);
			buf.writeInt(partPos.y);
			buf.writeInt(partPos.z);
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
	public void fromBytes(ByteBuf buf) {
		try{
			tilePos = new Position(buf.readInt(), buf.readInt(), buf.readInt());
			partPos = new Position(buf.readInt(), buf.readInt(), buf.readInt());
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
	public String toString() {
		return String.format("MultiBlockPacket : %s : %s : type = %s : value = %s", tilePos, partPos, type, value);
	}

	/**client only*/
	public static class MultiBlockPacketHandler implements IMessageHandler<MultiBlockPacket, IMessage> {
		@Override
		public IMessage onMessage(MultiBlockPacket msg, MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.side.SERVER));
			if(Minecraft.getMinecraft().theWorld.getTileEntity(msg.tilePos.x, msg.tilePos.y, msg.tilePos.z) instanceof MultiBlockCoreTileEntity){
				MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) Minecraft.getMinecraft().theWorld.getTileEntity(msg.tilePos.x, msg.tilePos.y, msg.tilePos.z);
				tile.processPacket(msg.partPos, msg.type, msg.value);
			}
			return null;
		}
	}

}
