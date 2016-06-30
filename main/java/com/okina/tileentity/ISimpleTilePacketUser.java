package com.okina.tileentity;

import com.okina.network.PacketType;
import com.okina.network.SimpleTilePacket;
import com.okina.utils.Position;

public interface ISimpleTilePacketUser {

	SimpleTilePacket getPacket(PacketType type);
	void processCommand(PacketType type, Object value);
	Position getPosition();

}
