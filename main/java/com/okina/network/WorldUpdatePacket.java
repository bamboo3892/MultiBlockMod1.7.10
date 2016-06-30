package com.okina.network;

import java.util.List;

import com.google.common.collect.Lists;
import com.okina.tileentity.ISimpleTilePacketUser;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class WorldUpdatePacket implements IMessage {

	public List<SimpleTilePacket> packets;

	public WorldUpdatePacket() {}

	public WorldUpdatePacket(List<SimpleTilePacket> packets) {
		if(packets == null || packets.isEmpty() || packets.contains(null)) throw new IllegalArgumentException();
		this.packets = packets;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			buf.writeInt(packets.size());
			for (SimpleTilePacket packet : packets){
				packet.toBytes(buf);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		packets = Lists.newArrayList();
		try{
			int size = buf.readInt();
			for (int i = 0; i < size; i++){
				SimpleTilePacket packet = new SimpleTilePacket();
				packet.fromBytes(buf);
				packets.add(packet);
			}
		}catch (Exception e){
			System.err.println("Illegal packet received : " + this);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "World Update Packet : Packet Length = " + packets.size();
	}

	/**client only*/
	public static class WorldUpdatePacketHandler implements IMessageHandler<WorldUpdatePacket, IMessage> {
		@Override
		public IMessage onMessage(WorldUpdatePacket msg, MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.side.SERVER));
			if(Minecraft.getMinecraft().theWorld != null && msg.packets != null){
				for (SimpleTilePacket packet : msg.packets){
					if(Minecraft.getMinecraft().theWorld.getTileEntity(packet.x, packet.y, packet.z) instanceof ISimpleTilePacketUser){
						ISimpleTilePacketUser tile = (ISimpleTilePacketUser) Minecraft.getMinecraft().theWorld.getTileEntity(packet.x, packet.y, packet.z);
						tile.processCommand(packet.type, packet.value);
					}
				}
			}
			return null;
		}
	}

}
