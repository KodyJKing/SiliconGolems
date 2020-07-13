package silicongolems.computer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import silicongolems.util.Util;

import java.util.List;

public class TextBuffer {
    public char[] data = null;
    private int rotation = 0;
    public int width;
    public int height;

    public TextBuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public TextBuffer init() {
        data = new char[width * height];
        return this;
    }

    // region index logic
    private int firstIndexOfLine(int line) {
        return line * width;
    }

    private int lineIndex(int y) {
        return Util.mod(y + rotation, height);
    }

    private int index(int x, int y) {
        return firstIndexOfLine(lineIndex(y)) + x;
    }
    // endregion

    // region buffer shifting
    private void shiftDown() {
        rotation--;
        wrapAround();
    }

    private void shiftUp() {
        rotation++;
        wrapAround();
    }

    private void wrapAround() {
        rotation = Util.mod(rotation, height);
    }
    // endregion

    // region access
    public char charAt(int x, int y) {
        return data[index(x, y)];
    }

    public String lineAt(int y) {
        return String.copyValueOf(data, index(0, y), width);
    }

    public void clear() {
        for (int i = 0; i < data.length; i++)
            data[i] = '\u0000';
    }

    public void print(String text) {
        List<String> lines = Util.printableLines(text, width);
        for (String line: lines)
            printLine(line);
    }

    public void printLine(String text) {
        shiftUp();
        int firstOfBottom = index(0, height - 1);
        for (int i = 0; i < text.length(); i++)
            data[firstOfBottom + i] = text.charAt(i);
        for (int i = text.length(); i < width; i++)
            data[firstOfBottom + i] = 0;
    }
    // endregion

    // region networking
    public void fromBytes(ByteBuf buf) {
        rotation = buf.readInt();
        String text = ByteBufUtils.readUTF8String(buf);
        data = text.toCharArray();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(rotation);
        String text = String.copyValueOf(data);
        ByteBufUtils.writeUTF8String(buf, text);
    }
    // endregion
}
