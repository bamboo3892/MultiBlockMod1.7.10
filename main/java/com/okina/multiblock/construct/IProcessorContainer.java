package com.okina.multiblock.construct;

import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.network.PacketType;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface IProcessorContainer {

	ProcessorBase getContainProcessor();

	ProcessorBase getProcessor(int x, int y, int z);

	/**send packet to client.<br>
	 * make sure to process the packet at #processPacket method on clinet.
	 */
	void sendPacket(PacketType type, Object value);

	/**server only*/
	void markForUpdate(PacketType type);

	/**client only*/
	void markForRenderUpdate();

	World world();

	/**if processor is in tile, you dontnhave to use this.<br>
	 * return the same reference object*/
	Vec3 toReadWorld(Vec3 coord);

	/**if processor is in tile, you dontnhave to use this.*/
	int toRealWorldSide(int side);

	/**if processor is in tile, you dontnhave to use this.*/
	int toInsideWorldSide(int side);

}
