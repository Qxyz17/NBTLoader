package com.nbtloader.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NBTLoaderCore {
    private static final Map<String, Integer> ITEM_ID_MAP = new HashMap<>();
    
    static {
        // 初始化物品ID映射表
        initItemMappings();
    }
    
    public static void loadNBT(EntityPlayer player, int mode, String path) {
        File file = new File(path);
        
        // 安全检查：防止目录遍历攻击
        if (!isSafePath(file)) {
            player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.invalidpath")));
            return;
        }
        
        if (!file.exists()) {
            player.addChatMessage(new ChatComponentText(
                LanguageManager.translate("nbtloader.loader.filenotexist", path)
            ));
            return;
        }
        
        if (!file.isFile()) {
            player.addChatMessage(new ChatComponentText(
                LanguageManager.translate("nbtloader.loader.notfile", path)
            ));
            return;
        }
        
        if (file.length() > 1024 * 1024) { // 1MB限制
            player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.filetoolarge")));
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(fis);
            if (nbt == null) {
                // 尝试读取为JSON格式
                try {
                    String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                    nbt = JsonToNBT.getTagFromJson(content);
                } catch (Exception e) {
                    player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.invalidformat")));
                    return;
                }
            }
            
            if (nbt == null) {
                player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.invalidformat")));
                return;
            }
            
            // 从NBT创建物品
            ItemStack itemStack = createItemFromNBT(nbt);
            if (itemStack == null) {
                player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.failedcreate")));
                return;
            }
            
            if (mode == 0) {
                // 添加到玩家手中
                if (player.inventory.addItemStackToInventory(itemStack)) {
                    player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.addedtohand")));
                } else {
                    // 如果物品栏已满，掉落物品
                    player.dropItem(itemStack, false);
                    player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.droppeditem")));
                }
            } else if (mode == 1) {
                // 添加到玩家物品栏
                if (player.inventory.addItemStackToInventory(itemStack)) {
                    player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.addedtoinventory")));
                } else {
                    // 如果物品栏已满，掉落物品
                    player.dropItem(itemStack, false);
                    player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.droppeditem")));
                }
            }
        } catch (IOException e) {
            player.addChatMessage(new ChatComponentText(
                LanguageManager.translate("nbtloader.loader.loadfailed", e.getMessage())
            ));
        } catch (Exception e) {
            player.addChatMessage(new ChatComponentText(LanguageManager.translate("nbtloader.loader.unexpectederror")));
        }
    }
    
    private static ItemStack createItemFromNBT(NBTTagCompound nbt) {
        try {
            // 从NBT创建物品
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbt);
            if (itemStack != null) {
                return itemStack;
            }
            
            // 如果标准方法失败，尝试手动创建
            String itemId = null;
            int count = 1;
            int damage = 0;
            NBTTagCompound tag = null;
            
            // 尝试获取物品ID
            if (nbt.hasKey("id", 8)) { // 8 = 字符串
                itemId = nbt.getString("id");
            } else if (nbt.hasKey("Name", 8)) {
                itemId = nbt.getString("Name");
            } else if (nbt.hasKey("Block", 10)) { // 10 = 复合标签
                NBTTagCompound blockTag = nbt.getCompoundTag("Block");
                if (blockTag.hasKey("name", 8)) {
                    itemId = blockTag.getString("name");
                }
            }
            
            // 获取数量
            if (nbt.hasKey("Count", 1)) { // 1 = 字节
                count = nbt.getByte("Count");
            }
            
            // 获取损伤值
            if (nbt.hasKey("Damage", 2)) { // 2 = 短整型
                damage = nbt.getShort("Damage");
            }
            
            // 获取附加标签
            if (nbt.hasKey("tag", 10)) {
                tag = nbt.getCompoundTag("tag");
            }
            
            // 如果物品ID为空，使用默认物品
            if (itemId == null || itemId.isEmpty()) {
                itemId = "minecraft:stone";
            }
            
            // 创建物品堆栈
            Item item = getItemById(itemId);
            if (item == null) {
                // 如果物品不存在，使用石头代替
                item = Item.getItemById(1); // 石头
            }
            
            ItemStack stack = new ItemStack(item, count, damage);
            
            // 设置附加标签
            if (tag != null) {
                stack.setTagCompound(tag);
            }
            
            return stack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Item getItemById(String itemId) {
        try {
            // 尝试通过注册名获取物品
            Item item = (Item)Item.itemRegistry.getObject(itemId);
            if (item != null) {
                return item;
            }
            
            // 如果注册名获取失败，尝试通过数字ID获取
            if (itemId.contains(":")) {
                String[] parts = itemId.split(":");
                if (parts.length >= 2) {
                    // 尝试解析数字ID
                    try {
                        int id = Integer.parseInt(parts[1]);
                        return Item.getItemById(id);
                    } catch (NumberFormatException e) {
                        // 不是数字ID，继续尝试其他方法
                    }
                }
            }
            
            // 检查映射表
            if (ITEM_ID_MAP.containsKey(itemId)) {
                int id = ITEM_ID_MAP.get(itemId);
                return Item.getItemById(id);
            }
            
            // 最后尝试使用反射获取所有注册的物品
            for (Object obj : Item.itemRegistry.getKeys()) {
                String key = obj.toString();
                if (key.equalsIgnoreCase(itemId)) {
                    return (Item)Item.itemRegistry.getObject(key);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static boolean isSafePath(File file) {
        try {
            String canonicalPath = file.getCanonicalPath();
            // 防止目录遍历攻击
            return !canonicalPath.contains("../") && !canonicalPath.contains("..\\");
        } catch (IOException e) {
            return false;
        }
    }
    
    // 初始化物品映射表
    private static void initItemMappings() {
        // 添加一些常见但1.8.9不存在的物品映射
        ITEM_ID_MAP.put("minecraft:shulker_box", 218);
        ITEM_ID_MAP.put("minecraft:netherite_helmet", 310);
        ITEM_ID_MAP.put("minecraft:netherite_chestplate", 311);
        ITEM_ID_MAP.put("minecraft:netherite_leggings", 312);
        ITEM_ID_MAP.put("minecraft:netherite_boots", 313);
        ITEM_ID_MAP.put("minecraft:netherite_sword", 276);
        ITEM_ID_MAP.put("minecraft:netherite_pickaxe", 278);
        ITEM_ID_MAP.put("minecraft:netherite_axe", 279);
        ITEM_ID_MAP.put("minecraft:netherite_shovel", 277);
        ITEM_ID_MAP.put("minecraft:netherite_hoe", 293);
        ITEM_ID_MAP.put("minecraft:netherite_block", 57);
        ITEM_ID_MAP.put("minecraft:netherite_ingot", 266);
        ITEM_ID_MAP.put("minecraft:netherite_scrap", 265);
        ITEM_ID_MAP.put("minecraft:trident", 272);
        ITEM_ID_MAP.put("minecraft:turtle_helmet", 298);
        ITEM_ID_MAP.put("minecraft:phantom_membrane", 334);
        ITEM_ID_MAP.put("minecraft:crossbow", 261);
        ITEM_ID_MAP.put("minecraft:sweet_berries", 260);
        ITEM_ID_MAP.put("minecraft:campfire", 61);
        ITEM_ID_MAP.put("minecraft:lantern", 89);
        ITEM_ID_MAP.put("minecraft:bee_nest", 54);
        ITEM_ID_MAP.put("minecraft:beehive", 54);
        ITEM_ID_MAP.put("minecraft:honeycomb", 351);
        ITEM_ID_MAP.put("minecraft:honeycomb_block", 57);
        ITEM_ID_MAP.put("minecraft:honey_bottle", 373);
        ITEM_ID_MAP.put("minecraft:honey_block", 80);
    }
}
