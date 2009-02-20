/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * A series label.
 */
public interface ISeriesLabel {

    /**
     * Sets the format for numeric value. If null is given, default format
     * "############.###########" will be set.
     * 
     * @param format
     *            the format
     */
    void setFormat(String format);

    /**
     * Gets the format for numeric value.
     * 
     * @return the format for numeric value
     */
    String getFormat();

    /**
     * Sets the label color. If null is given, default color will be set.
     * 
     * @param color
     *            the label color
     */
    void setForeground(Color color);

    /**
     * Gets the label color.
     * 
     * @return the label color
     */
    Color getForeground();

    /**
     * Sets the label font.
     * 
     * @param font
     *            the label font
     */
    void setFont(Font font);

    /**
     * Gets the label font.
     * 
     * @return the label font
     */
    Font getFont();

    /**
     * Sets the label visibility state.
     * 
     * @param visible
     *            the label visibility state
     */
    void setVisible(boolean visible);

    /**
     * Gets the label visibility state.
     * 
     * @return true if label is visible
     */
    boolean isVisible();

}
