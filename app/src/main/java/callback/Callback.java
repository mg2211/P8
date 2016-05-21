package callback;

import java.util.HashMap;

/**
 * Created by ida803f16
 */
public interface Callback {

    void asyncDone (HashMap<String, HashMap<String, String>> asyncResults);
}
