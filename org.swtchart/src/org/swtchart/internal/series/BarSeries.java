/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.series;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IBarSeries;
import org.swtchart.Range;
import org.swtchart.IAxis.Direction;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressBarSeries;
import org.swtchart.internal.compress.CompressScatterSeries;

/**
 * Bar series.
 */
public class BarSeries extends Series implements IBarSeries {

    /** the riser index in a category */
    private int riserIndex;

    /** the riser color */
    private Color barColor;

    /** the padding */
    private int padding;

    /** the initial bar padding in percentage */
    public static final int INITIAL_PADDING = 20;

    /** the alpha value */
    private static final int ALPHA = 0xD0;

    /** the margin in pixels attached at the minimum/maximum plot */
    private static final int MARGIN_AT_MIN_MAX_PLOT = 6;

    /** the default bar color */
    private static final RGB DEFAULT_BAR_COLOR = Constants.LIGHT_BLUE;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param id
     *            the series id
     */
    protected BarSeries(Chart chart, String id) {
        super(chart, id);

        barColor = new Color(Display.getDefault(), DEFAULT_BAR_COLOR);
        padding = INITIAL_PADDING;
        type = SeriesType.BAR;

        compressor = new CompressBarSeries();
    }

    /*
     * @see IBarSeries#getBarPadding()
     */
    public int getBarPadding() {
        return padding;
    }

    /*
     * @see IBarSeries#setBarPadding(int)
     */
    public void setBarPadding(int padding) {
        if (padding < 0 || padding > 100) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        this.padding = padding;
    }

    /*
     * @see IBarSeries#getBarColor()
     */
    public Color getBarColor() {
        return barColor;
    }

