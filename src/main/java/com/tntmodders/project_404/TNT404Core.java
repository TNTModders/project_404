package com.tntmodders.project_404;

import com.tntmodders.project_404.block.BlockScranton;
import com.tntmodders.project_404.core.TNT404ModInfoCore;
import com.tntmodders.project_404.tile.TileEntityScranton;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
                event.getEntityLiving().world.playerEntities.size() <= 1) {
            if (event.getEntityLiving().getDistanceSq(event.getEntityLiving().world.getSpawnPoint()) > 256 &&
                    event.getEntityLiving().world.loadedTileEntityList.stream().noneMatch(
                            tileEntity -> tileEntity instanceof TileEntityScranton &&
                                    event.getEntityLiving().getDistanceSq(tileEntity.getPos()) < 256)) {
                List<String> blocks = new ArrayList<>();
                event.getEntityLiving().getEntityData().getKeySet().forEach(s -> {
                    if (s.startsWith("404_")) {
                        Block block = Block.getBlockFromName(s.replace("404_", ""));
                        if (block != null) {
                            blocks.add(block.getRegistryName().toString());
                        }
                    }
                });
                //blocks.add(Blocks.GRASS);
                for (int i = 0; i < 50; i++) {
                    Random rand = new Random();
                    rand.setSeed(Math.abs(System.currentTimeMillis() / 100));
                    int x = MathHelper.getInt(rand, -7, 8);
                    int y = MathHelper.getInt(rand, -7, 8);
                    int z = MathHelper.getInt(rand, -7, 8);
                    Block block = event.getEntityLiving().world.getBlockState(
                            event.getEntityLiving().getPosition().add(x, y, z)).getBlock();
                    if (blocks.contains(block.getRegistryName().toString())) {
                        LOGGER.info("dest:" + block.getLocalizedName());

                        if (!event.getEntityLiving().world.isRemote) {
                            event.getEntityLiving().world.setBlockState(
                                    event.getEntityLiving().getPosition().add(x, y, z), Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }

            if (event.getEntityLiving().world.getWorldTime() % 6000 == 0) {
                for (int i = 0; i < 5; i++) {
                    Random rand = new Random();
                    rand.setSeed(Math.abs(System.currentTimeMillis() / 1000));
                    Block block = Block.REGISTRY.getRandomObject(rand);
                    event.getEntityLiving().getEntityData().setBoolean("404_" + block.getRegistryName().toString(),
                            true);
                    LOGGER.info(block);
                }
                event.getEntityLiving().getEntityData().setBoolean("404_" + event.getEntityLiving().world.getBlockState(
                        event.getEntityLiving().getPosition().down()).getBlock().getRegistryName().toString(), true);
            }
        }
    }

    @SubscribeEvent
    public void sleep(PlayerSleepInBedEvent event) {
        event.getEntityPlayer().getEntityData().getKeySet().forEach(s -> {
            if (s.contains("404_")) {
                LOGGER.info(s);
            }
        });
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