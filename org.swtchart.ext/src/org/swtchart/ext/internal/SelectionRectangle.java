package org.swtchart.ext.internal;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.swtchart.Range;
import org.swtchart.IAxis.Direction;

/**
 * Selection rectangle with mouse to zoom in/out.
 */
public class SelectionRectangle {

	private Point startPoint;

	private Point endPoint;

	/**
	 * Sets the start point.
	 * 
	 * @param x
	 *            the X coordinate of start point in pixels
	 * @param y
	 *            the Y coordinate of start point in pixels
	 */
	public void setStartPoint(int x, int y) {
		startPoint = new Point(x, y);
	}

	/**
	 * Sets the end point.
	 * 
	 * @param x
	 *            the X coordinate of end point in pixels
	 * @param y
	 *            the X coordinate of end point in pixels
	 */
	public void setEndPoint(int x, int y) {
		endPoint = new Point(x, y);
	}

	/**
	 * Gets the range with ratio.
	 * 
	 * @param direction
	 *            the axis direction
	 * @param rectangle
	 *            the rectangle for plot area
	 * @return the X range with ratio
	 */
	public Range getRange(Direction direction, Rectangle rectangle) {
		if (endPoint == null) {
			return null;
		}

		double start = 0;
		double end = 0;
		if (direction == Direction.X) {
			start = (startPoint.x - rectangle.x) / (double) rectangle.width;
			end = (endPoint.x - rectangle.x) / (double) rectangle.width;
		} else if (direction == Direction.Y) {
			start = (startPoint.y - rectangle.y) / (double) rectangle.height;
			end = (endPoint.y - rectangle.y) / (double) rectangle.height;
		}

		double min;
		double max;
		if (start < end) {
			min = start;
			max = end;
		} else {
			min = end;
			max = start;
		}

		return new Range(min, max);
	}

	/**
	 * Check if selection is disposed.
	 * 
	 * @return true if selection is disposed.
	 */
	public boolean isDisposed() {
		return startPoint == null;
	}

	/**
	 * Disposes the resource.
	 */
	public void dispose() {
		startPoint = null;
		endPoint = null;
	}

	/**
	 * Draws the selection rectangle on chart panel.
	 * 
	 * @param gc
	 *            the graphics context
	 */
	public void draw(GC gc) {
		if (startPoint == null || endPoint == null) {
			return;
		}

		int minX;
		int maxX;
		if (startPoint.x > endPoint.x) {
			minX = endPoint.x;
			maxX = startPoint.x;
		} else {
			minX = startPoint.x;
			maxX = endPoint.x;
		}

		int minY;
		int maxY;
		if (startPoint.y > endPoint.y) {
			minY = endPoint.y;
			maxY = startPoint.y;
		} else {
			minY = startPoint.y;
			maxY = endPoint.y;
		}

		gc.drawRectangle(minX, minY, maxX - minX, maxY - minY);
	}

}
