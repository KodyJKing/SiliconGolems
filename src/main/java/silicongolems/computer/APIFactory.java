package silicongolems.computer;

import com.eclipsesource.v8.V8;

public interface APIFactory {
    Object create(V8 runtime);
}
