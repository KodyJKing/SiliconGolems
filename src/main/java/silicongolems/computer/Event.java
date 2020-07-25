package silicongolems.computer;

import org.graalvm.polyglot.HostAccess;
import silicongolems.util.Util;

public class Event {
    @HostAccess.Export
    public String type;
    public Event(String type) {  this.type = type;  }
    @Override
    @HostAccess.Export
    public String toString() {  return Util.gson.toJson(this); }
}
