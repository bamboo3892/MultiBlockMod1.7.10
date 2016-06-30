package com.okina.register;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.okina.multiblock.construct.mode.ContainerModeBase;
import com.okina.multiblock.construct.processor.ContainerProcessor;

import cpw.mods.fml.common.FMLLog;

public class ContainerModeRegister {

	private static List<Class<? extends ContainerModeBase>> baseList = new ArrayList<Class<? extends ContainerModeBase>>();

	public static boolean registerProcessor(Class<? extends ContainerModeBase> base) {
		if(baseList.contains(base)){
			FMLLog.severe("That name mode is already registered : " + base.getSimpleName(), new Object[0]);
			return false;
		}else{
			baseList.add(base);
			return true;
		}
	}

	public static ContainerModeBase[] getModeInstances(ContainerProcessor container) {
		ContainerModeBase[] bases = new ContainerModeBase[baseList.size()];
		for (int i = 0; i < bases.length; i++){
			try{
				Constructor<? extends ContainerModeBase> constructor = baseList.get(i).getConstructor(ContainerProcessor.class);
				bases[i] = constructor.newInstance(container);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return bases;
	}

}
