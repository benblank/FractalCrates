package com.five35.minecraft.fractalcrates.client;

import com.five35.minecraft.fractalcrates.CommonProxy;
import com.five35.minecraft.fractalcrates.CrateContainer;
import com.five35.minecraft.fractalcrates.CrateTileEntity;
import com.five35.minecraft.fractalcrates.FractalCrates;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	public ClientProxy() {
		super(RenderingRegistry.getNextAvailableRenderId());
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (id == this.CRATE_GUI_ID) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te != null && te instanceof CrateTileEntity) {
				return new CrateGui(new CrateContainer(player, (CrateTileEntity) te));
			}
		}

		return null;
	}

	@Override
	public void registerRenderers() {
		final CrateRenderer renderer = new CrateRenderer(this.crateRendererId);

		RenderingRegistry.registerBlockHandler(renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(CrateTileEntity.class, renderer);
		MinecraftForgeClient.registerItemRenderer(FractalCrates.crate.blockID, renderer);
	}
}
