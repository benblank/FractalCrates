package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
	protected final int CRATE_GUI_ID = 0;
	protected final int crateRendererId;

	public CommonProxy() {
		this(-1);
	}

	public CommonProxy(final int crateRendererId) {
		this.crateRendererId = crateRendererId;
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		return null;
	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (id == this.CRATE_GUI_ID) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te != null && te instanceof CrateTileEntity) {
				return new CrateContainer(player, (CrateTileEntity) te);
			}
		}

		return null;
	}

	public void registerRenderers() {
		// nothing to do on server
	}
}
