package com.tntmodders.project_404;

import com.tntmodders.project_404.core.TNT404ModInfoCore;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    public static TNT404Core TakumiInstance;
    @Metadata(MODID)
    public static ModMetadata metadata;

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        TNT404ModInfoCore.load(metadata);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void event(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
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
                event.getEntityLiving().getPosition().add(x, y, z), type.getBreakSound(), SoundCategory.BLOCKS,
                type.volume, type.pitch);
        Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(
                event.getEntityLiving().getPosition().add(x, y, z),
                event.getEntityLiving().world.getBlockState(event.getEntityLiving().getPosition().add(x, y, z)));
    }
}