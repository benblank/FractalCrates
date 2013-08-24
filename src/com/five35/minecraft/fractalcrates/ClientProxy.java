package com.five35.minecraft.fractalcrates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (id == FractalCrates.CRATE_GUI_ID) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te != null && te instanceof CrateTileEntity) {
				return new CrateGui((CrateContainer) this.getServerGuiElement(id, player, world, x, y, z));
			}
		}

		return null;
	}
}
