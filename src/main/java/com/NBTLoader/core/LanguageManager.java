package com.nbtloader.core;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Map<String, Map<String, String>> LANGUAGE_MAP = new HashMap<>();
    
    static {
        // 初始化语言映射
        initLanguages();
    }
    
    private static void initLanguages() {
        // 中文翻译
        Map<String, String> zhCN = new HashMap<>();
        zhCN.put("nbtloader.gui.title", "NBT加载器");
        zhCN.put("nbtloader.gui.filepath", "文件路径:");
        zhCN.put("nbtloader.gui.loadtohand", "加载到手上");
        zhCN.put("nbtloader.gui.loadtoinventory", "加载到物品栏");
        zhCN.put("nbtloader.gui.close", "关闭");
        zhCN.put("nbtloader.gui.loadsuccess", "物品加载成功");
        zhCN.put("nbtloader.gui.nopath", "请输入文件路径");
        zhCN.put("nbtloader.gui.changelang", "切换语言");
        
        zhCN.put("nbtloader.command.usage", ".nbt <gui|模式> <路径>");
        zhCN.put("nbtloader.command.modes", "模式: 0=加载到手上, 1=加载到物品栏");
        zhCN.put("nbtloader.command.invalidmode", "无效的模式，请输入0或1");
        
        zhCN.put("nbtloader.loader.invalidpath", "无效的文件路径");
        zhCN.put("nbtloader.loader.filenotexist", "文件不存在: %s");
        zhCN.put("nbtloader.loader.notfile", "路径不是文件: %s");
        zhCN.put("nbtloader.loader.filetoolarge", "文件太大 (最大1MB)");
        zhCN.put("nbtloader.loader.invalidformat", "无效的NBT文件格式");
        zhCN.put("nbtloader.loader.failedcreate", "从NBT创建物品失败");
        zhCN.put("nbtloader.loader.addedtohand", "物品已添加到手上");
        zhCN.put("nbtloader.loader.addedtoinventory", "物品已添加到物品栏");
        zhCN.put("nbtloader.loader.droppeditem", "物品栏已满，物品已掉落");
        zhCN.put("nbtloader.loader.loadfailed", "加载失败: %s");
        zhCN.put("nbtloader.loader.unexpectederror", "发生意外错误");
        
        LANGUAGE_MAP.put("zh_CN", zhCN);
        
        // 英文翻译
        Map<String, String> enUS = new HashMap<>();
        enUS.put("nbtloader.gui.title", "NBT Loader");
        enUS.put("nbtloader.gui.filepath", "File Path:");
        enUS.put("nbtloader.gui.loadtohand", "Load to Hand");
        enUS.put("nbtloader.gui.loadtoinventory", "Load to Inventory");
        enUS.put("nbtloader.gui.close", "Close");
        enUS.put("nbtloader.gui.loadsuccess", "Item loaded successfully");
        enUS.put("nbtloader.gui.nopath", "Please enter a file path");
        enUS.put("nbtloader.gui.changelang", "Change Language");
        
        enUS.put("nbtloader.command.usage", ".nbt <gui|mode> <path>");
        enUS.put("nbtloader.command.modes", "Modes: 0=Load to hand, 1=Load to inventory");
        enUS.put("nbtloader.command.invalidmode", "Invalid mode. Use 0 or 1");
        
        enUS.put("nbtloader.loader.invalidpath", "Invalid file path");
        enUS.put("nbtloader.loader.filenotexist", "File does not exist: %s");
        enUS.put("nbtloader.loader.notfile", "Path is not a file: %s");
        enUS.put("nbtloader.loader.filetoolarge", "File is too large (max 1MB)");
        enUS.put("nbtloader.loader.invalidformat", "Invalid NBT file format");
        enUS.put("nbtloader.loader.failedcreate", "Failed to create item from NBT");
        enUS.put("nbtloader.loader.addedtohand", "Item added to your hand");
        enUS.put("nbtloader.loader.addedtoinventory", "Item added to your inventory");
        enUS.put("nbtloader.loader.droppeditem", "Inventory full, item dropped");
        enUS.put("nbtloader.loader.loadfailed", "Load failed: %s");
        enUS.put("nbtloader.loader.unexpectederror", "An unexpected error occurred");
        
        LANGUAGE_MAP.put("en_US", enUS);
    }
    
    public static String translate(String key, Object... args) {
        String lang = ConfigHandler.getLanguage();
        Map<String, String> translations = LANGUAGE_MAP.get(lang);
        
        if (translations != null && translations.containsKey(key)) {
            String translation = translations.get(key);
            return String.format(translation, args);
        }
        
        // 如果当前语言没有翻译，尝试使用英文
        if (!lang.equals("en_US")) {
            translations = LANGUAGE_MAP.get("en_US");
            if (translations != null && translations.containsKey(key)) {
                String translation = translations.get(key);
                return String.format(translation, args);
            }
        }
        
        // 如果都没有，返回键名
        return key;
    }
}
