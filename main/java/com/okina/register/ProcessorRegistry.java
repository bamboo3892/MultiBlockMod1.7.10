package com.okina.register;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.okina.multiblock.construct.IProcessorContainer;
import com.okina.multiblock.construct.block.BlockConstructBase;
import com.okina.multiblock.construct.processor.ProcessorBase;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;

public class ProcessorRegistry {

	public static HashMap<String, Class<? extends ProcessorBase>> partsMap = new HashMap<String, Class<? extends ProcessorBase>>();
	public static HashMap<String, BlockConstructBase[]> blockMap = new HashMap<String, BlockConstructBase[]>();

	public static boolean registerProcessor(Block[] block, ProcessorBase base) {
		String name = base.getNameForNBT();
		if(partsMap.containsKey(name)){
			FMLLog.severe("That name part is already registered : " + name, new Object[0]);
			return false;
		}else{
			partsMap.put(name, base.getClass());
			blockMap.put(name, (BlockConstructBase[]) block);
			return true;
		}
	}

	public static ProcessorBase getProcessorFromName(String name, IProcessorContainer pc, boolean isRemote, boolean isTile, int x, int y, int z, int grade) {
		if(partsMap.containsKey(name)){
			try{
				Constructor<? extends ProcessorBase> constructor = partsMap.get(name).getConstructor(IProcessorContainer.class, boolean.class, boolean.class, int.class, int.class, int.class, int.class);
				return constructor.newInstance(pc, isRemote, isTile, x, y, z, grade);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		new Throwable("not registered the name : " + name).printStackTrace();
		return null;
	}

	//	/**
	//	 * @param tag
	//	 * @param solid (use to encode id to coordinate)
	//	 * @return
	//	 */
	//	public static ConstructPartBase getNewPartFromNBT(MultiBlockCoreTileEntity tile, NBTTagCompound tag, RectangularSolid solid) {
	//		String name = tag.getString("name");
	//		if(partsMap.containsKey(name)){
	//			try{
	//				ConstructPartBase part = partsMap.get(name).newInstance();
	//				part.setCoreTile(tile);
	//				part.readFromNBT(tag, solid);
	//				return part;
	//			}catch (InstantiationException e){
	//				e.printStackTrace();
	//			}catch (IllegalAccessException e){
	//				e.printStackTrace();
	//			}
	//		}else{
	//			FMLLog.severe("That name part is not registered : " + name, new Object[0]);
	//		}
	//		return null;
	//	}

	public static BlockConstructBase getBlockFromNBTName(String name, int grade) {
		if(blockMap.containsKey(name)){
			try{
				return blockMap.get(name)[grade];
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

}
