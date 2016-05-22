package helper;

/**
 * Created by Brandur on 5/16/2016.
 */

        import android.text.Layout;
        import android.text.StaticLayout;
        import android.text.TextPaint;

        import java.util.ArrayList;
        import java.util.List;


public class Pagination {
    private final boolean mIncludePad = true;
    private final int mWidth;
    private final int mHeight;
    final float mSpacingMult = 1;
    final float mSpacingAdd = 10; // number of pages to possible add
    private final CharSequence mText;
    private final TextPaint mPaint;
    private final List<CharSequence> mPages;

    public Pagination(CharSequence text, int pageW, int pageH, TextPaint paint) {
        this.mText = text;
        this.mWidth = pageW;
        this.mHeight = pageH;
        this.mPaint = paint;

        this.mPages = new ArrayList<CharSequence>();

        layout();
    }

    private void layout() {
        final StaticLayout layout = new StaticLayout(mText, mPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, mIncludePad);

        final int lines = layout.getLineCount();
        final CharSequence text = layout.getText();
        int startOffset = 0;
        int height = mHeight;

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

    private void addPage(CharSequence text) {
        mPages.add(text);
    }

    public int size() {
        return mPages.size();
    }

    public CharSequence get(int index) {
        return (index >= 0 && index < mPages.size()) ? mPages.get(index) : null;
    }
}