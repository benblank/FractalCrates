package com.five35.minecraft.fractalcrates.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public abstract class BlockRenderHelper {
	protected static class Vertex {
		protected final double x;
		protected final double y;
		protected final double z;

		private static double dim(final double pos, final int offset) {
			return (offset > 0 ? 1 : 0) + pos / 16 * offset * -1;
		}

		protected Vertex(final ForgeDirection dir, final double depth, final double x, final double y) {
			final ForgeDirection topDir = BlockRenderHelper.dirTop.get(dir);
			final ForgeDirection leftDir = topDir.getRotation(dir.getOpposite());

			this.x = Vertex.dim(depth, dir.offsetX) + Vertex.dim(x, leftDir.offsetX) + Vertex.dim(y, topDir.offsetX);
			this.y = Vertex.dim(depth, dir.offsetY) + Vertex.dim(x, leftDir.offsetY) + Vertex.dim(y, topDir.offsetY);
			this.z = Vertex.dim(depth, dir.offsetZ) + Vertex.dim(x, leftDir.offsetZ) + Vertex.dim(y, topDir.offsetZ);
		}
	}

	protected static final Map<ForgeDirection, ForgeDirection> dirTop = new HashMap<ForgeDirection, ForgeDirection>();

	static {
		BlockRenderHelper.dirTop.put(ForgeDirection.DOWN, ForgeDirection.NORTH);
		BlockRenderHelper.dirTop.put(ForgeDirection.UP, ForgeDirection.NORTH);
		BlockRenderHelper.dirTop.put(ForgeDirection.NORTH, ForgeDirection.UP);
		BlockRenderHelper.dirTop.put(ForgeDirection.SOUTH, ForgeDirection.UP);
		BlockRenderHelper.dirTop.put(ForgeDirection.WEST, ForgeDirection.UP);
		BlockRenderHelper.dirTop.put(ForgeDirection.EAST, ForgeDirection.UP);
	}

	protected final Block block;
	protected final int metadata;

	protected BlockRenderHelper(final Block block, final int metadata) {
		this.block = block;
		this.metadata = metadata;
	}

	// this method must not be static, so that WorldBlockRenderHelper can override it
	@SuppressWarnings("static-method")
	protected void addVertex(final Vertex vertex, final double u, final double v) {
		Tessellator.instance.addVertexWithUV(vertex.x, vertex.y, vertex.z, u, v);
	}

	public boolean renderFace(final ForgeDirection dir) {
		return this.renderFace(dir, this.block.getIcon(dir.ordinal(), this.metadata));
	}

	public boolean renderFace(final ForgeDirection dir, final Icon icon) {
		// defaults are no depth (at face) and full width/height of face/texture
		return this.renderQuad(dir, icon, 0, 0, 0, 16, 16);
	}

	public boolean renderQuad(final ForgeDirection dir, final double depth, final double x1, final double y1, final double x2, final double y2) {
		return this.renderQuad(dir, depth, x1, y1, x2, y2, x1, y1, x2, y2);
	}

	public boolean renderQuad(final ForgeDirection dir, final double depth, final double x1, final double y1, final double x2, final double y2, final double u1, final double v1, final double u2, final double v2) {
		return this.renderQuad(dir, this.block.getIcon(dir.ordinal(), this.metadata), depth, x1, y1, x2, y2, u1, v1, u2, v2);
	}

	public boolean renderQuad(final ForgeDirection dir, final Icon icon, final double depth, final double x1, final double y1, final double x2, final double y2) {
		return this.renderQuad(dir, icon, depth, x1, y1, x2, y2, x1, y1, x2, y2);
	}

	public abstract boolean renderQuad(final ForgeDirection dir, final Icon icon, final double depth, final double x1, final double y1, final double x2, final double y2, final double u1, final double v1, final double u2, final double v2);

}
