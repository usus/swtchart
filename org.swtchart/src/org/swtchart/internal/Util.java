/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.swtchart.LineStyle;

/**
 * A utility class providing generic methods.
 */
public final class Util {

    /**
     * Rotates the given image data. If the given image data is
     * <code>null</code>, <code>null</code> will be returned.
     * 
     * @param imageData
     *            the image data to be rotated
     * @return the rotated image data
     * @throws IllegalArgumentException
     *             if the given image data is <code>null</code>.
     */
    public static ImageData rotate(ImageData imageData)
            throws IllegalArgumentException {

        if (imageData == null) {
            return null;
        }

        int scanlinePad = imageData.height * imageData.bytesPerLine
                / imageData.width;
        byte[] data = new byte[imageData.data.length];

        for (int y = 0; y < imageData.height; y++) {
            for (int x = 0; x < imageData.width; x++) {
                int srcPos = y * imageData.bytesPerLine + x
                        * imageData.bytesPerLine / imageData.width;
                int destPos = (imageData.width - x - 1) * scanlinePad + y
                        * imageData.bytesPerLine / imageData.width;
                int length = imageData.bytesPerLine / imageData.width;

                System.arraycopy(imageData.data, srcPos, data, destPos, length);
            }
        }

        return new ImageData(imageData.height, imageData.width,
                imageData.depth, imageData.palette, scanlinePad, data);
    }

    /**
     * Gets the text extent with given font in GC. If the given text or font is
     * <code>null</code> or already disposed, point containing size zero will be
     * returned.
     * 
     * @param font
     *            the font
     * @param text
     *            the text
     * @return a point containing text extent
     */
    public static Point getExtentInGC(Font font, String text) {

        if (text == null || font == null || font.isDisposed()) {
            return new Point(0, 0);
        }

        // create GC
        int ARBITRARY_WIDTH = 10;
        int ARBITRARY_HEIGHT = 10;
        Image image = new Image(Display.getCurrent(), ARBITRARY_WIDTH,
                ARBITRARY_HEIGHT);
        GC gc = new GC(image);

        // get extent of text with given font
        gc.setFont(font);
        Point p = gc.textExtent(text);

        // dispose resources
        image.dispose();
        gc.dispose();

        return p;
    }

    /**
     * Gets the index defined in SWT.
     * 
     * @param lineStyle
     *            the line style
     * @return the index defined in SWT.
     */
    public static int getIndexDefinedInSWT(LineStyle lineStyle) {
        switch (lineStyle) {
        case NONE:
            return SWT.NONE;
        case SOLID:
            return SWT.LINE_SOLID;
        case DASH:
            return SWT.LINE_DASH;
        case DOT:
            return SWT.LINE_DOT;
        case DASHDOT:
            return SWT.LINE_DASHDOT;
        case DASHDOTDOT:
            return SWT.LINE_DASHDOTDOT;
        default:
            return SWT.LINE_SOLID;
        }
    }
}
