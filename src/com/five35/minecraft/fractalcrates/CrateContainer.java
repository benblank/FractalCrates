package com.five35.minecraft.fractalcrates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class CrateContainer extends Container {
	final static int WIDTH = 176;
	final static int HEIGHT = 166;

	final EntityPlayer player;
	final CrateTileEntity crate;

	public CrateContainer(final EntityPlayer player, final CrateTileEntity crate) {
		this.player = player;
		this.crate = crate;

		// crates don't actually care about this, but it's good form
		crate.openChest();

		final GuiHelper helper = new GuiHelper(CrateContainer.WIDTH, CrateContainer.HEIGHT);

		for (final Slot slot : helper.getInventorySlots(crate, 1, 1)) {
			this.addSlotToContainer(slot);
		}

		for (final Slot slot : helper.getPlayerSlots(player)) {
			this.addSlotToContainer(slot);
		}
	}

	@Override
	public boolean canInteractWith(@SuppressWarnings("hiding") final EntityPlayer player) {
		return this.crate.isUseableByPlayer(player);
	}
}
