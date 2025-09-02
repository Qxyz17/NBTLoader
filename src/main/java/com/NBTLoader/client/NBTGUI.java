package com.nbtloader.client;

import com.nbtloader.core.LanguageManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class NBTGUI extends GuiScreen {
    private GuiTextField pathField;
    private String statusMessage = "";
    private long statusMessageTime = 0;
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.pathField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.pathField.setMaxStringLength(255);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 100, 98, 20, LanguageManager.translate("nbtloader.gui.loadtohand")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, 100, 98, 20, LanguageManager.translate("nbtloader.gui.loadtoinventory")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 130, 200, 20, LanguageManager.translate("nbtloader.gui.close")));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, 160, 200, 20, LanguageManager.translate("nbtloader.gui.changelang")));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, LanguageManager.translate("nbtloader.gui.title"), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.fontRendererObj, LanguageManager.translate("nbtloader.gui.filepath"), this.width / 2 - 100, 47, 0xA0A0A0);
        this.pathField.drawTextBox();
        
        // 显示状态消息
        if (!statusMessage.isEmpty() && System.currentTimeMillis() - statusMessageTime < 5000) {
            this.drawCenteredString(this.fontRendererObj, statusMessage, this.width / 2, 190, 0xFF5555);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.pathField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 || button.id == 1) {
            String path = this.pathField.getText().trim();
            if (!path.isEmpty()) {
                // 直接在客户端执行，不发送到服务器
                int mode = button.id;
                com.nbtloader.core.NBTLoaderCore.loadNBT(
                    net.minecraft.client.Minecraft.getMinecraft().thePlayer, 
                    mode, 
                    path
                );
                this.statusMessage = LanguageManager.translate("nbtloader.gui.loadsuccess");
                this.statusMessageTime = System.currentTimeMillis();
            } else {
                this.statusMessage = LanguageManager.translate("nbtloader.gui.nopath");
                this.statusMessageTime = System.currentTimeMillis();
            }
        } else if (button.id == 2) {
            this.mc.displayGuiScreen(null);
        } else if (button.id == 3) {
            // 切换语言
            String currentLang = com.nbtloader.core.ConfigHandler.getLanguage();
            String newLang = currentLang.equals("zh_CN") ? "en_US" : "zh_CN";
            com.nbtloader.core.ConfigHandler.setLanguage(newLang);
            
            // 重新初始化GUI以更新文本
            this.mc.displayGuiScreen(null);
            ClientProxy.openGUI();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
