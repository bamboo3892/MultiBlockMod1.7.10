package com.okina.item;

import com.okina.client.gui.InformationGui;
import com.okina.main.GuiHandler.IGuiItem;
import com.okina.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class TestItem extends Item implements IGuiItem {

	/*
	 * damege
	 * 0 : world interact mode
	 * 1 : block mode
	 */
	public TestItem() {
		setUnlocalizedName("mbm_testItem");
		setCreativeTab(TestCore.testCreativeTab);
		setTextureName("stick");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {

		if(!world.isRemote) return itemStack;

		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);
		if(movingobjectposition == null || movingobjectposition.sideHit == -1){
			if(world.isRemote) entityPlayer.addChatComponentMessage(new ChatComponentText("Found nothing."));
			return itemStack;
		}
		int x = entityPlayer.serverPosX;
		int y = entityPlayer.serverPosY;
		int z = entityPlayer.serverPosZ;
		if(world.isRemote) entityPlayer.openGui(TestCore.instance, TestCore.ITEM_GUI_ID, world, x, y, z);
		return itemStack;
	}

	public String[] getMessage(World world, EntityPlayer entityPlayer) {
		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityPlayer, true);
		if(movingobjectposition.typeOfHit == MovingObjectType.BLOCK){
			int x = movingobjectposition.blockX;
			int y = movingobjectposition.blockY;
			int z = movingobjectposition.blockZ;
			Block b = world.getBlock(x, y, z);
			TileEntity t = world.getTileEntity(x, y, z);
			String[] message = new String[10];
			message[0] = "Positon : " + x + ", " + y + ", " + z;
			message[1] = "Block Name : " + b.getLocalizedName() + " (" + world.getBlockMetadata(x, y, z) + ")";
			message[2] = (t == null ? "Doesn't " : "") + "Have TileEntity" + (t == null ? "" : "(" + t.getClass().getSimpleName() + ")");
			message[3] = "Hardness : " + b.getBlockHardness(world, x, y, z);
			message[4] = "Resistance : " + b.getExplosionResistance(null);
			message[5] = "Slipperiness : " + b.slipperiness;
			message[6] = "Opaque : " + b.isOpaqueCube();
			message[7] = "Light Opacity : " + b.getLightOpacity();
			message[8] = "Light Value : " + b.getLightValue();
			return message;
		}else if(movingobjectposition.typeOfHit == MovingObjectType.ENTITY){
			Entity e = movingobjectposition.entityHit;
			double x = e.posX;
			double y = e.posY;
			double z = e.posZ;
			String[] message = new String[10];
			message[0] = "Position : " + x + ", " + y + ", " + z;
			message[1] = "Entity Name : " + e.getClass().toString();
			message[2] = "";
			message[3] = "Fall Distance : " + e.fallDistance;
			message[4] = "Fire Resistance : " + e.fireResistance;
			message[5] = "Hurt Interval Time : " + e.hurtResistantTime;
			if(e instanceof EntityLivingBase){
				EntityLivingBase e2 = (EntityLivingBase) e;
				message[6] = "Health : " + e2.getHealth();
				message[7] = "Max Health : " + e2.getMaxHealth();
			}
			return message;
		}else{
			entityPlayer.addChatComponentMessage(new ChatComponentText("Unknown situation."));
		}
		return null;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		return serverSide ? null : new InformationGui(player);
	}

}




