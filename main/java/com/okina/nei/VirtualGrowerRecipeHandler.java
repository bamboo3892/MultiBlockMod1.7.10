package com.okina.nei;

import java.awt.Rectangle;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructVirtualGlower;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.okina.utils.InventoryHelper;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class VirtualGrowerRecipeHandler extends TemplateRecipeHandler {

	public static final String Identifier = "MBMVirtualGrower";
	//	public int offset = 0;

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
		//		int key = recipe + offset;
		if(arecipes.size() > recipe){
			VirtualGrowerRecipe recipe2 = ((RecipeCacher) arecipes.get(recipe)).recipe;
			if(recipe2 != null){
				String str1 = recipe2.getTime(0) + " ticks    " + recipe2.energy + " RF";
				GuiDraw.drawString(str1, 10, 45, -12566464, false);
				String str2 = "Require Grade : ";
				GuiDraw.drawString(str2, 10, 55, -12566464, false);
				String str3 = "" + ProcessorBase.GRADE_NAME[recipe2.requireGrade];
				GuiDraw.drawString(str3, 10 + Minecraft.getMinecraft().fontRenderer.getStringWidth(str2), 55, ProcessorBase.ColorCode[recipe2.requireGrade], false);
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
		//		offset = 0;
		if(outputId.equals(Identifier)){
			for (VirtualGrowerRecipe recipe : VirtualGrowerRecipeRegister.instance.getAllRecipes()){
				arecipes.add(new RecipeCacher(recipe));
			}
		}else{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(result);
		if(recipe == null){
			return;
		}
		if(recipe.getMaterial() != null) arecipes.add(new RecipeCacher(recipe));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if(ingredient != null && Block.getBlockFromItem(ingredient.getItem()) instanceof ConstructVirtualGlower){
			loadCraftingRecipes(Identifier, ingredient);
		}else{
			VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(ingredient);
			if(recipe == null) return;
			if(recipe.getMaterial() != null) arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public String getRecipeName() {
		return "Virtual Grower";
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

		private VirtualGrowerRecipe recipe;
		private PositionedStack input;
		private PositionedStack result;

		public RecipeCacher(VirtualGrowerRecipe recipe) {
			this.recipe = recipe;
			ItemStack in = recipe.getMaterial().copy();
			in.stackSize = 1;
			input = new PositionedStack(in.copy(), 48, 21);
			ItemStack item = in.copy();
			item.stackSize = 2;
			result = new PositionedStack(item, 102, 21);
		}

		@Override
		public PositionedStack getIngredient() {
			input.item = InventoryHelper.getOreItemForClient(recipe.material, (int) (cycleticks / 20d));
			return input;
		}

		@Override
		public PositionedStack getResult() {
			result.item = InventoryHelper.getOreItemForClient(recipe.material, (int) (cycleticks / 20d));
			result.item.stackSize = 2;
			return result;
		}

	}

}
