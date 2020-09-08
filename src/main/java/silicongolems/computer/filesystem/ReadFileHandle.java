package silicongolems.computer.filesystem;

import org.graalvm.polyglot.HostAccess;

import java.io.IOException;
import java.io.InputStream;

public class ReadFileHandle {
    private InputStream stream;
    public ReadFileHandle(InputStream stream) { this.stream = stream; }
    @HostAccess.Export
    public int read() throws IOException { return stream.read(); }
    @HostAccess.Export
    public void close() throws IOException { stream.close(); }
}
