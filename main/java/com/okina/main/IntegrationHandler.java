package com.okina.main;

import java.util.HashMap;
import java.util.Map;

import com.okina.register.AlterRecipeRegister;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.StackedOre;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IntegrationHandler {

	public static void registerOre() {
		OreDictionary.registerOre("coal", new ItemStack(Items.coal, 0));
		OreDictionary.registerOre("charcoal", new ItemStack(Items.coal, 1));
		OreDictionary.registerOre("charcoal", new ItemStack(Items.coal, 1));
		OreDictionary.registerOre("blockQuartzPiller", new ItemStack(Blocks.quartz_block, 1, 2));
		OreDictionary.registerOre("blockChiseledQuartz", new ItemStack(Blocks.quartz_block, 1, 1));
		ItemStack mapleDiamond = InventoryHelper.getOreItemForServer("mapleDiamond");
		if(mapleDiamond != null) OreDictionary.registerOre("gemMapleDiamond", mapleDiamond);
	}

	public static void registerRecipe() {
		//crusher and altar
		CrusherRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("oreIron"), new StackedOre(new ItemStack(TestCore.doubleDustIron, 1), 1));
		CrusherRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("oreGold"), new StackedOre(new ItemStack(TestCore.doubleDustGold, 1), 1));
		String[] names = OreDictionary.getOreNames();
		for (String name : names){
			registerOre(name);
		}
		//		if(Loader.isModLoaded("ThermalExpansion")){
		//			RecipePulverizer[] pulRecipes = PulverizerManager.getRecipeList();
		//			if(pulRecipes != null){
		//				for (RecipePulverizer pulRecipe : pulRecipes){
		//					if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) System.out.println(pulRecipe.getInput().getDisplayName() + pulRecipe.getInput().getItemDamage());
		//					CrusherRecipeRegister.instance.registerRecipe(pulRecipe.getInput(), pulRecipe.getPrimaryOutput());
		//				}
		//			}
		//		}
	}

	public static void registerOre(String name) {
		if(name.toLowerCase().startsWith("ore")){
			String dustName = name.replace("ore", "dust");
			String ingotName = name.replace("ore", "ingot");
			String denseoreName = name.replace("ore", "denseore");
			String gemName = name.replace("ore", "gem");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(dustName)){
					CrusherRecipeRegister.instance.registerRecipe(name, new StackedOre(dustName, 2));
				}else if(str.equals(ingotName)){
					Map<Position, Object> map = new HashMap();
					map.put(new Position(1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, 1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, 1, -1), new ItemStack(Blocks.cactus, 1));
					AlterRecipeRegister.instance.registerRecipe(name, map, 10000, 4800, 2, 0, new StackedOre(ingotName, 3));
				}else if(str.equals(denseoreName)){
					CrusherRecipeRegister.instance.registerRecipe(denseoreName, new StackedOre(name, 4));
				}else if(str.equals(gemName)){
					Map<Position, Object> map6 = new HashMap();
					map6.put(new Position(1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, 1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, 1, -1), new ItemStack(Blocks.cactus, 1));
					AlterRecipeRegister.instance.registerRecipe(name, map6, 10000, 4800, 2, 0, new StackedOre(gemName, 3));
				}
			}
		}else if(name.toLowerCase().startsWith("dust")){
			String oreName = name.replace("dust", "ore");
			String ingotName = name.replace("dust", "ingot");
			String clusterName = name.replace("dust", "cluster");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(oreName)){
					CrusherRecipeRegister.instance.registerRecipe(oreName, new StackedOre(name, 2));
				}else if(str.equals(ingotName)){
					CrusherRecipeRegister.instance.registerRecipe(ingotName, new StackedOre(name, 1));
				}else if(str.equals(clusterName)){
					CrusherRecipeRegister.instance.registerRecipe(clusterName, new StackedOre(name, 2));
				}
			}
		}else if(name.toLowerCase().startsWith("ingot")){
			String dustName = name.replace("ingot", "dust");
			String oreName = name.replace("ingot", "ore");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(dustName)){
					CrusherRecipeRegister.instance.registerRecipe(name, new StackedOre(dustName, 1));
				}else if(str.equals(oreName)){
					Map<Position, Object> map = new HashMap();
					map.put(new Position(1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(1, 1, -1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map.put(new Position(-1, 1, -1), new ItemStack(Blocks.cactus, 1));
					AlterRecipeRegister.instance.registerRecipe(oreName, map, 10000, 4800, 2, 0, new StackedOre(name, 3));
				}
			}
		}else if(name.toLowerCase().startsWith("cluster")){
			String dustName = name.replace("cluster", "dust");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(dustName)){
					CrusherRecipeRegister.instance.registerRecipe(name, new StackedOre(dustName, 2));
				}
			}
		}else if(name.toLowerCase().startsWith("denseore")){
			String oreName = name.replace("denseore", "ore");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(oreName)){
					CrusherRecipeRegister.instance.registerRecipe(name, new StackedOre(oreName, 4));
				}
			}
		}else if(name.toLowerCase().startsWith("gem")){
			String oreName = name.replace("gem", "ore");
			for (String str : OreDictionary.getOreNames()){
				if(str.equals(oreName)){
					Map<Position, Object> map6 = new HashMap();
					map6.put(new Position(1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, -1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, -1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(1, 1, -1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, 1, 1), new ItemStack(Blocks.cactus, 1));
					map6.put(new Position(-1, 1, -1), new ItemStack(Blocks.cactus, 1));
					AlterRecipeRegister.instance.registerRecipe(oreName, map6, 10000, 4800, 2, 0, new StackedOre(name, 3));
				}
			}
		}
	}

}
