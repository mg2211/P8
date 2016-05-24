package helper;
        import android.text.Layout;
        import android.text.StaticLayout;
        import android.text.TextPaint;

        import java.util.ArrayList;
        import java.util.List;


/**
 * Pagination class
 * Used for dividing text into several pages
 */
public class Pagination {
    private final boolean mIncludePad = true;
    private final int mWidth;
    private final int mHeight;
    private final float mSpacingMult = 1;
    private final float mSpacingAdd = 15;
    private final CharSequence mText;
    private final TextPaint mPaint;
    private final List<CharSequence> mPages;

    /**
     * Constructor
     * @param text
     * @param pageW
     * @param pageH
     * @param paint
     */
    public Pagination(CharSequence text, int pageW, int pageH, TextPaint paint) {
        this.mText = text;
        this.mWidth = pageW;
        this.mHeight = pageH;
        this.mPaint = paint;

        this.mPages = new ArrayList<>();

        layout();
    }

    /**
     * Creating the layout for containing the page
     */
    private void layout() {
        /*Creating the layout container*/
        final StaticLayout layout = new StaticLayout(mText, mPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, mIncludePad);

        /*Getting the lines of text in the layout*/
        final int lines = layout.getLineCount();
        /*Getting text from the layout*/
        final CharSequence text = layout.getText();
        /*Setting the offset*/
        int startOffset = 0;
        int height = mHeight;

        /*iterating the lines*/
        for (int i = 0; i < lines; i++) {
            if (height < layout.getLineBottom(i)) {
                // When the layout height has been exceeded
                addPage(text.subSequence(startOffset, layout.getLineStart(i)));
                startOffset = layout.getLineStart(i);
                height = layout.getLineTop(i) + mHeight;
            }

            if (i == lines - 1) {
                // Put the rest of the text into the last page
                addPage(text.subSequence(startOffset, layout.getLineEnd(i)));
                return;
            }
        }
    }

    /*Adds the page to the ArrayList of pages*/
    private void addPage(CharSequence text) {
        mPages.add(text);
    }

    /*Getting the number of pages*/
    public int size() {
        return mPages.size();
    }

    /*Getting text for a specific page*/
    public CharSequence get(int index) {
        return (index >= 0 && index < mPages.size()) ? mPages.get(index) : null;
    }
}