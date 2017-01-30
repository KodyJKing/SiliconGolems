package silicongolems.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import silicongolems.SiliconGolems;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenText extends GuiScreen {
    private static final ResourceLocation textGuiTextures = new ResourceLocation(SiliconGolems.modId, "textures/gui/text_editor.png");

    private int editorWidth = 248;
    private int editorHeight = 166;
    private int boarderWidth = 8;

    private int textWidth = 38;
    private int textHeight = 18;

    private int charWidth = 6;
    private int charHeight = 8;

    public int textScale = 2;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public int cornerX(){
        return (this.width - this.getEditorWidth()) / 2;
    }

    public int cornerY(){
        return (this.height - this.getEditorHeight()) / 2;
    }

    public int textCornerX(){
        return cornerX() + getBoarderWidth();
    }

    public int textCornerY(){
        return cornerY() + getBoarderWidth() + getCharHeight() / 2;
    }

//    public int cellX(int textX){
//        return textCornerX() + textX * getCharWidth();
//    }
//
//    public int cellY(int textY){
//        return textCornerY() + textY * getCharHeight();
//    }

    public int cellX(int textX){
        return textX * charWidth;
    }

    public int cellY(int textY){
        return textY * charHeight;
    }

    public void drawChar(int x, int y, char c, TextFormatting color){
        drawChar(x, y, c, color, true);
    }

    public void drawChar(int x, int y, char c, TextFormatting color, boolean fixThin){
        GlStateManager.pushMatrix();
        GlStateManager.translate(textCornerX(), textCornerY(), 0);
        GlStateManager.scale(1.0 / textScale, 1.0 / textScale, 1.0 / textScale);
        int xAdjust = fixThin && isThin(c) ? 2 : 0;
        this.fontRendererObj.drawString(
                color + Character.toString(c),
                cellX(x) + xAdjust,
                cellY(y),
                8);
        GlStateManager.popMatrix();
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
        this.drawTexturedModalRect(x, y, 0, 0, this.getEditorWidth(), this.getEditorHeight());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void keyTyped(char c, int keyCode) throws IOException {
        boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

        //System.out.println(keyCode); //Comment me out!

        if(isKeyComboCtrlT(keyCode))
            onCtrlT();
        else if(keyCode == 15)
            onType("    ");
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

    public boolean isKeyComboCtrlT(int keyCode){
        return keyCode == 20 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0)
            return;

        int textX = (mouseX - textCornerX()) / getCharWidth();
        int textY = (mouseY - textCornerY()) / getCharHeight();
        if(textX >= 0 && textX <= getTextWidth() && textY >= 0 && textY <= getTextHeight())
            onClickCell(textX, textY, mouseButton);
    }

    public boolean onEscape(){return false;}

    public void onEnter(){}
    public void onVertArrow(int dir){}
    public void onSideArrow(int dir, boolean ctrl){}
    public void onBackspace(boolean ctrl){}
    public void onType(String string){}
    public void onClickCell(int x, int y, int button){}
    public void onCtrlT(){}

    public int getEditorWidth() {
        return editorWidth;
    }
    public int getEditorHeight() {
        return editorHeight;
    }
    public int getBoarderWidth() {
        return boarderWidth;
    }
    public int getTextWidth() {
        return textWidth * textScale;
    }
    public int getTextHeight() {
        return textHeight * textScale;
    }
    public int getCharWidth() {
        return charWidth / textScale;
    }
    public int getCharHeight() {
        return charHeight / textScale;
    }
}
