package silicongolems.computer.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AssetFileSystem extends FileSystem {

    @Override
    protected InputStream getInputStream(String path) {
        return AssetFileSystem.class.getResourceAsStream(path);
    }

    @Override
    protected OutputStream getOutputStream(String path) throws IOException {
        throw new IOException("File is read-only.");
    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public boolean isFile(String path) {
        return false;
    }

    @Override
    public List<String> list(String path) {
        return null;
    }

}
