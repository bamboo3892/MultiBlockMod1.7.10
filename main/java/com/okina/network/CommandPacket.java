package com.okina.network;

import com.okina.main.TestCore;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class CommandPacket implements IMessage {

	public String command;
	public String value;

	public CommandPacket() {}

	public CommandPacket(String command, String value) {
		if(command == null || value == null) throw new IllegalArgumentException();
		this.command = command;
		this.value = value;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			ByteBufUtils.writeUTF8String(buf, command);
			ByteBufUtils.writeUTF8String(buf, value);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			command = ByteBufUtils.readUTF8String(buf);
			value = ByteBufUtils.readUTF8String(buf);
		}catch (Exception e){
			System.err.println("Illegal packet received : " + this);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Command Packet : /mbm " + command + " " + value;
	}

	/**client only*/
	public static class CommandPacketHandler implements IMessageHandler<CommandPacket, IMessage> {
		@Override
		public IMessage onMessage(CommandPacket msg, MessageContext ctx) {
			try{
				if("particleSpawnRate".equals(msg.command)){
					int value = Integer.parseInt(msg.value);
					if(value >= 0 && value <= 100){
						TestCore.effectProperties.particleSpawnRate = value;
						TestCore.proxy.updatePropertyFile();
						Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Set particleSpawnRate " + value));
					}
				}else if("renderPartsFancy".equals(msg.command)){
					boolean value = Boolean.parseBoolean(msg.value);
					TestCore.effectProperties.renderPartsFancy = value;
					TestCore.proxy.updatePropertyFile();
					Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Set renderPartsFancy " + value));
				}
			}catch (Exception e){
			}
			return null;
		}
	}

}
