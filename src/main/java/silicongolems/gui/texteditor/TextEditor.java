package silicongolems.gui.texteditor;

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

    public void type(String str) {
        int lineNum = 0;
        for(String substr : str.split("\n", -1))
        {
            if(lineNum++ > 0)
                newline();
            getLine().insert(cursorX, substr);
            cursorX += substr.length();
        }
    }

    public void newline(){
        splitLine();
        cursorY++;
        cursorX = 0;
    }

    public void splitLine(){
        StringBuilder line = getLine();
        String newLine = line.substring(cursorX);
        line.replace(cursorX, line.length(), "");
        lines.add(cursorY + 1, new StringBuilder(newLine));
    }

    public void backspace(){
        if(cursorX > 0){
            getLine().deleteCharAt(cursorX - 1);
            cursorX--;
        } else {
            mergeLines();
        }
    }

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

    public void ctrlMove(int dir){
        cursorX = ctrlSkip(dir);
        clampX();
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

    public void mergeLines(){
        if(cursorY <= 0)
            return;

        StringBuilder lowerLine = lines.remove(cursorY);
        StringBuilder upperLine = getLine(cursorY - 1);
        cursorX = upperLine.length();

        upperLine.append(lowerLine);
        cursorY--;
    }

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

    public boolean inLine(int index){
        return index >= 0 && index < getLine().length();
    }

    public char safeGetChar(int index){
        return inLine(index) ? getLine().charAt(index) : '\0';
    }
}
