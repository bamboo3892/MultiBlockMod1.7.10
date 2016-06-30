package com.okina.item;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.okina.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemNBTPrinter extends Item {

	public ItemNBTPrinter() {
		setTextureName("stick");
		setUnlocalizedName("mbm_nbt_printer");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) != null){
			TileEntity tile = world.getTileEntity(x, y, z);
			final NBTTagCompound tag = new NBTTagCompound();
			tile.writeToNBT(tag);
			final String tileName = tile.getClass().getSimpleName();
			final boolean isRemote = world.isRemote;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Date date = Calendar.getInstance().getTime();
					File file = new File(TestCore.ConfigFile.getAbsolutePath() + File.separator + Calendar.getInstance().get(Calendar.YEAR) + "_" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "_" + Calendar.getInstance().get(Calendar.DATE) + "_" + Calendar.getInstance().get(Calendar.HOUR) + "_" + Calendar.getInstance().get(Calendar.MINUTE) + "_" + (isRemote ? "CLIENT" : "SERVER") + ".txt");
					try{
						TestCore.ConfigFile.mkdirs();
						//file.createNewFile();
						FileWriter writer = null;
						try{
							writer = new FileWriter(file);
							writer.write("Side = " + (isRemote ? "CLIENT" : "SERVER") + "\n");
							writer.write("Tile Name = " + tileName + "\n");
							List<String> list = getNBTMessages(tag, 0);
							for (String msg : list){
								writer.write(msg + "\n");
							}
						}finally{
							if(writer != null) writer.close();
						}
					}catch (Exception e){
						System.err.println("Catch an exception : " + (isRemote ? "CLIENT" : "SERVER"));
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
		return true;
	}

	private static List<String> getNBTMessages(NBTBase base, int hierarchy) {
		List<String> list = Lists.newLinkedList();
		StringBuilder buider = new StringBuilder();
		for (int i = 0; i < hierarchy; i++){
			buider.append("\t");
		}
		String tabs = buider.toString();
		if(base instanceof NBTTagCompound){
			Iterator iterator = ((NBTTagCompound) base).func_150296_c().iterator();
			while (iterator.hasNext()){
				String s = (String) iterator.next();
				NBTBase nbtbase = ((NBTTagCompound) base).getTag(s);
				if(nbtbase instanceof NBTTagCompound || nbtbase instanceof NBTTagList){
					list.add(tabs + s + " {");
					list.addAll(getNBTMessages(nbtbase, hierarchy + 1));
					list.add(tabs + "}");
				}else{
					list.add(tabs + s + " = " + nbtbase.toString());
				}
			}
		}else if(base instanceof NBTTagList){
			list.add(tabs + "NBTTagList {");
			for (int i = 0; i < ((NBTTagList) base).tagCount(); i++){
				list.add(tabs + "List Content No." + i + " :");
				NBTBase content = ((NBTTagList) base).getCompoundTagAt(i);
				list.addAll(getNBTMessages(content, hierarchy + 1));
			}
			list.add(tabs + "}");
		}else{
			list.add(tabs + base.toString());
		}
		return list;
	}

}
