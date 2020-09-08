package silicongolems.computer.filesystem;

import org.graalvm.polyglot.HostAccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class FileSystem {

    Object open(String path,  boolean read, boolean append, boolean binary) throws Exception {
        try {
            if (read) {
                InputStream stream = getInputStream(path);
                if (binary) return new ReadFileHandle(stream);
                return new ReadTextFileHandle(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract InputStream getInputStream(String path) throws IOException;
    protected abstract OutputStream getOutputStream(String path) throws IOException;
    abstract boolean exists(String path) throws IOException;
    abstract boolean isFile(String path) throws IOException;
    abstract List<String> list(String path) throws IOException;

}
