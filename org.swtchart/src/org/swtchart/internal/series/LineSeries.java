package org.swtchart.internal.series;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.Range;
import org.swtchart.internal.Util;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressLineSeries;
import org.swtchart.internal.compress.CompressScatterSeries;

/**
 * Line series.
 */
public class LineSeries extends Series implements ILineSeries {

    /** the symbol size in pixel */
    private int symbolSize;

    /** the symbol color */
    private Color symbolColor;

    /** the symbol colors */
    private Color[] symbolColors;
    
    /** the symbol type */
    private PlotSymbolType symbolType;

    /** the line style */
    private LineStyle lineStyle;

    /** the line color */
    private Color lineColor;

    /** the state indicating if area chart is enabled */
    private boolean areaEnabled;

    /** the state indicating if step chart is enabled */
    private boolean stepEnabled;

    /** the alpha value to draw area */
    private static final int ALPHA = 50;

    /** the default line style */
    private static final LineStyle DEFAULT_LINE_STYLE = LineStyle.SOLID;

    /** the default line color */
    private static final RGB DEFAULT_LINE_COLOR = Constants.BLUE;

    /** the default symbol color */
    private static final RGB DEFAULT_COLOR = Constants.DARK_GRAY;

    /** the default symbol size */
    private static final int DEFAULT_SIZE = 4;

    /** the default symbol type */
    private static final PlotSymbolType DEFAULT_SYMBOL_TYPE = PlotSymbolType.CIRCLE;

    /** the margin in pixels attached at the minimum/maximum plot */
    private static final int MARGIN_AT_MIN_MAX_PLOT = 6;

