/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IBarSeries;
import org.swtchart.ILegend;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.internal.series.LineSeries;
import org.swtchart.internal.series.Series;

/**
 * A legend for chart.
 */
public class Legend extends Canvas implements ILegend, PaintListener {

    /** the plot chart */
    private Chart chart;

    /** the state indicating the legend visibility */
    private boolean visible;

    /** the position of legend */
    private int position;

    /** the margin */
    private static final int MARGIN = 5;

    /** the width of area to draw symbol */
    private static final int SYMBOL_WIDTH = 20;

    /** the line width */
    private static final int LINE_WIDTH = 2;

    /** the default foreground */
    private static final RGB DEFAULT_FOREGROUND = Constants.BLACK;

    /** the default background */
    private static final RGB DEFAULT_BACKGROUND = Constants.WHITE;

    /** the default font */
    private static final int DEFAULT_FONT_SIZE = Constants.SMALL_FONT_SIZE;

    /** the default position */
    private static final int DEFAULT_POSITION = SWT.RIGHT;

    /** the map between series id and cell bounds */
    private Map<String, Rectangle> cellBounds;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     */
    public Legend(Chart chart, int style) {
        super(chart, style);
        this.chart = chart;

        visible = true;
        position = DEFAULT_POSITION;
        cellBounds = new HashMap<String, Rectangle>();
        setFont(new Font(Display.getDefault(), "Tahoma", DEFAULT_FONT_SIZE,
                SWT.NORMAL));
        setForeground(new Color(Display.getDefault(), DEFAULT_FOREGROUND));
        setBackground(new Color(Display.getDefault(), DEFAULT_BACKGROUND));
        addPaintListener(this);
    }

    /*
     * @see Control#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }

        this.visible = visible;
        chart.updateLayout();
    }

    /*
     * @see Control#isVisible()
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /*
     * @see Canvas#setFont(Font)
     */
    @Override
    public void setFont(Font font) {
        if (font == null) {
            super.setFont(new Font(Display.getDefault(), "Tahoma",
                    DEFAULT_FONT_SIZE, SWT.NORMAL));
        } else {
            super.setFont(font);
        }
        chart.updateLayout();
    }

    /*
     * @see Control#setForeground(Color)
     */
    @Override
    public void setForeground(Color color) {
        if (color == null) {
            super.setForeground(new Color(Display.getDefault(),
                    DEFAULT_FOREGROUND));
        } else {
            super.setForeground(color);
        }
    }

    /*
     * @see Control#setBackground(Color)
     */
    @Override
    public void setBackground(Color color) {
        if (color == null) {
            super.setBackground(new Color(Display.getDefault(),
                    DEFAULT_BACKGROUND));
        } else {
            super.setBackground(color);
        }
    }

    /*
     * @see ILegend#getPosition()
     */
    public int getPosition() {
        return position;
    }

    /*
     * @see ILegend#setPosition(int)
     */
    public void setPosition(int value) {
        if (position != SWT.LEFT && position != SWT.RIGHT
                && position != SWT.TOP && position != SWT.BOTTOM) {
            position = DEFAULT_POSITION;
            return;
        }

        position = value;
    }

    /*
     * @see ILegend#getBounds(String)
     */
    public Rectangle getBounds(String seriesId) {
        return cellBounds.get(seriesId);
    }

    /**
     * Update the layout data.
     */
    public void updateLayoutData() {
        int width = 0;
        int height = 0;

        ISeries[] seriesArray = chart.getSeriesSet().getSeries();
        Rectangle r = chart.getClientArea();
        int titleHeight = ((Composite) chart.getTitle()).getSize().y;

        if (position == SWT.RIGHT || position == SWT.LEFT) {
            int columns = 1;
            int yPosition = MARGIN;
            int cellHeight = Util.getExtentInGC(getFont(), "dummy").y + MARGIN;
            int maxCellWidth = 0;

            for (ISeries series : seriesArray) {
                int textWidth = Util.getExtentInGC(getFont(), series.getId()).x;
                int cellWidth = textWidth + SYMBOL_WIDTH + MARGIN * 3;
                maxCellWidth = Math.max(maxCellWidth, cellWidth);
                if (yPosition + cellHeight < r.height - titleHeight
                        || yPosition == 0) {
                    yPosition += cellHeight;
                } else {
                    columns++;
                    yPosition = cellHeight + MARGIN;
                }
                cellBounds.put(series.getId(), new Rectangle(maxCellWidth
                        * (columns - 1), yPosition - cellHeight, cellWidth,
                        cellHeight));
                height = Math.max(yPosition, height);
            }
            width = maxCellWidth * columns;
        } else if (position == SWT.TOP || position == SWT.BOTTOM) {
            int rows = 1;
            int xPosition = 0;
            int cellHeight = Util.getExtentInGC(getFont(), "dummy").y;

            for (ISeries series : seriesArray) {
                int textWidth = Util.getExtentInGC(getFont(), series.getId()).x;
                int cellWidth = textWidth + SYMBOL_WIDTH + MARGIN * 3;
                if (xPosition + cellWidth < r.width || xPosition == 0) {
                    xPosition += cellWidth;
                } else {
                    rows++;
                    xPosition = cellWidth;
                }
                cellBounds.put(series.getId(), new Rectangle(xPosition
                        - cellWidth, (cellHeight + MARGIN) * (rows - 1)
                        + MARGIN, cellWidth, cellHeight));
                width = Math.max(xPosition, width);
            }
            height = (cellHeight + MARGIN) * rows + MARGIN;
        }

        setLayoutData(new ChartLayoutData(width, height));
    }

