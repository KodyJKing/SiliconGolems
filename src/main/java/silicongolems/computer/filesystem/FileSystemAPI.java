package silicongolems.computer.filesystem;

import org.graalvm.polyglot.HostAccess;

import java.io.IOException;
import java.util.List;

public class FileSystemAPI {
    private FileSystem fs;
    public FileSystemAPI(FileSystem fs) { this.fs = fs; }

    @HostAccess.Export
    public Object open(String path, boolean read, boolean append, boolean binary) throws Exception {
        return fs.open(path, read, append, binary);
    }

    @HostAccess.Export
    public boolean exists(String path) throws IOException {
        return fs.exists(path);
    }

    @HostAccess.Export
    public boolean isFile(String path) throws IOException {
        return fs.isFile(path);
    }

    @HostAccess.Export
    public List<String> list(String path) throws IOException {
        return fs.list(path);
    }
}
