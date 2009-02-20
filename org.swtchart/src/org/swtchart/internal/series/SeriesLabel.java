/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.series;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Constants;
import org.swtchart.ISeriesLabel;

/**
 * A series label.
 */
public class SeriesLabel implements ISeriesLabel {

    /** the visibility state of series label */
    private boolean isVisible;

    /** the series label font */
    protected Font font;

    /** the series label color */
    protected Color color;

    /** the format for series label */
    private String format;

    /** the default label color */
    private static final RGB DEFAULT_COLOR = Constants.BLACK;

    /** the default label format */
    private static final String DEFAULT_FORMAT = "############.###########";

    /** the default font */
    private static final int DEFAULT_FONT_SIZE = Constants.SMALL_FONT_SIZE;

    /**
     * Constructor.
     */
    public SeriesLabel() {
        font = new Font(Display.getDefault(), "Tahoma", DEFAULT_FONT_SIZE,
                SWT.NORMAL);
        color = new Color(Display.getDefault(), DEFAULT_COLOR);
        isVisible = false;
        format = DEFAULT_FORMAT;
    }

    /*
     * @see ISeriesLabel#getFormat()
     */
    public String getFormat() {
        return format;
    }

    /*
     * @see ISeriesLabel#setFormat(String)
     */
    public void setFormat(String format) {
        if (format == null) {
            this.format = DEFAULT_FORMAT;
        } else {
            this.format = format;
        }
    }

    /*
     * @see ISeriesLabel#getForeground()
     */
    public Color getForeground() {
        return color;
    }

    /*
     * @see ISeriesLabel#setForeground(Color)
     */
    public void setForeground(Color color) {
        if (color != null && color.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        if (color == null) {
            this.color = new Color(Display.getDefault(), DEFAULT_COLOR);
        } else {
            this.color = color;
        }
    }

    /*
     * @see ISeriesLabel#getFont()
     */
    public Font getFont() {
        return font;
    }

    /*
     * @see ISeriesLabel#setFont(Font)
     */
    public void setFont(Font font) {
        if (font != null && font.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        if (font == null) {
            this.font = Display.getDefault().getSystemFont();
        } else {
            this.font = font;
        }
    }

    /*
     * @see ISeriesLabel#isVisible()
     */
    public boolean isVisible() {
        return isVisible;
    }

    /*
     * @see ISeriesLabel#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Draws series label.
     * 
     * @param gc
     *            the GC object
     * @param h
     *            the horizontal coordinate to draw label
     * @param v
     *            the vertical coordinate to draw label
     * @param ySeriesValue
     *            the Y series value
     */
    protected void draw(GC gc, int h, int v, double ySeriesValue) {
        if (!isVisible) {
            return;
        }

        gc.setForeground(color);
        gc.setFont(font);
        String text = new DecimalFormat(format).format(ySeriesValue);
        gc.drawString(text, h, v, true);
    }
}
