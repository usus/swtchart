package org.swtchart.internal.axis;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.swtchart.Chart;
import org.swtchart.IAxisTick;

/**
 * An axis tick.
 */
public class AxisTick implements IAxisTick {

	/** the chart */
	private Chart chart;

	/** the axis tick labels */
	private AxisTickLabels axisTickLabels;

	/** the axis tick marks */
	private AxisTickMarks axisTickMarks;

	/** true if tick is visible */
	private boolean isVisible;

	/** the tick mark step hint */
	private int tickMarkStepHint;

	/** the default tick mark step hint */
	private static final int DEFAULT_TICK_MARK_STEP_HINT = 64;

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
	protected AxisTick(Chart chart, int style, Axis axis) {
		this.chart = chart;

		axisTickLabels = new AxisTickLabels(chart, style, axis);
		axisTickMarks = new AxisTickMarks(chart, style, axis);
		isVisible = true;
		tickMarkStepHint = DEFAULT_TICK_MARK_STEP_HINT;
	}

	/**
	 * Gets the axis tick marks.
	 * 
	 * @return the axis tick marks
	 */
	public AxisTickMarks getAxisTickMarks() {
		return axisTickMarks;
	}

	/**
	 * Gets the axis tick labels.
	 * 
	 * @return the axis tick labels
	 */
	public AxisTickLabels getAxisTickLabels() {
		return axisTickLabels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color) {
		axisTickMarks.setForeground(color);
		axisTickLabels.setForeground(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#getForeground()
	 */
	public Color getForeground() {
		return axisTickMarks.getForeground();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font) {
		axisTickLabels.setFont(font);
		chart.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#getFont()
	 */
	public Font getFont() {
		return axisTickLabels.getFont();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		chart.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#getTickMarkStepHint()
	 */
	public int getTickMarkStepHint() {
		return tickMarkStepHint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IAxisTick#setTickMarkStepHint(int)
	 */
	public void setTickMarkStepHint(int tickMarkStepHint) {
		if (tickMarkStepHint < MIN_GRID_STEP_HINT) {
			tickMarkStepHint = DEFAULT_TICK_MARK_STEP_HINT;
		}

		this.tickMarkStepHint = tickMarkStepHint;
		chart.updateLayout();
	}

	/**
	 * Updates the tick around per 64 pixel.
	 */
	protected void updateTick(int length) {
		if (length <= 0) {
			length = 1;
		}
		axisTickLabels.update(length);
	}

	/**
	 * Updates the tick layout.
	 */
	protected void updateLayoutData() {
		axisTickLabels.updateLayoutData();
		axisTickMarks.updateLayoutData();
	}

}
