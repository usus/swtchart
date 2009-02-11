package org.swtchart.internal.axis;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis.Position;
import org.swtchart.internal.ChartLayoutData;

/**
 * Axis tick marks.
 */
public class AxisTickMarks extends Canvas implements PaintListener {

    /** the axis */
    private Axis axis;

    /** the line width */
    protected static final int LINE_WIDTH = 1;

    /** the tick length */
    public static final int TICK_LENGTH = 5;

    /** the default foreground */
    private static final RGB DEFAULT_FOREGROUND = Constants.BLUE;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     * @param axis
     *            the axis
     */
    public AxisTickMarks(Chart chart, int style, Axis axis) {
        super(chart, style);
        this.axis = axis;

        setForeground(new Color(Display.getDefault(), DEFAULT_FOREGROUND));
        addPaintListener(this);
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

    /**
     * Gets the associated axis.
     * 
     * @return the axis
     */
    public Axis getAxis() {
        return axis;
    }

    /**
     * Updates title layout.
     */
    protected void updateLayoutData() {
        int width = SWT.DEFAULT;
        int height = SWT.DEFAULT;
        if (!axis.getTick().isVisible()) {
            width = 0;
            height = 0;
        } else {
            if (axis.isHorizontalAxis()) {
                height = Axis.MARGIN + TICK_LENGTH;
            } else {
                width = TICK_LENGTH + Axis.MARGIN;
            }
        }
        setLayoutData(new ChartLayoutData(width, height));
    }

    /*
     * @see PaintListener#paintControl(PaintEvent)
     */
    public void paintControl(PaintEvent e) {
        ArrayList<Integer> tickLabelPositions = axis.getTick()
                .getAxisTickLabels().getTickLabelPositions();

        int width = getSize().x;
        int height = getSize().y;

        if (axis.isHorizontalAxis()) {
            drawXTickMarks(e.gc, tickLabelPositions, axis.getPosition(), width,
                    height);
        } else {
            drawYTickMarks(e.gc, tickLabelPositions, axis.getPosition(), width,
                    height);
        }
    }

    /**
     * Draw the X tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param position
     *            the axis position
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param gc
     *            the graphics context
     */
    private void drawXTickMarks(GC gc, ArrayList<Integer> tickLabelPositions,
            Position position, int width, int height) {

        // draw tick marks
        gc.setLineStyle(SWT.LINE_SOLID);
        if (axis.isValidCategoryAxis()) {
            if (tickLabelPositions.size() > 1) {
                int step = tickLabelPositions.get(1).intValue()
                        - tickLabelPositions.get(0).intValue();
                int x = (int) (tickLabelPositions.get(0).intValue() - step / 2d);
                for (int i = 0; i < tickLabelPositions.size() + 1; i++) {
                    x += step;
                    int y = 0;
                    if (position == Position.Secondary) {
                        y = height - 1 - LINE_WIDTH - TICK_LENGTH;
                    }
                    gc.drawLine(x, y, x, y + TICK_LENGTH);
                }
            }
        } else {
            for (int i = 0; i < tickLabelPositions.size(); i++) {
                int x = tickLabelPositions.get(i);
                int y = 0;
                if (position == Position.Secondary) {
                    y = height - 1 - LINE_WIDTH - TICK_LENGTH;
                }
                gc.drawLine(x, y, x, y + TICK_LENGTH);
            }
        }

        // draw axis line
        if (position == Position.Primary) {
            gc.drawLine(0, 0, width - 1, 0);
        } else {
            gc.drawLine(0, height - 1, width - 1, height - 1);
        }
    }

    /**
     * Draw the Y tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param position
     *            the axis position
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param gc
     *            the graphics context
     */
    private void drawYTickMarks(GC gc, ArrayList<Integer> tickLabelPositions,
            Position position, int width, int height) {

        // draw tick marks
        gc.setLineStyle(SWT.LINE_SOLID);
        if (axis.isValidCategoryAxis()) {
            if (tickLabelPositions.size() > 1) {
                int step = tickLabelPositions.get(1).intValue()
                        - tickLabelPositions.get(0).intValue();
                int y = (int) (tickLabelPositions.get(0).intValue() - step / 2d);
                for (int i = 0; i < tickLabelPositions.size() + 1; i++) {
                    y += step;
                    int x = 0;
                    if (position == Position.Primary) {
                        x = width - 1 - LINE_WIDTH - TICK_LENGTH;
                    } else {
                        x = LINE_WIDTH;
                    }
                    gc.drawLine(x, y, x + TICK_LENGTH, y);
                }
            }
        } else {
            int y = 0;
            for (int i = 0; i < tickLabelPositions.size(); i++) {
                int x = 0;
                if (position == Position.Primary) {
                    x = width - 1 - LINE_WIDTH - TICK_LENGTH;
                } else {
                    x = LINE_WIDTH;
                }
                y = height - 1 - tickLabelPositions.get(i);
                gc.drawLine(x, y, x + TICK_LENGTH, y);
            }
        }

        // draw axis line
        if (position == Position.Primary) {
            gc.drawLine(width - 1, 0, width - 1, height - 1);
        } else {
            gc.drawLine(0, 0, 0, height - 1);
        }
    }
}
