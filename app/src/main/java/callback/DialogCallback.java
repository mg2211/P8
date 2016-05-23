package callback;

/**
 * Interface DialogCallback
 * used for sending dialog response back to caller activity
 */
public interface DialogCallback {
    /**
     *
     * @param response boolean
     */
    void dialogResponse(boolean response);
}
