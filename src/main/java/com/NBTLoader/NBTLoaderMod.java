package com.nbtloader;

import com.nbtloader.client.ClientProxy;
import com.nbtloader.core.ConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NBTLoaderMod.MODID, name = NBTLoaderMod.NAME, version = NBTLoaderMod.VERSION)
public class NBTLoaderMod {
    public static final String MODID = "nbtloader";
    public static final String NAME = "NBT Loader";
    public static final String VERSION = "1.0";
    
    @SidedProxy(
        clientSide = "com.nbtloader.client.ClientProxy",
        serverSide = "com.nbtloader.common.CommonProxy"
    )
    public static ClientProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 初始化配置
        ConfigHandler.init(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
