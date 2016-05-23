package callback;

/**
 * Interface DatePickerCallback
 * Used for passing date from the datepicker dialog to caller activity
 */
public interface DatePickerCallback {
    /**
     *
     * @param timestamp unix timestamp
     */
    void dateSelected(Long timestamp);
}
