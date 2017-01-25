package silicongolems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import silicongolems.SiliconGolems;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenText extends GuiScreen {
    private static final ResourceLocation textGuiTextures = new ResourceLocation(SiliconGolems.modId, "textures/gui/text_editor.png");
    public int editorWidth = 248;
    public int editorHeight = 166;
    public int boarderWidth = 8;
    public int textWidth = 38;
    public int textHeight = 18;
    public int charWidth = 6;
    public int charHeight = 8;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public int cornerX(){
        return (this.width - this.editorWidth) / 2;
    }

    public int cornerY(){
        return (this.height - this.editorHeight) / 2;
    }

    public int textCornerX(){
        return cornerX() + boarderWidth;
    }

    public int textCornerY(){
        return cornerY() + boarderWidth;
    }

    public int cellX(int textX){
        return textCornerX() + textX * charWidth;
    }

    public int cellY(int textY){
        return textCornerY() + textY * charHeight;
    }

    public void drawChar(int x, int y, char c, TextFormatting color){
        drawChar(x, y, c, color, true);
    }

    public void drawChar(int x, int y, char c, TextFormatting color, boolean fixThin){
        int xAdjust = fixThin && isThin(c) ? 2 : 0;
        this.fontRendererObj.drawString(
                color + Character.toString(c),
                cellX(x) + xAdjust,
                cellY(y),
                8);
    }

    public boolean isThin(char c) {
        return "il|!t,.".contains(Character.toString(c));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(mc == null){
            System.out.println("Minecraft reference is null!");
            return;
        }
        if(mc.getTextureManager() == null){
            System.out.println("Texture manager reference is null!");
            return;
        }

        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(textGuiTextures);
        int x = cornerX();
        int y = cornerY();
        this.drawTexturedModalRect(x, y, 0, 0, this.editorWidth, this.editorHeight);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    @Override
    protected void keyTyped(char c, int keyCode) throws IOException {
        boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

        if(keyCode == 1 && !onEscape())
            super.keyTyped(c, keyCode);
        else if(GuiScreen.isKeyComboCtrlV(keyCode))
            onType(GuiScreen.getClipboardString());
        else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            onSideArrow(-1, ctrl);
        else if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            onSideArrow(1, ctrl);
        else if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            onVertArrow(-1);
        else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            onVertArrow(1);
        else if(keyCode == 14)
            onBackspace(ctrl);
        else if(keyCode == 156 || keyCode == 28)
            onEnter();
        else if(ChatAllowedCharacters.isAllowedCharacter(c))
            onType(Character.toString(c));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0)
            return;

        int textX = (mouseX - textCornerX()) / charWidth;
        int textY = (mouseY - textCornerY()) / charHeight;
        if(textX >= 0 && textX <= textWidth && textY >= 0 && textY <= textHeight)
            onClickCell(textX, textY, mouseButton);
    }

    public boolean onEscape(){return false;}

    public void onEnter(){}
    public void onVertArrow(int dir){}
    public void onSideArrow(int dir, boolean ctrl){}
    public void onBackspace(boolean ctrl){}
    public void onType(String string){}
    public void onClickCell(int x, int y, int button){}
}
