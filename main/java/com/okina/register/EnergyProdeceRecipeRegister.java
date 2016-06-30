package com.okina.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.okina.utils.InventoryHelper;

import net.minecraft.item.ItemStack;

public class EnergyProdeceRecipeRegister {

	public static EnergyProdeceRecipeRegister instance = new EnergyProdeceRecipeRegister();
	private static ArrayList<EnergyProduceRecipe> recipes = new ArrayList<EnergyProduceRecipe>();

	public void registerRecipe(Object input, int prodeceEnergy, int time) {
		try{
			EnergyProdeceRecipeRegister.recipes.add(new EnergyProduceRecipe(input, prodeceEnergy, time));
		}catch (Exception e){
			//			e.printStackTrace();
		}
	}

	public EnergyProduceRecipe findRecipe(Object material) {
		if(material == null) return null;
		for (EnergyProduceRecipe recipe : recipes){
			if(recipe.isMatch(material) && recipe.isValid()){
				return recipe;
			}
		}
		return null;
	}

	public List<EnergyProduceRecipe> getAllRecipes() {
		List<EnergyProduceRecipe> list = Lists.newArrayList();
		for (EnergyProduceRecipe recipe : recipes){
			if(recipe.isValid()){
				list.add(recipe);
			}
		}
		return Collections.unmodifiableList(list);
	}

	public static class EnergyProduceRecipe {

		public final Object material;
		public final int produceEnergy;
		private final int time;

		public EnergyProduceRecipe(Object material, int prodeceEnergy, int time) {
			this.material = Objects.requireNonNull(material);
			produceEnergy = prodeceEnergy;
			this.time = time;
		}

		public ItemStack getMaterial() {
			return InventoryHelper.getOreItemForServer(material);
		}

		public int getEnergyProduceRate(int grade) {
			return (int) (produceEnergy / (double) getTime(grade));
		}

		public int getTime(int grade) {
			grade = grade == 0 ? 1 : grade;
			return time / grade;
		}

		public boolean isValid() {
			return InventoryHelper.hasOreItem(material);
		}

		public boolean isMatch(Object obj) {
			return InventoryHelper.isItemMaches(material, obj);
		}

		@Override
		public String toString() {
			return material + " : " + produceEnergy + "RF";
		}

	}

}
