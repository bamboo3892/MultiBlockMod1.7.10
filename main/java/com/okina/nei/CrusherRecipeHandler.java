package com.okina.nei;

import java.awt.Rectangle;
import java.util.List;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.CrusherRecipeRegister.CrusherRecipe;
import com.okina.utils.InventoryHelper;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class CrusherRecipeHandler extends TemplateRecipeHandler {

	public static final String Identifier = "MBMCrusher";

	@Override
	public int recipiesPerPage() {
		return 2;
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiDummy.class;
	}

	@Override
	public void drawExtras(int recipe) {
		super.drawExtras(recipe);
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(70, 20, 26, 21), Identifier));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if(outputId.equals(Identifier)){
			for (CrusherRecipe recipe : CrusherRecipeRegister.instance.getAllRecipes()){
				arecipes.add(new RecipeCacher(recipe));
			}
		}else{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		List<CrusherRecipe> recipes = CrusherRecipeRegister.instance.findRecipeFromProduct(result);
		if(recipes == null || recipes.isEmpty()){
			return;
		}
		for (CrusherRecipe recipe : recipes){
			arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if(ingredient != null && Block.getBlockFromItem(ingredient.getItem()) instanceof ConstructCrusher){
			loadCraftingRecipes(Identifier, ingredient);
		}else{
			CrusherRecipe recipe = CrusherRecipeRegister.instance.findRecipeFromMaterial(ingredient);
			if(recipe == null) return;
			arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public String getRecipeName() {
		return "Crusher";
	}

	@Override
	public String getOverlayIdentifier() {
		return Identifier;
	}

	@Override
	public String getGuiTexture() {
		return TestCore.MODID + ":textures/gui/container/recipe1to1.png";
	}

	public class RecipeCacher extends CachedRecipe {

		private CrusherRecipe recipe;
		private PositionedStack input;
		private PositionedStack result;

		public RecipeCacher(CrusherRecipe recipe) {
			this.recipe = recipe;
			input = new PositionedStack(InventoryHelper.getOreItemForClient(recipe.material, 0), 48, 21);
			result = new PositionedStack(recipe.getProduct(), 102, 21);
		}

		@Override
		public PositionedStack getIngredient() {
			input.item = InventoryHelper.getOreItemForClient(recipe.material, (int) (cycleticks / 20d));
			return input;
		}

		@Override
		public PositionedStack getResult() {
			result.item = recipe.getProduct();
			return result;
		}

	}

}
