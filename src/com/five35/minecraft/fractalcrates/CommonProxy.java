package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		return null;
	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (id == FractalCrates.CRATE_GUI_ID) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te != null && te instanceof CrateTileEntity) {
				return new CrateContainer(player, (CrateTileEntity) te);
			}
		}

		return null;
	}
}
