/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * Constant values.
 */
public class Constants {

    /** the large font size */
    final static public Font LARGE_FONT = new Font(Display.getDefault(),
            "Tahoma", 13, SWT.BOLD);

    /** the medium font size */
    final static public Font MEDIUM_FONT = new Font(Display.getDefault(),
            "Tahoma", 11, SWT.BOLD);

    /** the small font size */
    final static public Font SMALL_FONT = new Font(Display.getDefault(),
            "Tahoma", 9, SWT.NORMAL);
}
