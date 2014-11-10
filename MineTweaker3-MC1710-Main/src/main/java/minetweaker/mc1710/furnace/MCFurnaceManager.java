/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mc1710.furnace;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import minetweaker.api.MineTweakerAPI;
import minetweaker.api.action.UndoableAction;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStacks;
import minetweaker.api.recipes.IFurnaceManager;
import minetweaker.mc1710.item.MCItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 *
 * @author Stan
 */
public class MCFurnaceManager implements IFurnaceManager {
	public MCFurnaceManager() {
		
	}
	
	@Override
	public void remove(IIngredient output, IIngredient input) {
		if (output == null) throw new IllegalArgumentException("output cannot be null");
		
		Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.smelting().getSmeltingList();
		
		List<ItemStack> toRemove = new ArrayList<ItemStack>();
		List<ItemStack> toRemoveValues = new ArrayList<ItemStack>();
		for (Map.Entry<ItemStack, ItemStack> entry : smeltingList.entrySet()) {
			if (output.matches(new MCItemStack(entry.getValue()))
					&& (input == null || input.matches(new MCItemStack(entry.getKey())))) {
				toRemove.add(entry.getKey());
				toRemoveValues.add(entry.getValue());
			}
		}
		
		if (toRemove.isEmpty()) {
			MineTweakerAPI.logWarning("No furnace recipes for " + output.toString());
		} else {
			MineTweakerAPI.apply(new RemoveAction(toRemove, toRemoveValues));
		}
	}

	@Override
	public void addRecipe(IItemStack output, IIngredient input, double xp) {
		List<IItemStack> items = input.getItems();
		if (items == null) {
			MineTweakerAPI.logError("Cannot turn " + input.toString() + " into a furnace recipe");
		}
		
		ItemStack[] items2 = getItemStacks(items);
		ItemStack output2 = getItemStack(output);
		MineTweakerAPI.apply(new AddRecipeAction(input, items2, output2, xp));
	}

	@Override
	public void setFuel(IIngredient item, int fuel) {
		MineTweakerAPI.apply(new SetFuelAction(new SetFuelPattern(item, fuel)));
	}

	@Override
	public int getFuel(IItemStack item) {
		return GameRegistry.getFuelValue(getItemStack(item));
	}
	
	// ######################
	// ### Action classes ###
	// ######################
	
	private static class RemoveAction extends UndoableAction {
		private final List<ItemStack> items;
		private final List<ItemStack> values;
		
		public RemoveAction(List<ItemStack> items, List<ItemStack> values) {
			this.items = items;
			this.values = values;
		}
		
		@Override
		public void apply() {
			for (ItemStack item : items) {
				FurnaceRecipes.smelting().getSmeltingList().remove(item);
			}
		}

		@Override
		public void undo() {
			for (int i = 0; i < items.size(); i++) {
				FurnaceRecipes.smelting().getSmeltingList().put(items.get(i), values.get(i));
			}
		}

		@Override
		public String describe() {
			return "Removing " + items.size() + " furnace recipes";
		}

		@Override
		public String describeUndo() {
			return "Restoring " + items.size() + " furnace recipes";
		}
	}
	
	private static class AddRecipeAction extends UndoableAction {
		private final IIngredient ingredient;
		private final ItemStack[] input;
		private final ItemStack output;
		private final double xp;
		
		public AddRecipeAction(IIngredient ingredient, ItemStack[] input, ItemStack output, double xp) {
			this.ingredient = ingredient;
			this.input = input;
			this.output = output;
			this.xp = xp;
		}

		@Override
		public void apply() {
			for (ItemStack inputStack : input) {
				FurnaceRecipes.smelting().func_151394_a(inputStack, output, (float) xp);
			}
		}

		@Override
		public void undo() {
			for (ItemStack inputStack : input) {
				FurnaceRecipes.smelting().getSmeltingList().remove(inputStack);
			}
		}

		@Override
		public String describe() {
			return "Adding furnace recipe for " + ingredient;
		}

		@Override
		public String describeUndo() {
			return "Removing furnace recipe for " + ingredient;
		}
	}
	
	private static class SetFuelAction extends UndoableAction {
		private final SetFuelPattern pattern;
		
		public SetFuelAction(SetFuelPattern pattern) {
			this.pattern = pattern;
		}
		
		@Override
		public void apply() {
			FuelTweaker.INSTANCE.addFuelPattern(pattern);
		}

		@Override
		public void undo() {
			FuelTweaker.INSTANCE.removeFuelPattern(pattern);
		}

		@Override
		public String describe() {
			return "Setting fuel for " + pattern.getPattern();
		}

		@Override
		public String describeUndo() {
			return "Removing fuel for " + pattern.getPattern();
		}
	}
}
