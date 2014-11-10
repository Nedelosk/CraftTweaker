/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mods.mfr.machines;

import cofh.lib.util.WeightedRandomItemStack;
import java.util.ArrayList;
import java.util.List;
import minetweaker.annotations.ModOnly;
import minetweaker.api.MineTweakerAPI;
import minetweaker.api.action.IUndoableAction;
import minetweaker.api.action.UndoableAction;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.WeightedItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.util.WeightedRandom;
import org.openzen.zencode.annotations.ZenClass;
import org.openzen.zencode.annotations.ZenMethod;
import powercrystals.minefactoryreloaded.MFRRegistry;

/**
 *
 * @author Stan
 */
@ZenClass("mods.mfr.SludgeBoiler")
@ModOnly("MineFactoryReloaded")
public class SludgeBoiler {
	@ZenMethod
	public static void addDrop(WeightedItemStack item) {
		MineTweakerAPI.apply(new AddDropAction(item));
	}
	
	@ZenMethod
	public static void removeDrop(IIngredient item) {
		if (MFRRegistry.getSludgeDrops() == null) {
			MineTweakerAPI.logWarning("Cannot remove drops from the sludge boiler");
		} else {
			MineTweakerAPI.apply(new RemoveDropAction(item));
		}
	}
	
	// ######################
	// ### Action classes ###
	// ######################
	
	private static class AddDropAction extends UndoableAction {
		private final WeightedRandomItemStack item;
		
		public AddDropAction(WeightedItemStack item) {
			this.item = new WeightedRandomItemStack(MineTweakerMC.getItemStack(item.getStack()), (int) item.getChance());
		}

		@Override
		public void apply() {
			MFRRegistry.getSludgeDrops().add(item);
		}

		@Override
		public void undo() {
			MFRRegistry.getSludgeDrops().remove(item);
		}

		@Override
		public String describe() {
			return "Adding sludge boiler drop " + item.getStack().getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Removing sludge boiler drop " + item.getStack().getDisplayName();
		}
	}
	
	private static class RemoveDropAction extends UndoableAction {
		private final IIngredient item;
		private final List<WeightedRandomItemStack> toRemove;
		
		private RemoveDropAction(IIngredient item) {
			this.item = item;
			
			toRemove = new ArrayList<WeightedRandomItemStack>();
			for (WeightedRandom.Item iStack : MFRRegistry.getSludgeDrops()) {
				if (iStack instanceof WeightedRandomItemStack) {
					WeightedRandomItemStack itemStack = (WeightedRandomItemStack) iStack;
					if (item.matches(MineTweakerMC.getIItemStack(itemStack.getStack()))) {
						toRemove.add(itemStack);
					}
				}
			}
		}

		@Override
		public void apply() {
			for (WeightedRandomItemStack itemStack : toRemove) {
				MFRRegistry.getSludgeDrops().remove(itemStack);
			}
		}

		@Override
		public void undo() {
			for (WeightedRandomItemStack item : toRemove) {
				MFRRegistry.getSludgeDrops().add(item);
			}
		}

		@Override
		public String describe() {
			return "Removing " + toRemove.size() + " sludge boiler drops for " + item;
		}

		@Override
		public String describeUndo() {
			return "Restoring " + toRemove.size() + " sludge boiler drops for " + item;
		}
	}
}
