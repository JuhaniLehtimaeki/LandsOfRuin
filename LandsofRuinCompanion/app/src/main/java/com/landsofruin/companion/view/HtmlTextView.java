package com.landsofruin.companion.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by juhani on 19/05/15.
 */
public class HtmlTextView extends TextView {

    private LinkClickListener mLinkClickListener;

    public HtmlTextView(Context context) {
        super(context);
        setTextHTML(getText().toString());
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextHTML(getText().toString());
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextHTML(getText().toString());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTextHTML(getText().toString());
    }

    protected void addLinkClickListener(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (mLinkClickListener != null) {
                    mLinkClickListener.onLinkClick(span.getURL(), view);
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public void setTextHTML(String html) {
        if (TextUtils.isEmpty(html)) {
            return;
        }
        setLinksClickable(true);
        setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder spannable = new SpannableStringBuilder(sequence);
        URLSpan[] urls = spannable.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            addLinkClickListener(spannable, span);
        }
        setText(spannable);
    }

    public void setLinkClickListener(LinkClickListener linkClickListener) {
        mLinkClickListener = linkClickListener;
    }

    public interface LinkClickListener {

        void onLinkClick(String link, View view);

    }
}
