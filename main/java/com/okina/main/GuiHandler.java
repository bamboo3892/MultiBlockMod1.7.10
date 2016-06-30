package com.okina.main;

import static com.okina.main.TestCore.*;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return getGuiElement(ID, player, world, x, y, z, true);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return getGuiElement(ID, player, world, x, y, z, false);
	}

	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z, boolean serverSide) {
		try{
			if(ID == ITEM_GUI_ID){
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IGuiItem){
					return ((IGuiItem) player.getCurrentEquippedItem().getItem()).getGuiElement(player, serverSide);
				}
			}else if(ID >= BLOCK_GUI_ID_0 && ID <= BLOCK_GUI_ID_5){
				if(world.getTileEntity(x, y, z) instanceof IGuiTile){
					return ((IGuiTile) world.getTileEntity(x, y, z)).getGuiElement(player, ID - BLOCK_GUI_ID_0, serverSide);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public interface IGuiItem {

		public Object getGuiElement(EntityPlayer player, boolean serverSide);

	}

	public interface IGuiTile {

		public Object getGuiElement(EntityPlayer player, int side, boolean serverSide);

	}

}
