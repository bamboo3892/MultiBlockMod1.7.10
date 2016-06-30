package com.okina.item.itemBlock;

import java.util.List;

import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;
import com.okina.utils.Position;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemMultiBlock extends ItemBlock implements IToolTipUser {

	public ItemMultiBlock(Block block) {
		super(block);
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	//	@Override
	//	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
	//		if(!world.setBlock(x, y, z, field_150939_a, metadata, 3)){
	//			return false;
	//		}
	//		if(world.getBlock(x, y, z) == field_150939_a){
	//			NBTTagCompound tag = stack.getTagCompound();
	//			if(tag == null){
	//				FMLLog.severe("Tag deleted itemstack", new Object[0]);
	//				tag = new NBTTagCompound();
	//			}
	//			MultiBlockCoreTileEntity tile = MultiBlockCoreTileEntity.createTileFromNBT(tag);
	//			world.setTileEntity(x, y, z, tile);
	//			field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
	//			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
	//		}
	//		return true;
	//	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.rare;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		if(shiftPressed){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				int xSize = tag.getInteger("xSize");
				int ySize = tag.getInteger("ySize");
				int zSize = tag.getInteger("zSize");
				toolTip.add(xSize + " x " + ySize + " x " + zSize);
				RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
				NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
				if(blockTagList != null){
					for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
						NBTTagCompound blockTagCompound = blockTagList.getCompoundTagAt(tagCounter);
						String name = blockTagCompound.getString("name");
						int x = blockTagCompound.getInteger("x");
						int y = blockTagCompound.getInteger("y");
						int z = blockTagCompound.getInteger("z");
						Position p = new Position(x, y, z);
						toolTip.add(name + ", " + p);
					}
				}
				NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
				if(interfaceTagList != null){
					for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
						NBTTagCompound interfaceTag = interfaceTagList.getCompoundTagAt(tagCounter);
						int side = interfaceTag.getInteger("side");
						int x = interfaceTag.getInteger("x");
						int y = interfaceTag.getInteger("y");
						int z = interfaceTag.getInteger("z");
						toolTip.add("Interface : " + ForgeDirection.getOrientation(side) + " To " + x + ", " + y + ", " + z);
					}
				}
			}
		}
	}
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
