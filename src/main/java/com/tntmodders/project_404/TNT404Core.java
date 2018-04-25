package com.tntmodders.project_404;

import com.tntmodders.project_404.block.BlockScranton;
import com.tntmodders.project_404.core.TNT404ModInfoCore;
import com.tntmodders.project_404.tile.TileEntityScranton;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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


@Mod(modid = TNT404Core.MODID, version = TNT404Core.VERSION, acceptedMinecraftVersions = "[1.12.2]", name = TNT404Core.MODID, updateJSON = "https://raw.githubusercontent.com/TNTModders/project_404/master/version/version.json")
public class TNT404Core {

    //初期設定
    public static final String MODID = "project_404";
    public static final String VERSION = "1.1";
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
                event.getEntityLiving().world.playerEntities.size() <= 1 && !event.getEntityLiving().world.isRemote) {
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
                for (int i = 0; i < 100; i++) {
                    Random rand = event.getEntityLiving().getRNG();
                    int x = MathHelper.getInt(rand, -7, 8);
                    int y = MathHelper.getInt(rand, -7, 8);
                    int z = MathHelper.getInt(rand, -7, 8);
                    Block block = event.getEntityLiving().world.getBlockState(
                            event.getEntityLiving().getPosition().add(x, y, z)).getBlock();
                    if (blocks.contains(block.getRegistryName().toString())) {
                        LOGGER.info("dest:" + block.getLocalizedName());
                        event.getEntityLiving().world.setBlockToAir(event.getEntityLiving().getPosition().add(x, y, z));
                    }
                }
            }

            if (event.getEntityLiving().world.getWorldTime() % 1000 == 0) {
                Random rand = new Random();
                rand.setSeed(Math.abs(System.currentTimeMillis() / 1000));
                Block block = Block.REGISTRY.getRandomObject(rand);
                event.getEntityLiving().getEntityData().setBoolean("404_" + block.getRegistryName().toString(), true);
                LOGGER.info(block);
                if (event.getEntityLiving() instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) event.getEntityLiving()).connection.sendPacket(
                            new PacketChat(Block.getIdFromBlock(block)));
                }
            }
        }
    }

    @SubscribeEvent
    public void ondeath(PlayerEvent.Clone event) {
        event.getOriginal().getEntityData().getKeySet().forEach(
                s -> event.getEntityPlayer().getEntityData().setTag(s, event.getOriginal().getEntityData().getTag(s)));
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

    private class PacketChat implements Packet<INetHandlerPlayClient> {
        int i;

        private PacketChat() {
        }

        private PacketChat(Integer i) {
            this.i = i;
        }

        @Override
        public void readPacketData(PacketBuffer buf) {
            this.i = buf.readInt();
        }

        @Override
        public void writePacketData(PacketBuffer buf) {
            buf.writeInt(i);
        }

        @Override
        public void processPacket(INetHandlerPlayClient handler) {
            Handler.handler(i);
        }
    }

    @SideOnly(Side.CLIENT)
    private static class Handler {
        private static void handler(int i) {
            Block block = Block.getBlockById(i);
            Minecraft.getMinecraft().getToastGui().add((toastGui, delta) -> {
                toastGui.getMinecraft().getTextureManager().bindTexture(IToast.TEXTURE_TOASTS);
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
                toastGui.getMinecraft().fontRenderer.drawString("SCP-404-MC", 30, 7, -11534256);
                toastGui.getMinecraft().fontRenderer.drawString("対象追加：" + block.getLocalizedName(), 30, 18, -16777216);
                RenderHelper.enableGUIStandardItemLighting();
                toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(null, new ItemStack(block), 8, 8);
                return delta >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
            });

        }
    }
}