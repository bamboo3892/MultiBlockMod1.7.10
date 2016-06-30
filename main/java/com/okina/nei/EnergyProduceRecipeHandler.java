package com.okina.nei;

import java.awt.Rectangle;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructEnergyProvider;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister.EnergyProduceRecipe;
import com.okina.utils.InventoryHelper;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class EnergyProduceRecipeHandler extends TemplateRecipeHandler {

	public static final String Identifier = "MBMEnergyProvider";

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
		if(arecipes.size() > recipe){
			EnergyProduceRecipe recipe2 = ((RecipeCacher) arecipes.get(recipe)).recipe;
			if(recipe2 != null){
				GuiDraw.drawString("" + recipe2.getEnergyProduceRate(0) + " RF/tick", 100, 15, -12566464, false);
				GuiDraw.drawString("" + recipe2.produceEnergy + " RF", 100, 25, -12566464, false);
				GuiDraw.drawString("" + recipe2.getTime(0) + " ticks", 100, 35, -12566464, false);
				super.drawExtras(recipe);
			}
		}
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(70, 20, 26, 21), Identifier));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if(outputId.equals(Identifier)){
			for (EnergyProduceRecipe recipe : EnergyProdeceRecipeRegister.instance.getAllRecipes()){
				arecipes.add(new RecipeCacher(recipe));
			}
		}else{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		return;
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if(ingredient != null && Block.getBlockFromItem(ingredient.getItem()) instanceof ConstructEnergyProvider){
			loadCraftingRecipes(Identifier, ingredient);
		}else{
			EnergyProduceRecipe recipe = EnergyProdeceRecipeRegister.instance.findRecipe(ingredient);
			if(recipe == null) return;
			arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public String getRecipeName() {
		return "Energy Provider";
	}

	@Override
	public String getOverlayIdentifier() {
		return Identifier;
	}

	@Override
	public String getGuiTexture() {
		return TestCore.MODID + ":textures/gui/container/recipe1to0.png";
	}

	public class RecipeCacher extends CachedRecipe {

		private EnergyProduceRecipe recipe;
		private PositionedStack input;

		public RecipeCacher(EnergyProduceRecipe recipe) {
			this.recipe = recipe;
			input = new PositionedStack(recipe.getMaterial().copy(), 48, 21);
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public PositionedStack getIngredient() {
			input.item = InventoryHelper.getOreItemForClient(recipe.material, (int) (cycleticks / 20d));
			return input;
		}

	}

}
