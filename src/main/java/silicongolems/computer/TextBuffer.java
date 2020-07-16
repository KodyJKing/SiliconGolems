package silicongolems.computer;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import silicongolems.util.Util;

import java.util.Arrays;
import java.util.List;

public class TextBuffer {
    public char[] data = null;
    private int shift = 0;
    public int width;
    public int height;

    public TextBuffer() {}
    public TextBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        data = new char[width * height];
    }

    // region index logic
    private int lineIndex(int y) {
        return Util.mod(y + shift, height);
    }

    private int index(int x, int y) {
        return lineIndex(y) * width + x;
    }
    // endregion

    // region network serialization
    public void fromBytes(ByteBuf buf) {
        shift = buf.readInt();
        width = buf.readInt();
        height = buf.readInt();
        String text = ByteBufUtils.readUTF8String(buf);
        data = text.toCharArray();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(shift);
        buf.writeInt(width);
        buf.writeInt(height);
        String text = String.copyValueOf(data);
        ByteBufUtils.writeUTF8String(buf, text);
    }
    // endregion

    // region API
    public void setShift(int shift) {
        this.shift = Util.mod(shift, height);
    }

    public int getShift() {
        return this.shift;
    }

    public String getLine(int y) {
        return String.copyValueOf(data, index(0, y), width);
    }

    public void setLine(int y, String text) {
        int firstOfBottom = index(0, y);
        for (int i = 0; i < text.length(); i++)
            data[firstOfBottom + i] = text.charAt(i);
        for (int i = text.length(); i < width; i++)
            data[firstOfBottom + i] = 0;
    }
    // endregion
}
