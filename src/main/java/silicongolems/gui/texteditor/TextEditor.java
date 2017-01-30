package silicongolems.gui.texteditor;

import net.minecraft.util.ChatAllowedCharacters;
import silicongolems.common.Common;

//Manages operations on text.
public class TextEditor {
    public int cursorX, cursorY;
    public DualStackList<StringBuilder> lines;

    public TextEditor() {
        clear();
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        int lineNum = 0;
        for(StringBuilder line : lines){
            if(lineNum++ > 0)
                result.append("\n");
            result.append(line);
        }
        return result.toString();
    }

    public void clear(){
        cursorX = 0;
        cursorY = 0;

        if(lines != null)
            lines.clear();
        else
            lines = new DualStackList<StringBuilder>();
        lines.add(new StringBuilder());
    }

    public StringBuilder getLine(){
        return lines.get(cursorY);
    }

    public StringBuilder getLine(int i){
        return lines.get(i);
    }

    //region typing
    public void type(String str) {
        if(str.length() == 1 && ChatAllowedCharacters.isAllowedCharacter(str.charAt(0))){
            getLine().insert(cursorX, str);
            cursorX++;
        } else {
            typeLines(str);
        }
    }

    public void typeLines(String str){
        int lineNum = 0;
        for(String substr : str.split("\n", -1)) {
            substr = Common.removeUnprintable(substr);
            if(lineNum++ > 0)
                newline(false);
            getLine().insert(cursorX, substr);
            cursorX += substr.length();
        }
    }
    //endregion

    //region newline
    public void newline(boolean isEditing){
        int indent = isEditing ? getIndent() : 0;
        boolean makeBlock = isEditing ? shouldMakeBlock() : false;

        splitLine();
        cursorY++;
        cursorX = 0;

        if(!isEditing)
            return;

        doIndent(indent);

        if(makeBlock){
            doIndent(4);
            int oldx = cursorX;
            int oldy = cursorY;
            newline(true);
            doIndent(indent);
            type("}");
            cursorX = oldx;
            cursorY = oldy;
        }
    }

    public void splitLine(){
        StringBuilder line = getLine();
        String newLine = line.substring(cursorX);
        line.replace(cursorX, line.length(), "");
        lines.add(cursorY + 1, new StringBuilder(newLine));
    }

    public int getIndent(){
        int currLine = getIndent(cursorY);
        if(cursorY + 1 >= lines.size())
            return  currLine;
        return Math.max(currLine, getIndent(cursorY + 1));
    }

    public boolean shouldMakeBlock(){
        boolean curly = safeGetChar(cursorX - 1) == '{';
        if(cursorY + 1 >= lines.size())
            return curly;
        else
            return curly && getIndent(cursorY) >= getIndent(cursorY + 1);
    }

    public int getIndent(int linenum){
        String line = getLine(linenum).toString();
        int i = 0;
        for(; i < line.length() && line.charAt(i) == ' '; i++);
        return i;
    }

    public void doIndent(int indent){
        for(int i = 0; i < indent; i++) type(" ");
    }
    //endregion

    //region backspace
    public void backspace(){
        if(cursorX > 0){
            getLine().deleteCharAt(cursorX - 1);
            cursorX--;
        } else {
            mergeLines();
        }
    }

    public void mergeLines(){
        if(cursorY <= 0)
            return;

        StringBuilder lowerLine = lines.remove(cursorY);
        StringBuilder upperLine = getLine(cursorY - 1);
        cursorX = upperLine.length();

        upperLine.append(lowerLine);
        cursorY--;
    }
    //endregion

    //region ctrl-actions
    public void ctrlBackspace(){
        int stop = ctrlSkip(-1);
        if(stop == cursorX - 1)
            backspace();
        else{
            if(stop < 0)
                stop = 0;
            getLine().replace(stop, cursorX, "");
            cursorX = stop;
        }
    }

    public void ctrlMove(int dir) {
        int stop = ctrlSkip(dir);
        if (stop == cursorX + dir){
            moveCursorX(dir);
        } else {
            cursorX = ctrlSkip(dir);
            clampX();
        }
    }

    /**
     * Finds new position for cursor after performing ctrl-arrow or ctrl-backspace.
     */
    public int ctrlSkip(int dir){
        int currX = cursorX;
        int lookAhead = dir < 0 ? dir : 0;

        char currChar = safeGetChar(currX + lookAhead);

        boolean whitespace, identifier;
        whitespace = Character.isWhitespace(currChar);
        identifier = Character.isJavaIdentifierPart(currChar);

        if(!whitespace && !identifier)
            return currX + dir;

        while(inLine(currX) || currX == getLine().length()){
            currChar = safeGetChar(currX + lookAhead);

            if(whitespace){

                if(Character.isWhitespace(currChar))
                    currX += dir;
                else if(Character.isJavaIdentifierPart(currChar)){
                    identifier = true;
                    whitespace = false;
                    currX += dir;
                } else
                    return currX;

            } else if(identifier){

                if(Character.isJavaIdentifierPart(currChar))
                    currX += dir;
                else
                    return currX;
            }
        }

        return currX;
    }
    //endregion

    //region cursor logic
    public void moveCursorX(int amount){
        if(cursorX == 0 && amount < 0){
            int oldY = cursorY;
            cursorY--;
            clampY();
            if(cursorY != oldY)
                cursorX = getLine().length();

        } else if(cursorX == getLine().length() && amount > 0){
            int oldY = cursorY;
            cursorY++;
            clampY();
            if(cursorY != oldY)
                cursorX = 0;
        } else {
            cursorX += amount;
            clampX();
        }

    }

    public void moveCursorY(int amount){
        cursorY += amount;
        clampY();
    }

    public void clampX(){
        cursorX = Common.clamp(0, getLine().length(), cursorX);
    }

    public void clampY(){
        cursorY = Common.clamp(0, lines.size() - 1, cursorY);
        clampX();
    }
    //endregion

    public boolean inLine(int index){
        return index >= 0 && index < getLine().length();
    }

    public char safeGetChar(int index){
        return inLine(index) ? getLine().charAt(index) : '\0';
    }
}
