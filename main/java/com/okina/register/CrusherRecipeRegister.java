package com.okina.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.okina.utils.InventoryHelper;

import net.minecraft.item.ItemStack;

public class CrusherRecipeRegister {

	public static CrusherRecipeRegister instance = new CrusherRecipeRegister();
	private static ArrayList<CrusherRecipe> recipes = new ArrayList<CrusherRecipe>();

	public void registerRecipe(Object input, StackedOre output) {
		try{
			CrusherRecipeRegister.recipes.add(new CrusherRecipe(input, output));
		}catch (Exception e){
			//			e.printStackTrace();
		}
	}

	public CrusherRecipe findRecipeFromMaterial(Object material) {
		if(material == null) return null;
		for (CrusherRecipe recipe : recipes){
			if(recipe.isMatch(material) && recipe.isValid()){
				return recipe;
			}
		}
		return null;
	}

	public List<CrusherRecipe> findRecipeFromProduct(Object product) {
		List<CrusherRecipe> list = Lists.newArrayList();
		if(product == null) return list;
		for (CrusherRecipe recipe : recipes){
			if(InventoryHelper.isItemMaches(recipe.getProduct(), product) && recipe.isValid()){
				list.add(recipe);
			}
		}
		return list;
	}

	public List<CrusherRecipe> getAllRecipes() {
		List<CrusherRecipe> list = Lists.newArrayList();
		for (CrusherRecipe recipe : recipes){
			if(recipe.isValid()){
				list.add(recipe);
			}
		}
		return Collections.unmodifiableList(list);
	}

	public static class CrusherRecipe {

		public final Object material;
		private final StackedOre product;

		public CrusherRecipe(Object material, StackedOre product) {
			if(!(material instanceof String || material instanceof ItemStack)) new IllegalArgumentException("Material item must be String or ItemStack");
			for (CrusherRecipe recipe : recipes){
				if(InventoryHelper.isItemMaches(material, recipe.material)) throw new IllegalArgumentException("The same core item ore is already registered.");
			}
			this.material = material;
			this.product = product;
		}

		public ItemStack getProduct() {
			return product.getItemStackForServer();
		}

		public boolean isValid() {
			return InventoryHelper.hasOreItem(material) && product != null && product.isValid();
		}

		public boolean isMatch(Object obj) {
			return InventoryHelper.isItemMaches(material, obj);
			//			if(material instanceof String){
			//				int[] ids = OreDictionary.getOreIDs(stack);
			//				int id1 = OreDictionary.getOreID((String) material);
			//				if(ids != null){
			//					for (int id : ids){
			//						if(id == id1) return true;
			//					}
			//				}
			//				return false;
			//			}else if(material instanceof ItemStack){
			//				return OreDictionary.itemMatches((ItemStack) material, stack, false);
			//			}
			//			return false;
		}

	}

}
