package com.okina.nei;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructAlter;
import com.okina.multiblock.construct.processor.ProcessorBase;
import com.okina.register.AlterRecipeRegister;
import com.okina.register.AlterRecipeRegister.AlterRecipe;
import com.okina.utils.InventoryHelper;
import com.okina.utils.Position;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class AlterRecipeHandler extends TemplateRecipeHandler {

	private static final Comparator<PositionedStack> COMPARATOR = new Comparator<PositionedStack>() {
		@Override
		public int compare(PositionedStack o1, PositionedStack o2) {
			double z1 = 0;
			double z2 = 0;
			if(o1 instanceof PositionedStack2){
				z1 = ((PositionedStack2) o1).z;
			}
			if(o2 instanceof PositionedStack2){
				z2 = ((PositionedStack2) o2).z;
			}
			return (int) (z1 - z2);
		}
	};

	public static final String Identifier = "MBMAlter";

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiDummy.class;
	}

	@Override
	public void drawExtras(int recipe) {
		if(arecipes.size() > recipe){
			AlterRecipe recipe2 = ((RecipeCacher) arecipes.get(recipe)).recipe;
			if(recipe2 != null){

				//				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(TestCore.MODID + ":textures/blocks/alter_effect2.png"));
				//				Tessellator tessellator = Tessellator.instance;
				//				GL11.glPushMatrix();
				//				GL11.glDisable(GL11.GL_CULL_FACE);
				//				GL11.glDepthMask(true);
				//				GL11.glEnable(GL11.GL_TEXTURE_2D);
				//				GL11.glDisable(GL11.GL_LIGHTING);
				//				tessellator.startDrawingQuads();
				//				tessellator.setColorRGBA_F(1f, 1f, 1f, 1f);
				//				int x = 67;
				//				int y = 71;
				//				int w = 30;
				//				int h = 30;
				//				tessellator.addVertexWithUV(x, y, 0, 0, 0);
				//				tessellator.addVertexWithUV(x, y + h, 0, 1, 0);
				//				tessellator.addVertexWithUV(x + w, y + h, 0, 1, 1);
				//				tessellator.addVertexWithUV(x + w, y, 0, 0, 1);
				//				tessellator.draw();
				//				GL11.glPopMatrix();

				int lineIndex = 0;
				String str1 = recipe2.getTime(0) + " ticks    " + recipe2.energy + " RF";
				GuiDraw.drawString(str1, 5, 22 + lineIndex++ * 10, -12566464, false);
				String str2 = "Require Grade : ";
				GuiDraw.drawString(str2, 5, 22 + lineIndex * 10, -12566464, false);
				String str3 = "" + ProcessorBase.GRADE_NAME[recipe2.requireGrade];
				GuiDraw.drawString(str3, 5 + Minecraft.getMinecraft().fontRenderer.getStringWidth(str2), 22 + lineIndex++ * 10, ProcessorBase.ColorCode[recipe2.requireGrade], false);
				GuiDraw.drawString(": 1m", 124, 48, -12566464, false);
				//				GuiDraw.changeTexture(new ResourceLocation("textures/particle/particles.png"));
				//				GuiDraw.drawTexturedModalRect(10, 10, 64, 32, 64, 64);
				//			for (Entry<Position, Object> entry : arecipe.subItemMap.entrySet()){
				//				if(entry.getValue() instanceof String){
				//					String str = entry.getValue() + " at " + entry.getKey();
				//					GuiDraw.drawString(str, 5, 45 + lineIndex++ * 10, -12566464, false);
				//				}else{
				//					String str = ((ItemStack) entry.getValue()).getDisplayName() + " at " + entry.getKey();
				//					GuiDraw.drawString(str, 5, 45 + lineIndex++ * 10, -12566464, false);
				//				}
				//			}
				//			String str1 = arecipe.getTime(0) + " ticks    " + arecipe.energy + " RF";
				//			GuiDraw.drawString(str1, 5, 45 + lineIndex++ * 10, -12566464, false);
				//			String str2 = "Require Grade : ";
				//			GuiDraw.drawString(str2, 5, 45 + lineIndex * 10, -12566464, false);
				//			String str3 = "" + ProcessorBase.GRADE_NAME[arecipe.requireGrade];
				//			GuiDraw.drawString(str3, 5 + Minecraft.getMinecraft().fontRenderer.getStringWidth(str2), 45 + lineIndex++ * 10, ProcessorBase.ColorCode[arecipe.requireGrade], false);
			}
		}
		super.drawExtras(recipe);
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(70, 2, 26, 21), Identifier));
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if(outputId.equals(Identifier)){
			for (AlterRecipe recipe : AlterRecipeRegister.instance.getAllRecipes()){
				arecipes.add(new RecipeCacher(recipe));
			}
		}else{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		List<AlterRecipe> recipes = AlterRecipeRegister.instance.findRecipeFromProduct(result);
		if(recipes == null || recipes.isEmpty()){
			return;
		}
		for (AlterRecipe recipe : recipes){
			arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		if(ingredient != null && Block.getBlockFromItem(ingredient.getItem()) instanceof ConstructAlter){
			loadCraftingRecipes(Identifier, ingredient);
		}else{
			AlterRecipe recipe = AlterRecipeRegister.instance.findRecipeFromMaterial(ingredient);
			if(recipe == null) return;
			arecipes.add(new RecipeCacher(recipe));
		}
	}

	@Override
	public String getRecipeName() {
		return "MBM Alter";
	}

	@Override
	public String getOverlayIdentifier() {
		return Identifier;
	}

	@Override
	public String getGuiTexture() {
		return TestCore.MODID + ":textures/gui/container/alter.png";
	}

	public class RecipeCacher extends CachedRecipe {

		private AlterRecipe recipe;
		private PositionedStack input;
		private PositionedStack result;

		public RecipeCacher(AlterRecipe recipe) {
			this.recipe = Objects.requireNonNull(recipe);
			this.input = new PositionedStack2(recipe.getMaterial(), 48, 2, 0);
			this.result = new PositionedStack2(recipe.getProduct(), 102, 2, 0);
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public PositionedStack getIngredient() {
			return input;
		}

		@Override
		public List<PositionedStack> getIngredients() {
			List<PositionedStack> ingredients = Lists.newArrayList();
			ItemStack item1 = InventoryHelper.getOreItemForClient(recipe.coreItem, (int) (cycleticks / 20d));
			ingredients.add(new PositionedStack2(item1, 48, 2, 0));
			ingredients.add(new PositionedStack2(item1, 73, 78, 0));
			for (Entry<Position, Object> entry : recipe.subItemMap.entrySet()){
				Vec3 vec = Vec3.createVectorHelper(entry.getKey().x * 17d, -entry.getKey().y * 17d, entry.getKey().z * 17d);
				float f = (float) ((cycleticks % 100) / 50.0f * Math.PI);
				vec.rotateAroundY(f);
				vec.rotateAroundX((float) (Math.PI / 9.0d));
				ItemStack itemStack = InventoryHelper.getOreItemForClient(entry.getValue(), (int) (cycleticks / 20d));
				if(itemStack != null){
					ingredients.add(new PositionedStack2(itemStack, (int) vec.xCoord + 73, (int) vec.yCoord + 78, vec.zCoord));
				}
			}
			ingredients.sort(COMPARATOR);
			return ingredients;
		}

	}

}




