package com.okina.multiblock.construct;

import com.okina.inventory.IInternalInventoryUser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IConstructInventory extends IInternalInventoryUser {

	public static final int[] maxTransfer = { 1, 4, 16, 32, 64 };

	public boolean onClickedViaInterface(World world, EntityPlayer player, int side, float hitX, float hitY, float hitZ);

}
