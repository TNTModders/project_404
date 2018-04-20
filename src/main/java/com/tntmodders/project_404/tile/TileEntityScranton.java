package com.tntmodders.project_404.tile;

import com.tntmodders.project_404.TNT404Core;
import net.minecraft.tileentity.TileEntity;

public class TileEntityScranton extends TileEntity {
    @Override
    public double getDistanceSq(double x, double y, double z) {
        TNT404Core.LOGGER.info(this.getPos());
        return super.getDistanceSq(x, y, z);
    }
}