    /**
     * Constructor.
     */
    protected LineSeries(Chart chart, String id) {
        super(chart, id);

        symbolSize = 4;
        symbolColor = new Color(Display.getDefault(), DEFAULT_COLOR);
        symbolType = DEFAULT_SYMBOL_TYPE;

        lineStyle = DEFAULT_LINE_STYLE;
        lineColor = new Color(Display.getDefault(), DEFAULT_LINE_COLOR);

        areaEnabled = false;
        
        compressor = new CompressLineSeries();
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getLineStyle()
     */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setLineStyle(org.swtchart.LineStyle)
     */
    public void setLineStyle(LineStyle style) {
        if (style == null) {
            this.lineStyle = DEFAULT_LINE_STYLE;
        } else {
            this.lineStyle = style;
            if (compressor instanceof CompressScatterSeries) {
                ((CompressScatterSeries) compressor)
                        .setLineVisible(style != LineStyle.NONE);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getLineColor()
     */
    public Color getLineColor() {
        return lineColor;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setLineColor(org.eclipse.swt.graphics.Color)
     */
    public void setLineColor(Color color) {
        if (color != null && color.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        if (color == null) {
            this.lineColor = new Color(Display.getDefault(), DEFAULT_LINE_COLOR);
        } else {
            this.lineColor = color;
        }
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getSymbolType()
     */
    public PlotSymbolType getSymbolType() {
        return symbolType;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setSymbolType(org.swtchart.ILineSeries.PlotSymbolType)
     */
    public void setSymbolType(PlotSymbolType type) {
        if (type == null) {
            this.symbolType = DEFAULT_SYMBOL_TYPE;
        } else {
            this.symbolType = type;
        }
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getSymbolSize()
     */
    public int getSymbolSize() {
        return symbolSize;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setSymbolSize(int)
     */
    public void setSymbolSize(int size) {
        if (size <= 0) {
            size = DEFAULT_SIZE;
        }
        this.symbolSize = size;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getSymbolColor()
     */
    public Color getSymbolColor() {
        return symbolColor;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setSymbolColor(org.eclipse.swt.graphics.Color)
     */
    public void setSymbolColor(Color color) {
        if (color != null && color.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        if (color == null) {
            this.symbolColor = new Color(Display.getDefault(), DEFAULT_COLOR);
        } else {
            this.symbolColor = color;
        }
    }
    
    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#getSymbolColors()
     */
    public Color[] getSymbolColors() {
        if (symbolColors == null) {
            return null;
        }

        Color[] copiedSymbolColors = new Color[symbolColors.length];
        System.arraycopy(symbolColors, 0, copiedSymbolColors, 0, symbolColors.length);

        return copiedSymbolColors;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#setSymbolColors(org.eclipse.swt.graphics.Color[])
     */
    public void setSymbolColors(Color[] colors) {
        if (colors == null) {
            symbolColors = null;
            return;
        }

        for (Color color : colors) {
            if (color.isDisposed()) {
                SWT.error(SWT.ERROR_INVALID_ARGUMENT);
            }
        }

        symbolColors = new Color[colors.length];
        System.arraycopy(colors, 0, symbolColors, 0, colors.length);
    }

    /* (non-Javadoc)
     * @see org.swtchart.internal.series.Series#setCompressor()
     */
    protected void setCompressor() {
        if (isXMonotoneIncreasing) {
            compressor = new CompressLineSeries();
        } else {
            compressor = new CompressScatterSeries();
            ((CompressScatterSeries) compressor)
                    .setLineVisible(getLineStyle() != LineStyle.NONE);
        }
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#enableArea(boolean)
     */
    public void enableArea(boolean enabled) {
        areaEnabled = enabled;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#isAreaEnabled()
     */
    public boolean isAreaEnabled() {
        return areaEnabled;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#enableStep(boolean)
     */
    public void enableStep(boolean enabled) {
        stepEnabled = enabled;
    }

    /* (non-Javadoc)
     * @see org.swtchart.ILineSeries#isStepEnabled()
     */
    public boolean isStepEnabled() {
        return stepEnabled;
    }

    /* (non-Javadoc)
     * @see org.swtchart.internal.series.Series#getXRangeToDraw(boolean)
     */
    public Range getXRangeToDraw(boolean isLogScale) {
        int length = chart.getPlotArea().getSize().x;
        return getRangeToDraw(isLogScale, getXRange(), length);
    }

    /* (non-Javadoc)
     * @see org.swtchart.internal.series.Series#getYRangeToDraw(boolean)
     */
    public Range getYRangeToDraw(boolean isLogScale) {
        int length = chart.getPlotArea().getSize().y;
        return getRangeToDraw(isLogScale, getYRange(), length);
    }

    /**
     * Gets the range to draw.
     * 
     * @param isLogScale
     *            the state indicating if axis is log scale
     * @param range
     *            the range
     * @param length
     *            the axis length in pixels
     * @return the range to draw
     */
    private Range getRangeToDraw(boolean isLogScale, Range range, int length) {
        if (length <= 0) {
            return range;
        }
        
        double lowerPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;
        double upperPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;

        double upper;
        double lower;
        if (isLogScale) {
            double digitMax = Math.log10(range.upper);
            double digitMin = Math.log10(range.lower);

            // log(upper) - log(max) = 10 * (log(max) - log(min)) / width
            upper = Math.pow(10, digitMax + upperPlotMargin
                    * ((digitMax - digitMin) / length));

            // log(min) - log(lower) = 10 * (log(max) - log(min)) / width
            lower = Math.pow(10, digitMin - lowerPlotMargin
                    * ((digitMax - digitMin) / length));
        } else {
            lower = range.lower - (range.upper - range.lower)
                    * (lowerPlotMargin / length);
            upper = range.upper + (range.upper - range.lower)
                    * (upperPlotMargin / length);
        }
        return new Range(lower, upper);
    }

    /* (non-Javadoc)
     * @see org.swtchart.internal.series.Series#draw(org.eclipse.swt.graphics.GC, int, int, org.swtchart.internal.axis.Axis, org.swtchart.internal.axis.Axis)
     */
    protected void draw(GC gc, int width, int height, Axis xAxis, Axis yAxis) {
        if (xAxis.isValidCategoryAxis()) {
            if (lineStyle != LineStyle.NONE) {
                drawLineAndAreaOnCategoryAxis(gc, width, height, xAxis, yAxis);
            }
            if (getSymbolType() != PlotSymbolType.NONE) {
                drawSymbolsOnCategoryAxis(gc, width, height, xAxis, yAxis);
            }
        } else {
            if (lineStyle != LineStyle.NONE) {
                drawLineAndArea(gc, width, height, xAxis, yAxis);
            }
            if (getSymbolType() != PlotSymbolType.NONE) {
                drawSymbols(gc, width, height, xAxis, yAxis);
            }
        }
    }

    /**
     * Draws the line and area.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     * @param xAxis
     *            the x axis
     * @param yAxis
     *            the y axis
     */
    private void drawLineAndArea(GC gc, int width, int height, Axis xAxis,
            Axis yAxis) {
        boolean isHorizontal = xAxis.isHorizontalAxis();
        Range hRange = isHorizontal ? xAxis.getRange() : yAxis.getRange();
        Range vRange = isHorizontal ? yAxis.getRange() : xAxis.getRange();

        double[] hSeries = getHorizontalSeries(isHorizontal);
        double[] vSeries = getVerticalSeries(isHorizontal);

        boolean isHAxisLogScale = isHorizontal ? xAxis.isLogScaleEnabled()
                : yAxis.isLogScaleEnabled();
        boolean isVAxisLogScale = isHorizontal ? yAxis.isLogScaleEnabled()
                : xAxis.isLogScaleEnabled();

        gc.setLineStyle(Util.getIndexDefinedInSWT(lineStyle));
        gc.setForeground(lineColor);
        for (int i = 0; i < hSeries.length - 1; i++) {
            if (isVAxisLogScale && i + (int) hRange.lower + 1 > ySeries.length) {
                break;
            }

            LinePoints p = getLinePoints(isHAxisLogScale, isVAxisLogScale,
                    isHorizontal, hRange, vRange, hSeries, vSeries, width,
                    height, i);

            // draw line
            if (stepEnabled) {
                if (isHorizontal) {
                    gc.drawLine(p.x1, p.y1, p.x2, p.y1);
                    gc.drawLine(p.x2, p.y1, p.x2, p.y2);
                } else {
                    gc.drawLine(p.x1, p.y1, p.x1, p.y2);
                    gc.drawLine(p.x1, p.y2, p.x2, p.y2);
                }
            } else {
                gc.drawLine(p.x1, p.y1, p.x2, p.y2);
            }
            
            // draw area
            if (areaEnabled) {
                drawArea(gc, p, isHorizontal);
            }
        }
    }
    
    /**
     * Gets the horizontal series.
     * 
     * @param isHorizontal
     *            the state indicating if the chart is horizontal orientation
     * @return the horizontal series
     */
    private double[] getHorizontalSeries(boolean isHorizontal) {
        if (isHorizontal) {
            return compressor.getCompressedXSeries();
        } else {
            return compressor.getCompressedYSeries();
        }
    }
    
    /**
     * Gets the vertical series.
     * 
     * @param isHorizontal
     *            the state indicating if the chart is horizontal orientation
     * @return the horizontal series
     */
    private double[] getVerticalSeries(boolean isHorizontal) {
        if (isHorizontal) {
            return compressor.getCompressedYSeries();
        } else {
            return compressor.getCompressedXSeries();
        }
    }
    
    /**
     * Gets the line points to draw line and area.
     * 
     * @return the line points
     */
    private LinePoints getLinePoints(boolean isHAxisLogScale,
            boolean isVAxisLogScale, boolean isHorizontal, Range hRange,
            Range vRange, double[] hSeries, double[] vSeries, int width,
            int height, int i) {

        int h1;
        int h2;
        if (isHAxisLogScale) {
            double digitMax = Math.log10(hRange.upper);
            double digitMin = Math.log10(hRange.lower);
            h1 = (int) ((Math.log10(hSeries[i]) - digitMin)
                    / (digitMax - digitMin) * width);
            h2 = (int) ((Math.log10(hSeries[i + 1]) - digitMin)
                    / (digitMax - digitMin) * width);
        } else {
            h1 = (int) ((hSeries[i] - hRange.lower)
                    / (hRange.upper - hRange.lower) * width);
            h2 = (int) ((hSeries[i + 1] - hRange.lower)
                    / (hRange.upper - hRange.lower) * width);
        }

        int v1;
        int v2;
        if (isVAxisLogScale) {
            double digitMax = Math.log10(vRange.upper);
            double digitMin = Math.log10(vRange.lower);
            v1 = (int) ((Math.log10(vSeries[i]) - digitMin)
                    / (digitMax - digitMin) * height);
            v2 = (int) ((Math.log10(vSeries[i + 1]) - digitMin)
                    / (digitMax - digitMin) * height);
        } else {
            v1 = (int) ((vSeries[i] - vRange.lower)
                    / (vRange.upper - vRange.lower) * height);
            v2 = (int) ((vSeries[i + 1] - vRange.lower)
                    / (vRange.upper - vRange.lower) * height);
        }

        int v0;
        if (isHorizontal) {
            if (isVAxisLogScale) {
                v0 = 0;
            } else {
                v0 = (int) ((0 - vRange.lower) / (vRange.upper - vRange.lower) * height);
            }
            return new LinePoints(h1, height - v1, h2, height - v2, h2, height
                    - v0, h1, height - v0);
        } else {
            if (isHAxisLogScale) {
                v0 = 0;
            } else {
                v0 = (int) ((0 - hRange.lower) / (hRange.upper - hRange.lower) * width);
            }
            return new LinePoints(h1, height - v1, h2, height - v2, v0, height
                    - v2, v0, height - v1);
        }
    }
    
    /**
     * Draws line series on category axis.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     * @param xAxis
     *            the x axis
     * @param yAxis
     *            the y axis
     */
    private void drawLineAndAreaOnCategoryAxis(GC gc, int width, int height,
            Axis xAxis, Axis yAxis) {
        boolean isHorizontal = xAxis.isHorizontalAxis();
        boolean isLogScale = yAxis.isLogScaleEnabled();

        Range xRange = xAxis.getRange();
        Range yRange = yAxis.getRange();

        int xWidth = isHorizontal? width : height;
        int yWidth = isHorizontal? height : width;

        gc.setLineStyle(Util.getIndexDefinedInSWT(lineStyle));
        gc.setForeground(lineColor);
        for (int i = (int) xRange.lower - 1; i < (int) xRange.upper + 1; i++) {
            if (i < 0) {
                continue;
            }
            if (i >= ySeries.length - 1) {
                break;
            }
            if (isLogScale && i + (int) xRange.lower + 1 > ySeries.length) {
                break;
            }

            LinePoints p = getLinePointsOnCategoryAxis(isLogScale, isHorizontal, xRange,
                    yRange, xWidth, yWidth, i);

            // draw line
            if (stepEnabled) {
                if (isHorizontal) {
                    gc.drawLine(p.x1, p.y1, p.x2, p.y1);
                    gc.drawLine(p.x2, p.y1, p.x2, p.y2);
                } else {
                    gc.drawLine(p.x1, p.y1, p.x1, p.y2);
                    gc.drawLine(p.x1, p.y2, p.x2, p.y2);
                }
            } else {
                gc.drawLine(p.x1, p.y1, p.x2, p.y2);
            }

            // fill area
            if (areaEnabled) {
                drawArea(gc, p, isHorizontal);
            }
        }
    }

    /**
     * Gets the line points to draw line and area on category axis.
     * 
     * @return the line points
     */
    private LinePoints getLinePointsOnCategoryAxis(boolean isLogScale,
            boolean isHorizontal, Range xRange, Range yRange, int xWidth,
            int yWidth, int i) {

        int x1 = (int) ((i + 0.5 - (int) xRange.lower)
                / ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);
        int x2 = (int) ((i + 1.5 - (int) xRange.lower)
                / ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);

        int y1;
        int y2;
        if (isLogScale) {
            double digitMax = Math.log10(yRange.upper);
            double digitMin = Math.log10(yRange.lower);
            y1 = (int) ((Math.log10(ySeries[i]) - digitMin)
                    / (digitMax - digitMin) * yWidth);
            y2 = (int) ((Math.log10(ySeries[i + 1]) - digitMin)
                    / (digitMax - digitMin) * yWidth);
        } else if (stackEnabled) {
            y1 = (int) ((stackSeries[i] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
            y2 = (int) ((stackSeries[i + 1] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
        } else {
            y1 = (int) ((ySeries[i] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
            y2 = (int) ((ySeries[i + 1] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
        }

        if (isLogScale) {
            if (isHorizontal) {
                return new LinePoints(x1, yWidth - y1, x2, yWidth - y2, x2,
                        yWidth - 0, x1, yWidth - 0);
            } else {
                return new LinePoints(y1, xWidth - x1, y2, xWidth - x2, 0,
                        xWidth - x2, 0, xWidth - x1);
            }
        } else if (stackEnabled) {
            int v3 = (int) ((stackSeries[i + 1] - ySeries[i + 1] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
            int v4 = (int) ((stackSeries[i] - ySeries[i] - yRange.lower)
                    / (yRange.upper - yRange.lower) * yWidth);
            if (isHorizontal) {
                return new LinePoints(x1, yWidth - y1, x2, yWidth - y2, x2,
                        yWidth - v3, x1, yWidth - v4);
            } else {
                return new LinePoints(y1, xWidth - x1, y2, xWidth - x2, v3,
                        xWidth - x2, v4, xWidth - x1);
            }
        } else {
            int v0 = (int) ((0 - yRange.lower) / (yRange.upper - yRange.lower) * yWidth);
            if (isHorizontal) {
                return new LinePoints(x1, yWidth - y1, x2, yWidth - y2, x2,
                        yWidth - v0, x1, yWidth - v0);
            } else {
                return new LinePoints(y1, xWidth - x1, y2, xWidth - x2, v0,
                        xWidth - x2, v0, xWidth - x1);
            }
        }
    }

    /**
     * Draws the area.
     * 
     * @param gc
     *            the graphic context
     * @param p
     *            the line points
     */
    private void drawArea(GC gc, LinePoints p, boolean isHorizontal) {
        int alpha = gc.getAlpha();
        gc.setAlpha(ALPHA);
        gc.setBackground(lineColor);

        int[] pointArray;
        if (stepEnabled) {
            if (isHorizontal) {
                pointArray = new int[] { p.x1, p.y1, p.x2, p.y1, p.x3, p.y4,
                        p.x4, p.y4, p.x1, p.y1 };
            } else {
                pointArray = new int[] { p.x1, p.y1, p.x1, p.y2, p.x4, p.y3,
                        p.x4, p.y4, p.x1, p.y1 };
            }
        } else {
            pointArray = new int[] { p.x1, p.y1, p.x2, p.y2, p.x3, p.y3, p.x4,
                    p.y4, p.x1, p.y1 };
        }

        gc.fillPolygon(pointArray);
        gc.setAlpha(alpha);
    }

    /**
     * Draws series symbol.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     * @param xAxis
     *            the x axis
     * @param yAxis
     *            the y axis
     */
    private void drawSymbols(GC gc, int width, int height, Axis xAxis,
            Axis yAxis) {

        boolean isHorizontal = xAxis.isHorizontalAxis();
        Range hRange = isHorizontal ? xAxis.getRange() : yAxis.getRange();
        Range vRange = isHorizontal ? yAxis.getRange() : xAxis.getRange();

        double[] hSeries = getHorizontalSeries(isHorizontal);
        double[] vSeries = getVerticalSeries(isHorizontal);

        boolean isHLogScale = isHorizontal ? xAxis.isLogScaleEnabled() : yAxis
                .isLogScaleEnabled();
        boolean isVLogScale = isHorizontal ? yAxis.isLogScaleEnabled() : xAxis
                .isLogScaleEnabled();
    
        for (int i = 0; i < hSeries.length; i++) {
            int h;
            if (isHLogScale) {
                double digitMax = Math.log10(hRange.upper);
                double digitMin = Math.log10(hRange.lower);
                h = (int) ((Math.log10(hSeries[i]) - digitMin)
                        / (digitMax - digitMin) * width);
            } else {
                h = (int) ((hSeries[i] - hRange.lower)
                        / (hRange.upper - hRange.lower) * width);
            }
    
            int v;
            if (isVLogScale) {
                double digitMax = Math.log10(vRange.upper);
                double digitMin = Math.log10(vRange.lower);
                v = (int) ((Math.log10(vSeries[i]) - digitMin)
                        / (digitMax - digitMin) * height);
            } else {
                v = (int) ((vSeries[i] - vRange.lower)
                        / (vRange.upper - vRange.lower) * height);
            }
    
            Color color = symbolColor;
            if (symbolColors != null && symbolColors.length > i) {
                color = symbolColors[i];
            }
            drawSeriesSymbol(gc, h, height - v, color);
            ((SeriesLabel) seriesLabel).draw(gc, h, height - v, vSeries[i]);
        }
    }

    /**
     * Draws series symbol.
     * 
     * @param gc
     *            the GC object
     * @param h
     *            the horizontal coordinate to draw symbol
     * @param v
     *            the vertical coordinate to draw symbol
     * @param color
     *            the symbol color
     */
    public void drawSeriesSymbol(GC gc, int h, int v, Color color) {
        gc.setAntialias(SWT.ON);
        gc.setForeground(color);
        gc.setBackground(color);
    
        switch (symbolType) {
        case CIRCLE:
            gc.fillOval(h - symbolSize, v - symbolSize, symbolSize * 2,
                    symbolSize * 2);
            break;
        case SQUARE:
            gc.fillRectangle(h - symbolSize, v - symbolSize, symbolSize * 2,
                    symbolSize * 2);
            break;
        case DIAMOND:
            int[] diamondArray = { h, v - symbolSize, h + symbolSize, v, h,
                    v + symbolSize, h - symbolSize, v };
            gc.fillPolygon(diamondArray);
            break;
        case TRIANGLE:
            int[] triangleArray = { h, v - symbolSize, h + symbolSize,
                    v + symbolSize, h - symbolSize, v + symbolSize };
            gc.fillPolygon(triangleArray);
            break;
        case INVERTED_TRIANGLE:
            int[] invertedTriangleArray = { h, v + symbolSize, h + symbolSize,
                    v - symbolSize, h - symbolSize, v - symbolSize };
            gc.fillPolygon(invertedTriangleArray);
            break;
        case CROSS:
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.drawLine(h - symbolSize, v - symbolSize, h + symbolSize, v
                    + symbolSize);
            gc.drawLine(h - symbolSize, v + symbolSize, h + symbolSize, v
                    - symbolSize);
            break;
        case PLUS:
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.drawLine(h, v - symbolSize, h, v + symbolSize);
            gc.drawLine(h - symbolSize, v, h + symbolSize, v);
            break;
        case NONE:
        default:
            break;
        }
        gc.setAntialias(SWT.OFF);
    }

    /**
     * Draws series symbol on category axis.
     * 
     * @param gc
     *            the graphics context
     * @param width
     *            the width to draw series
     * @param height
     *            the height to draw series
     * @param xAxis
     *            the x axis
     * @param yAxis
     *            the y axis
     */
    private void drawSymbolsOnCategoryAxis(GC gc, int width, int height,
            Axis xAxis, Axis yAxis) {
        boolean isHorizontal = xAxis.isHorizontalAxis();
        boolean isLogScale = yAxis.isLogScaleEnabled();

        Range xRange = xAxis.getRange();
        Range yRange = yAxis.getRange();

        int xWidth = isHorizontal? width : height;
        int yWidth = isHorizontal? height : width;

        for (int i = (int) xRange.lower; i < (int) xRange.upper + 1; i++) {
            if (i >= ySeries.length) {
                break;
            }
            int x = (int) ((i - (int) xRange.lower + 0.5)
                    / ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);

            int y;
            if (isLogScale) {
                double digitMax = Math.log10(yRange.upper);
                double digitMin = Math.log10(yRange.lower);
                y = (int) ((Math.log10(ySeries[i]) - digitMin)
                        / (digitMax - digitMin) * yWidth);
            } else if (stackEnabled) {
                y = (int) ((stackSeries[i] - yRange.lower)
                        / (yRange.upper - yRange.lower) * yWidth);
            } else {
                y = (int) ((ySeries[i] - yRange.lower)
                        / (yRange.upper - yRange.lower) * yWidth);
            }

            Color color = symbolColor;
            if (symbolColors != null && symbolColors.length > i) {
                color = symbolColors[i];
            }

            if (isHorizontal) {
                drawSeriesSymbol(gc, x, height - y, color);
                ((SeriesLabel) seriesLabel).draw(gc, x, height - y, ySeries[i]);
            } else {
                drawSeriesSymbol(gc, y, height - x, color);
                ((SeriesLabel) seriesLabel).draw(gc, y, height - x, ySeries[i]);
            }
        }
    }
    
    /**
     * The line points to draw either a line between plots or an area.
     */
    private static class LinePoints {
        
        /** the x coordinate of first point of line */
        public int x1;
        
        /** the y coordinate of first point of line */
        public int y1;
        
        /** the x coordinate of second point of line */
        public int x2;
        
        /** the y coordinate of second point of line */
        public int y2;
        
        /** the x coordinate of third point of line to draw area */
        public int x3;

        /** the y coordinate of third point of line to draw area */
        public int y3;

        /** the x coordinate of fourth point of line to draw area */
        public int x4;
        
        /** the y coordinate of fourth point of line to draw area */
        public int y4; 
        
        /**
         * The constructor.
         */
        public LinePoints(int x1, int y1, int x2, int y2, int x3, int y3,
                int x4, int y4) {
            this(x1, y1, x2, y2);
            this.x3 = x3;
            this.y3 = y3;
            this.x4 = x4;
            this.y4 = y4;
        }
        
        /**
         * The constructor.
         */
        public LinePoints(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