    /**
     * Draws the symbol of series.
     * 
     * @param gc
     *            the graphics context
     * @param series
     *            the series
     * @param r
     *            the rectangle to draw the symbol of series
     */
    protected void drawSymbol(GC gc, Series series, Rectangle r) {

        if (!visible) {
            return;
        }

        if (series instanceof ILineSeries) {
            // draw plot line
            gc.setForeground(((ILineSeries) series).getLineColor());
            gc.setLineWidth(LINE_WIDTH);
            int lineStyle = Util.getIndexDefinedInSWT(((ILineSeries) series)
                    .getLineStyle());
            int x = r.x;
            int y = r.y + r.height / 2;
            if (lineStyle != SWT.NONE) {
                gc.setLineStyle(lineStyle);
                gc.drawLine(x, y, x + SYMBOL_WIDTH, y);
            }

            // draw series symbol
            Color color = ((ILineSeries) series).getSymbolColor();
            Color[] colors = ((ILineSeries) series).getSymbolColors();
            if (colors != null && colors.length > 0) {
                color = colors[0];
            }
            ((LineSeries) series).drawSeriesSymbol(gc, x + SYMBOL_WIDTH / 2, y,
                    color);
        } else if (series instanceof IBarSeries) {
            // draw riser
            gc.setBackground(((IBarSeries) series).getBarColor());
            int size = SYMBOL_WIDTH / 2;
            int x = r.x + size / 2;
            int y = (int) (r.y - size / 2d + r.height / 2d);
            gc.fillRectangle(x, y, size, size);
        }
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl(PaintEvent e) {
        if (!visible) {
            return;
        }

        GC gc = e.gc;
        gc.setFont(getFont());
        ISeries[] seriesArray = chart.getSeriesSet().getSeries();
        if (seriesArray.length == 0) {
            return;
        }

        // draw frame
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setLineWidth(1);
        gc.setForeground(new Color(Display.getDefault(), Constants.GRAY));
        gc.drawRectangle(0, 0, getSize().x - 1, getSize().y - 1);

        // sort the series array
        ISeries[] sortedSeriesArray = sort(seriesArray);

        // draw content
        for (int i = 0; i < sortedSeriesArray.length; i++) {

            // draw plot line, symbol etc
            String id = sortedSeriesArray[i].getId();
            Rectangle r = cellBounds.get(id);
            drawSymbol(gc, (Series) sortedSeriesArray[i],
                    new Rectangle(r.x + MARGIN, r.y + MARGIN, SYMBOL_WIDTH,
                            r.height - MARGIN * 2));

            // draw plot id
            gc.setBackground(getBackground());
            gc.setForeground(getForeground());
            gc.drawText(id, r.x + SYMBOL_WIDTH + MARGIN * 2, r.y);
        }
    }

    /**
     * Sorts the given series array. For instance, if there are two stack series
     * in horizontal orientation, the top of stack series should appear at top
     * of legend.
     * <p>
     * If there are multiple x axes, the given series array will be sorted with
     * x axis first. And then, the series in each x axis will be sorted with
     * {@link Legend#sort(List, boolean, boolean)}.
     * 
     * @param seriesArray
     *            the series array
     * @return the sorted series array
     */
    private ISeries[] sort(ISeries[] seriesArray) {

        // create a map between axis id and series list
        Map<Integer, List<ISeries>> map = new HashMap<Integer, List<ISeries>>();
        for (ISeries series : seriesArray) {
            int axisId = series.getXAxisId();
            List<ISeries> list = map.get(axisId);
            if (list == null) {
                list = new ArrayList<ISeries>();
            }
            list.add(series);
            map.put(axisId, list);
        }

        // sort an each series list
        List<ISeries> sortedArray = new ArrayList<ISeries>();
        boolean isVertical = chart.getOrientation() == SWT.VERTICAL;
        for (Entry<Integer, List<ISeries>> entry : map.entrySet()) {
            boolean isCategoryEnabled = chart.getAxisSet().getXAxis(
                    entry.getKey()).isCategoryEnabled();
            sortedArray.addAll(sort(entry.getValue(), isCategoryEnabled,
                    isVertical));
        }

        return sortedArray.toArray(new ISeries[0]);
    }

    /**
     * Sorts the given series list which belongs to a certain x axis.
     * <ul>
     * <li>The stacked series will be gathered, and the order of stack series
     * will be reversed.</li> <li>In the case of vertical orientation, the order
     * of whole series will be reversed.</li>
     * </ul>
     * 
     * @param seriesList
     *            the series list which belongs to a certain x axis
     * @param isCategoryEnabled
     *            true if category is enabled
     * @param isVertical
     *            true in the case of vertical orientation
     * @return the sorted series array
     */
    private List<ISeries> sort(List<ISeries> seriesList,
            boolean isCategoryEnabled, boolean isVertical) {
        List<ISeries> sortedArray = new ArrayList<ISeries>();

        // gather the stacked series reversing the order of stack series
        int insertIndex = -1;
        for (int i = 0; i < seriesList.size(); i++) {
            if (isCategoryEnabled
                    && ((Series) seriesList.get(i)).isValidStackSeries()) {
                if (insertIndex == -1) {
                    insertIndex = i;
                } else {
                    sortedArray.add(insertIndex, seriesList.get(i));
                    continue;
                }
            }
            sortedArray.add(seriesList.get(i));
        }

        // reverse the order of whole series in the case of vertical orientation
        if (isVertical) {
            Collections.reverse(sortedArray);
        }

        return sortedArray;
    }
}