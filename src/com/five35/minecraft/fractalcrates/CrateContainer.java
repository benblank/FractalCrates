package com.five35.minecraft.fractalcrates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class CrateContainer extends Container {
	private EntityPlayer player;
	private CrateTileEntity crate;

	public CrateContainer(EntityPlayer player, CrateTileEntity crate) {
		this.player = player;
		this.crate = crate;
	}

	@Override
	public boolean canInteractWith(@SuppressWarnings("hiding") EntityPlayer player) {
		return this.crate.isUseableByPlayer(player);
	}
}