    /*
     * @see IBarSeries#setBarColor(Color)
     */
    public void setBarColor(Color color) {
        if (color != null && color.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        if (color == null) {
            this.barColor = new Color(Display.getDefault(), DEFAULT_BAR_COLOR);
        } else {
            this.barColor = color;
        }
    }

    /**
     * Sets the index of riser in a category.
     * 
     * @param riserIndex
     *            the index of riser in a category
     */
    protected void setRiserIndex(int riserIndex) {
        this.riserIndex = riserIndex;
    }

    /*
     * @see Series#setCompressor()
     */
    @Override
    protected void setCompressor() {
        if (isXMonotoneIncreasing) {
            compressor = new CompressBarSeries();
        } else {
            compressor = new CompressScatterSeries();
        }
    }

    /*
     * @see Series#getAdjustedRange(Axis, int)
     */
    @Override
    public Range getAdjustedRange(Axis axis, int length) {

        // calculate a range which has margin
        Range range;
        int lowerPlotMargin;
        int upperPlotMargin;
        if (axis.getDirection() == Direction.X) {
            double lowerRiserWidth = getRiserWidth(xSeries, 0, axis, minX, maxX);
            double upperRiserWidth = getRiserWidth(xSeries, xSeries.length - 1,
                    axis, minX, maxX);
            lowerPlotMargin = (int) (lowerRiserWidth / 2d + MARGIN_AT_MIN_MAX_PLOT);
            upperPlotMargin = (int) (upperRiserWidth / 2d + MARGIN_AT_MIN_MAX_PLOT);
            range = getXRange();
        } else {
            range = getYRange();
            if (range.upper < 0) {
                range.upper = 0;
            }
            if (range.lower > 0) {
                range.lower = axis.isLogScaleEnabled() ? axis.getRange().lower
                        : 0;
            }
            lowerPlotMargin = (range.lower == 0) ? 0 : MARGIN_AT_MIN_MAX_PLOT;
            upperPlotMargin = (range.upper == 0) ? 0 : MARGIN_AT_MIN_MAX_PLOT;
        }

        return getRangeWithMargin(lowerPlotMargin, upperPlotMargin, length,
                axis, range);
    }

    /*
     * @see Series#draw(GC, int, int, Axis, Axis)
     */
    @Override
    protected void draw(GC gc, int width, int height, Axis xAxis, Axis yAxis) {

        // get x and y series
        double[] xseries, yseries;
        if (xAxis.isValidCategoryAxis()) {
            xseries = new double[xSeries.length];
            for (int i = 0; i < xSeries.length; i++) {
                xseries[i] = i;
            }
            yseries = ySeries;
        } else {
            xseries = compressor.getCompressedXSeries();
            yseries = compressor.getCompressedYSeries();
        }

        // draw risers
        Range xRange = xAxis.getRange();
        Range yRange = yAxis.getRange();
        for (int i = 0; i < xseries.length; i++) {
            double yData = isValidStackSeries() ? stackSeries[i] : yseries[i];
            int x = xAxis.getPixelCoordinate(xseries[i]);
            int y = yAxis.getPixelCoordinate(yData);
            double riserwidth = getRiserWidth(xseries, i, xAxis, xRange.lower,
                    xRange.upper);
            double riserHeight = Math.abs(yAxis.getPixelCoordinate(yseries[i],
                    yRange.lower, yRange.upper)
                    - yAxis.getPixelCoordinate(
                            yAxis.isLogScaleEnabled() ? yRange.lower : 0,
                            yRange.lower, yRange.upper));

            // adjust riser x coordinate and riser width for multiple series
            int riserCnt = xAxis.getNumRisers();
            if (riserCnt > 1) {
                x = (int) (x - riserwidth / 2d + riserwidth / riserCnt
                        * (riserIndex + 0.5));
                riserwidth /= riserCnt;
            }

            // draw riser
            if (xAxis.isHorizontalAxis()) {

                // adjust coordinate for negative series
                if (y > yAxis.getPixelCoordinate(0)) {
                    y = yAxis.getPixelCoordinate(0);
                }

                drawRiser(gc, x, y, riserwidth, riserHeight);
                ((SeriesLabel) seriesLabel).draw(gc, x,
                        (int) (y + riserHeight / 2d), yData, i, SWT.CENTER);
            } else {

                // adjust coordinate for negative series
                if (y < yAxis.getPixelCoordinate(0)) {
                    y = yAxis.getPixelCoordinate(0);
                }

                drawRiser(gc, y, x, riserwidth, riserHeight);
                ((SeriesLabel) seriesLabel).draw(gc,
                        (int) (y - riserHeight / 2d), x, yData, i, SWT.CENTER);
            }
        }
    }

    /**
     * Gets the riser width.
     * 
     * @param series
     *            the X series
     * @param index
     *            the series index
     * @param xAxis
     *            the X axis
     * @param min
     *            the min value of range
     * @param max
     *            the max value of range
     * @return the raiser width in pixels
     */
    private int getRiserWidth(double[] series, int index, Axis xAxis,
            double min, double max) {

        // get two x coordinates
        double upper;
        double lower;
        if (series.length == 1) {
            upper = series[0] + 0.5;
            lower = series[0] - 0.5;
        } else if (index != series.length - 1
                && (index == 0 || series[index + 1] - series[index] < series[index]
                        - series[index - 1])) {
            upper = series[index + 1];
            lower = series[index];
        } else {
            upper = series[index];
            lower = series[index - 1];
        }

        // get riser width without padding
        int width = Math.abs(xAxis.getPixelCoordinate(upper, min, max)
                - xAxis.getPixelCoordinate(lower, min, max));

        // adjust for padding
        width *= (100 - padding) / 100d;

        // symbol size should be at least more than 1
        if (width == 0) {
            width = 1;
        }

        return width;
    }

    /**
     * Draws symbol.
     * 
     * @param gc
     *            the graphics context
     * @param h
     *            the horizontal coordinate
     * @param v
     *            the vertical coordinate
     * @param riserWidth
     *            the riser width
     * @param riserHeight
     *            the riser height
     */
    private void drawRiser(GC gc, double h, double v, double riserWidth,
            double riserHeight) {
        gc.setBackground(barColor);

        int alpha = gc.getAlpha();
        gc.setAlpha(ALPHA);

        int x, y, width, height;
        if (chart.getOrientation() == SWT.HORIZONTAL) {
            x = (int) Math.floor(h - riserWidth / 2d);
            y = (int) v;
            width = (int) Math.ceil(riserWidth);
            width = (width == 0) ? 1 : width;
            height = (int) riserHeight;
        } else {
            x = (int) (h - riserHeight);
            y = (int) Math.floor(v - riserWidth / 2d);
            width = (int) riserHeight;
            height = (int) Math.ceil(riserWidth);
            height = (height == 0) ? 1 : height;
        }
        gc.fillRectangle(x, y, width, height);

        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(getFrameColor(barColor));
        gc.drawRectangle(x, y, width, height);

        gc.setAlpha(alpha);
    }

    /**
     * Gets the color for riser frame. The color will be darker or lighter than
     * the given color.
     * 
     * @param color
     *            the riser color
     * @return the riser frame color
     */
    private Color getFrameColor(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        red *= (red > 128) ? 0.8 : 1.2;
        green *= (green > 128) ? 0.8 : 1.2;
        blue *= (blue > 128) ? 0.8 : 1.2;

        return new Color(color.getDevice(), red, green, blue);
    }
}
