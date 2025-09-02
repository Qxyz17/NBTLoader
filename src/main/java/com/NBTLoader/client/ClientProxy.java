package com.nbtloader.client;

import com.nbtloader.common.CommonProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // 注册客户端命令处理器
        new com.nbtloader.command.NBTCommand().register();
    }
    
    public static void openGUI() {
        net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(new NBTGUI());
    }
}
