package com.nbtloader.core;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ConfigHandler {
    private static Configuration config;
    public static String language;

    public static void init(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "nbtloader.cfg");
        config = new Configuration(configFile);
        config.load();
        
        // 语言设置
        Property langProp = config.get(Configuration.CATEGORY_CLIENT, "language", "zh_CN", 
                "Language setting (zh_CN/en_US)");
        langProp.setLanguageKey("nbtloader.config.language");
        language = langProp.getString();
        
        config.save();
    }
    
    public static void setLanguage(String lang) {
        language = lang;
        config.get(Configuration.CATEGORY_CLIENT, "language", "zh_CN").set(lang);
        config.save();
    }
    
    public static String getLanguage() {
        return language;
    }
}
