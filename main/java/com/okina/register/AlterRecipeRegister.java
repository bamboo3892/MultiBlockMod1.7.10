package com.okina.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;

import net.minecraft.item.ItemStack;

public class AlterRecipeRegister {

	public static AlterRecipeRegister instance = new AlterRecipeRegister();
	private static ArrayList<AlterRecipe> recipes = new ArrayList<AlterRecipe>();

	/**symmetryFlag  0 : point  1 : line 2 : non-symmetry.<br>
	 * Positions are relative coordinates<br>
	 * Object must be String(OreDict name) or ItemStack.
	 **/
	public void registerRecipe(Object coreItem, Map<Position, Object> subItemMap, int energy, int time, int requireGrade, int symmetryFlag, StackedOre product) {
		try{
			AlterRecipeRegister.recipes.add(new AlterRecipe(coreItem, subItemMap, energy, time, requireGrade, symmetryFlag, product));
		}catch (Exception e){
//			e.printStackTrace();
		}
	}

	public AlterRecipe findRecipeFromMaterial(Object material) {
		if(material == null) return null;
		for (AlterRecipe recipe : recipes){
			if(recipe.isMatch(material) && recipe.isValid()){
				return recipe;
			}
		}
		return null;
	}

	public List<AlterRecipe> findRecipeFromProduct(Object product) {
		List<AlterRecipe> list = Lists.newArrayList();
		if(product == null) return list;
		for (AlterRecipe recipe : recipes){
			if(InventoryHelper.isItemMaches(recipe.getProduct(), product) && recipe.isValid()){
				list.add(recipe);
			}
		}
		return list;
	}

	//	public  List<AlterRecipe>  findRecipeFromProduct(ItemStack product) {
	//		for (AlterRecipe recipe : recipes){
	//			if(recipe.getProduct().isItemEqual(product) && recipe.isValid()){
	//				return recipe;
	//			}
	//		}
	//		return null;
	//	}

	//	public AlterRecipe getRecipeFromKey(int key) {
	//		for (AlterRecipe recipe : recipes){
	//			if(recipe.key == key){
	//				return recipe;
	//			}
	//		}
	//		return null;
	//	}

	public List<AlterRecipe> getAllRecipes() {
		List<AlterRecipe> list = Lists.newArrayList();
		for (AlterRecipe recipe : recipes){
			if(recipe.isValid()){
				list.add(recipe);
			}
		}
		return Collections.unmodifiableList(list);
	}

	public static class AlterRecipe {

		//		private static int KeyIndex = 0;

		/**null : use ore dictionary*/
		public final Object coreItem;
		public final Map<Position, Object> subItemMap;
		public final int energy;
		private final int time;
		public final int requireGrade;
		public final int symmetryFlag;
		public final StackedOre product;
		//		public final int key;

		/**symmetryFlag  0 : point  1 : line 2 : non-symmetry.<br>
		 * Positions are relative coordinates<br>
		 * Object must be String(OreDict name) or ItemStack.
		 **/
		private AlterRecipe(Object coreItem, Map<Position, Object> subItemMap, int energy, int time, int requireGrade, int symmetryFlag, StackedOre product) {
			if(!(coreItem instanceof String || coreItem instanceof ItemStack)) new IllegalArgumentException("Material item must be String or ItemStack");
			for (AlterRecipe recipe : recipes){
				if(InventoryHelper.isItemMaches(coreItem, recipe.coreItem)) throw new IllegalArgumentException("The same core item is already registered");
			}
			for (Entry<Position, Object> entry : subItemMap.entrySet()){
				if(entry.getKey() == null || entry.getValue() == null){
					throw new IllegalArgumentException("SubItemMap must not contain null.");
				}
			}
			this.coreItem = coreItem;
			this.subItemMap = Collections.unmodifiableMap(subItemMap);
			this.energy = energy;
			this.time = time;
			this.requireGrade = requireGrade;
			this.symmetryFlag = symmetryFlag;
			this.product = product;
			//			this.key = KeyIndex++;
		}

		public ItemStack getMaterial() {
			return InventoryHelper.getOreItemForServer(coreItem);
		}

		public ItemStack getProduct() {
			return product.getItemStackForServer();
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
			if(InventoryHelper.hasOreItem(coreItem) && product.isValid()){
				for (Entry<Position, Object> entry : subItemMap.entrySet()){
					if(entry.getKey() == null || !InventoryHelper.hasOreItem(entry.getValue())){
						return false;
					}
				}
				return true;
			}
			return false;
		}

		public boolean isMatch(Object obj) {
			return InventoryHelper.isItemMaches(coreItem, obj);
		}

	}

}
