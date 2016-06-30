package com.okina.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.okina.utils.InventoryHelper;

import net.minecraft.item.ItemStack;

public class VirtualGrowerRecipeRegister {

	public static VirtualGrowerRecipeRegister instance = new VirtualGrowerRecipeRegister();
	private static ArrayList<VirtualGrowerRecipe> recipes = new ArrayList<VirtualGrowerRecipe>();

	public void registerRecipe(Object material, int energy, int time, int requireGrade) {
		try{
			recipes.add(new VirtualGrowerRecipe(material, energy, time, requireGrade));
		}catch (Exception e){
			//			e.printStackTrace();
		}
	}

	public VirtualGrowerRecipe findRecipe(Object material) {
		if(material == null) return null;
		for (VirtualGrowerRecipe recipe : recipes){
			if(recipe.isMatch(material) && recipe.isValid()){
				return recipe;
			}
		}
		return null;
	}

	public List<VirtualGrowerRecipe> getAllRecipes() {
		List<VirtualGrowerRecipe> list = Lists.newArrayList();
		for (VirtualGrowerRecipe recipe : recipes){
			if(recipe.isValid()){
				list.add(recipe);
			}
		}
		return Collections.unmodifiableList(list);
	}

	public static class VirtualGrowerRecipe {

		//		public static int KeyIndex = 0;
		public final Object material;
		public final int energy;
		private final int time;
		public final int requireGrade;

		public VirtualGrowerRecipe(Object material, int energy, int time, int requireGrade) {
			this.material = Objects.requireNonNull(material);
			this.energy = energy;
			this.time = time;
			this.requireGrade = requireGrade;
		}

		public ItemStack getMaterial() {
			return InventoryHelper.getOreItemForServer(material);
		}

		public int getEnergyRate(int grade) {
			return (int) ((float) energy / (float) getTime(grade));
		}

		public int getTime(int grade) {
			grade = grade == 0 ? 1 : grade;
			return time / grade;
		}

		public boolean canProcess(int grade) {
			return requireGrade <= grade;
		}

		public boolean isValid() {
			return InventoryHelper.hasOreItem(material);
		}

		public boolean isMatch(Object obj) {
			return InventoryHelper.isItemMaches(material, obj);
		}

		//		public boolean isMatch(ItemStack stack) {
		//			if(material != null){
		//				return OreDictionary.itemMatches(material, stack, false);
		//			}else{
		//				int[] ids = OreDictionary.getOreIDs(stack);
		//				if(ids != null){
		//					for (int id : ids){
		//						if(id == materialId) return true;
		//					}
		//				}
		//				return false;
		//			}
		//		}

	}

}
