package silicongolems.computer.filesystem;

import org.graalvm.polyglot.HostAccess;
import silicongolems.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadTextFileHandle {
    private BufferedReader reader;
    public ReadTextFileHandle(InputStream stream) {
        InputStreamReader sr = new InputStreamReader(stream);
        this.reader = new BufferedReader(sr);
    }

    @HostAccess.Export
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @HostAccess.Export
    public String readAll() throws IOException {
        return Util.readAll(reader);
    }

    @HostAccess.Export
    public void close() throws IOException {
        reader.close();
    }
}
