/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal.axis;

import org.eclipse.swt.graphics.Font;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis.Direction;
import org.swtchart.internal.Title;

/**
 * An Axis title.
 */
public class AxisTitle extends Title {

    /** the default text for X Axis */
    private static final String DEFAULT_TEXT_FOR_XAXIS = "X Axis";

    /** the default text for X Axis */
    private static final String DEFAULT_TEXT_FOR_YAXIS = "Y Axis";

    /** the default font */
    private static final Font DEFAULT_FONT = Constants.MEDIUM_FONT;

    /** the axis */
    private Axis axis;

    /** the direction of axis */
    private Direction direction;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     * @param axis
     *            the axis
     * @param direction
     *            the direction
     */
    public AxisTitle(Chart chart, int style, Axis axis, Direction direction) {
        super(chart, style);
        this.axis = axis;
        this.direction = direction;
        setFont(DEFAULT_FONT);
    }

    /*
     * @see Title#getDefaultText()
     */
    @Override
    protected String getDefaultText() {
        if (direction == Direction.X) {
            return DEFAULT_TEXT_FOR_XAXIS;
        }
        return DEFAULT_TEXT_FOR_YAXIS;
    }

    /*
     * @see Title#isHorizontal()
     */
    @Override
    protected boolean isHorizontal() {
        return axis.isHorizontalAxis();
    }
}
