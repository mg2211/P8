package callback;

import java.util.HashMap;

/**
 * Created by ida803f16
 */
/*Interface Callback
* Used when passing async results back to caller activity*/
public interface Callback {
    /**
     *
     * @param asyncResults - the HashMap of results
     */
    void asyncDone (HashMap<String, HashMap<String, String>> asyncResults);
}
