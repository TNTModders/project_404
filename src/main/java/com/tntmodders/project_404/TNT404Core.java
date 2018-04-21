package com.tntmodders.project_404;

import com.tntmodders.project_404.block.BlockScranton;
import com.tntmodders.project_404.core.TNT404ModInfoCore;
import com.tntmodders.project_404.tile.TileEntityScranton;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = TNT404Core.MODID, version = TNT404Core.VERSION, acceptedMinecraftVersions = "[1.12.2]", name = TNT404Core.MODID)
public class TNT404Core {

    //初期設定
    public static final String MODID = "project_404";
    public static final String VERSION = "1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    @Instance(MODID)
    public static TNT404Core Instance;
    @Metadata(MODID)
    public static ModMetadata metadata;

    public static final Block SCRANTON = new BlockScranton();

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        TNT404ModInfoCore.load(metadata);
        MinecraftForge.EVENT_BUS.register(this);
        GameRegistry.registerTileEntity(TileEntityScranton.class, "scranton");
    }

    @SubscribeEvent
    public void event(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer &&
                event.getEntityLiving().getDistanceSq(event.getEntityLiving().world.getSpawnPoint()) > 256 &&
                event.getEntityLiving().world.loadedTileEntityList.stream().noneMatch(
                        tileEntity -> tileEntity instanceof TileEntityScranton &&
                                event.getEntityLiving().getDistanceSq(tileEntity.getPos()) < 256)) {
            for (int i = 0; i < 10; i++) {
                int x = MathHelper.getInt(event.getEntityLiving().world.rand, -7, 8);
                int y = MathHelper.getInt(event.getEntityLiving().world.rand, -7, 8);
                int z = MathHelper.getInt(event.getEntityLiving().world.rand, -7, 8);
                if (event.getEntityLiving().world.getBlockState(
                        event.getEntityLiving().getPosition().add(x, y, z)).getBlock() == Blocks.DIRT) {
                    if (FMLCommonHandler.instance().getSide().isClient() && event.getEntityLiving().world.isRemote) {
                        this.clientEvent(event, x, y, z);
                    }
                    if (!event.getEntityLiving().world.isRemote) {
                        event.getEntityLiving().world.setBlockToAir(event.getEntityLiving().getPosition().add(x, y, z));
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientEvent(LivingEvent.LivingUpdateEvent event, int x, int y, int z) {
        IBlockState state =
                event.getEntityLiving().world.getBlockState(event.getEntityLiving().getPosition().add(x, y, z));
        SoundType type = state.getBlock().getSoundType(state, event.getEntityLiving().world,
                event.getEntityLiving().getPosition().add(x, y, z), null);
        event.getEntityLiving().world.playSound(Minecraft.getMinecraft().player,
                event.getEntityLiving().getPosition().add(x, y, z), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS, 0.5F, 2.6F + (event.getEntityLiving().world.rand.nextFloat() -
                        event.getEntityLiving().world.rand.nextFloat()) * 0.8F);
        Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(
                event.getEntityLiving().getPosition().add(x, y, z),
                event.getEntityLiving().world.getBlockState(event.getEntityLiving().getPosition().add(x, y, z)));
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(SCRANTON);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(SCRANTON).setRegistryName(MODID, "scranton"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(SCRANTON), 0,
                new ModelResourceLocation(new ResourceLocation(MODID, "scranton"), "inventory"));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator((random, chunkX, chunkZ, world, chunkGenerator, chunkProvider) -> {
            if (random.nextBoolean()) {
                new WorldGenMinable(SCRANTON.getDefaultState(), 3).generate(world, random,
                        new BlockPos(random.nextInt(16) + (chunkX << 4), random.nextInt(16),
                                random.nextInt(16) + (chunkZ << 4)));
            }
        }, 1);
    }
}