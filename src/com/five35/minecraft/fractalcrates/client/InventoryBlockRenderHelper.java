package com.five35.minecraft.fractalcrates.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class InventoryBlockRenderHelper extends BlockRenderHelper {
	public InventoryBlockRenderHelper(final Block block) {
		this(block, 0);
	}

	public InventoryBlockRenderHelper(final Block block, final int metadata) {
		super(block, metadata);
	}

	@Override
	public boolean renderQuad(final ForgeDirection dir, final Icon icon, final double depth, final double x1, final double y1, final double x2, final double y2, final double u1, final double v1, final double u2, final double v2) {
		final double topV = icon.getInterpolatedV(v1);
		final double rightU = icon.getInterpolatedU(u2);
		final double bottomV = icon.getInterpolatedV(v2);
		final double leftU = icon.getInterpolatedU(u1);

		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);

		this.addVertex(new Vertex(dir, depth, x1, y1), leftU, topV);
		this.addVertex(new Vertex(dir, depth, x1, y2), leftU, bottomV);
		this.addVertex(new Vertex(dir, depth, x2, y2), rightU, bottomV);
		this.addVertex(new Vertex(dir, depth, x2, y1), rightU, topV);

		Tessellator.instance.draw();

		return true;
	}
}
