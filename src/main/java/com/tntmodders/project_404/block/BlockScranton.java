package com.tntmodders.project_404.block;

import com.tntmodders.project_404.TNT404Core;
import com.tntmodders.project_404.tile.TileEntityScranton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockScranton extends BlockContainer {
    public BlockScranton() {
        super(Material.IRON);
        this.setRegistryName(TNT404Core.MODID, "scranton");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setUnlocalizedName("scranton");
        this.setHardness(10f);
        this.setResistance(1000000f);
        this.setHarvestLevel("pickaxe",3);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityScranton();
    }
}
