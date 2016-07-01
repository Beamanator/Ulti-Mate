package io.scoober.ulti.ulti_mate.CustomViews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by Navin on 6/30/2016.
 */
public class TeamImageButton extends ImageButton{
    private GradientDrawable gd;
    private @ColorInt int color;
    private int size;

    public TeamImageButton(Context context) {
        super(context);
    }

    public TeamImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeamImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Build the TeamImageButton as a circle with the following parameters
     * @param size          Size of the oval GradientDrawable
     * @param color         Color of the oval GradientDrawable
     * @param strokeSize    Size of the stroke surrounding the GradientDrawable
     * @param strokeColor   Color of the stroke surrounding the GradientDrawable
     */
    public void build(int size, @ColorInt int color, int strokeSize, @ColorInt int strokeColor) {
        gd = new GradientDrawable();
        gd.setShape((GradientDrawable.OVAL));
        setColor(color);
        setSize(size);
        if (strokeSize > 0) {
            setStroke(strokeSize, strokeColor);
        }
        setImageDrawable(gd);
    }

    public void build(int size, @ColorInt int color) {
        build(size, color, 0, 0);
    }

    public @ColorInt int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        gd.setColor(color);
    }

    public void setSize(int size) {
        this.size = size;
        gd.setSize(size, size);
    }

    public void setStroke(int strokeSize, @ColorInt int strokeColor) {
        gd.setStroke(strokeSize, strokeColor);
    }

}
