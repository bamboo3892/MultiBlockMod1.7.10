package com.okina.network;

import net.minecraft.nbt.NBTTagCompound;

public enum PacketType {

	UNKNOWN(0, null),

	SLIDER_INPUT(1, Integer.class),

	EFFECT(2, Integer.class),

	EFFECT2(3, NBTTagCompound.class),

	ENERGY(4, Integer.class),

	FILTER_NBT_FROM_GUI(5, NBTTagCompound.class),

	FLAG_IO(6, String.class),

	NBT_CONTENT(7, NBTTagCompound.class),

	NBT_CONNECTION(8, NBTTagCompound.class),

	NBT_CONTAINER_MODE(9, NBTTagCompound.class),

	NBT_MULTIBLOCK(10, NBTTagCompound.class),

	ALL_FILTER_UPDATE(11, NBTTagCompound.class),

	RENDER(12, Integer.class),

	OTHER(13, String.class),

	OTHER2(14, Integer.class);

	public final int id;
	public final Class<?> valueClass;

	private PacketType(int id, Class<?> valueClass) {
		this.id = id;
		this.valueClass = valueClass;
	}

	public static PacketType getFromId(int id) {
		for (PacketType type : PacketType.values()){
			if(type.id == id) return type;
		}
		return UNKNOWN;
	}

}
/*for 1.8
@Override
public IMessage onMessage(PacketClockPulser message, MessageContext ctx) {
    IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
    mainThread.addScheduledTask(new Runnable() {
        @Override
        public void run() {
            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        }
    });
    return null; // no response in this case
}
*/