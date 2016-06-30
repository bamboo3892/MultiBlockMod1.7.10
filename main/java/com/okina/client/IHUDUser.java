package com.okina.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

/**implements this interface on TileEntity, Block or Item*/
public interface IHUDUser {

	/**Max renderTicks is 1 hour*/
	public void renderHUD(Minecraft sr, double renderTicks, MovingObjectPosition mop);

	/**
	 * @param object Past rendered object(tile or block or item)
	 * @param past
	 * @param current
	 * @return whether continue to count renderTicks that is passed to {@link IHUDUser#renderHUD} method
	 */
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current);

}
