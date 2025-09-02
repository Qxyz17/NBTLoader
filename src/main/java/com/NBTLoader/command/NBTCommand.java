package com.nbtloader.command;

import com.nbtloader.core.LanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import com.nbtloader.core.NBTLoaderCore;
import com.nbtloader.client.ClientProxy;

public class NBTCommand {
    private static boolean registered = false;
    
    public void register() {
        if (!registered) {
            // 注册客户端命令处理器
            ClientCommandHandler.instance.registerCommand(new ClientNBTCommand());
            registered = true;
        }
    }
    
    // 客户端命令类
    public static class ClientNBTCommand extends net.minecraft.command.CommandBase {
        @Override
        public String getCommandName() {
            return ".nbt";
        }
        
        @Override
        public String getCommandUsage(net.minecraft.command.ICommandSender sender) {
            return LanguageManager.translate("nbtloader.command.usage");
        }
        
        @Override
        public void processCommand(net.minecraft.command.ICommandSender sender, String[] args) {
            if (args.length == 0) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                sender.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.command.modes")));
                return;
            }
            
            // 处理 "gui" 命令
            if (args[0].equalsIgnoreCase("gui")) {
                ClientProxy.openGUI();
                return;
            }
            
            // 处理语言切换命令
            if (args[0].equalsIgnoreCase("lang")) {
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(".nbt lang <zh_CN|en_US>"));
                    return;
                }
                
                String lang = args[1];
                if (lang.equals("zh_CN") || lang.equals("en_US")) {
                    com.nbtloader.core.ConfigHandler.setLanguage(lang);
                    sender.addChatMessage(new ChatComponentText("Language set to: " + lang));
                } else {
                    sender.addChatMessage(new ChatComponentText("Invalid language. Use zh_CN or en_US"));
                }
                return;
            }
            
            // 处理加载命令
            if (args.length < 2) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                sender.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.command.modes")));
                return;
            }
            
            int mode;
            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.command.invalidmode")));
                return;
            }
            
            if (mode != 0 && mode != 1) {
                sender.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.command.invalidmode")));
                return;
            }
            
            // 合并路径参数（处理路径中的空格）
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) pathBuilder.append(" ");
                pathBuilder.append(args[i]);
            }
            String path = pathBuilder.toString();
            
            // 直接在客户端执行，不发送到服务器
            NBTLoaderCore.loadNBT(Minecraft.getMinecraft().thePlayer, mode, path);
        }
        
        @Override
        public boolean canCommandSenderUseCommand(net.minecraft.command.ICommandSender sender) {
            return true;
        }
        
        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }
    }
}
