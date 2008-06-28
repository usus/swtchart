package org.swtchart.internal.axis;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.internal.ChartLayoutData;
import org.swtchart.internal.Util;

/**
 * Axis tick labels.
 */
public class AxisTickLabels extends Canvas implements PaintListener {

	/** the axis */
	private Axis axis;

	/** the array of tick label */
	private ArrayList<String> tickLabels;

	/** the array of tick label position in pixels */
	private ArrayList<Integer> tickLabelPositions;

	/** the array of visibility state of tick label */
	private ArrayList<Boolean> tickVisibilities;

	/** the maximum length of tick labels */
	private int tickLabelMaxLength;

	/** the default foreground */
	private static final Color DEFAULT_FOREGROUND = Constants.BLUE;

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
	protected AxisTickLabels(Chart chart, int style, Axis axis) {
		super(chart, style);
		this.axis = axis;

		tickLabels = new ArrayList<String>();
		tickLabelPositions = new ArrayList<Integer>();
		tickVisibilities = new ArrayList<Boolean>();

		setFont(Constants.TINY_FONT);
		setForeground(DEFAULT_FOREGROUND);
		addPaintListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics
	 * .Color)
	 */
	@Override
	public void setForeground(Color color) {
		if (color == null) {
			color = DEFAULT_FOREGROUND;
		}
		super.setForeground(color);
	}

	/**
	 * Gets the array of tick label.
	 * 
	 * @return the array of tick label
	 */
	protected ArrayList<String> getTickLabel() {
		return tickLabels;
	}

	/**
	 * Updates the tick labels
	 */
	protected void update(int length) {
		if (axis.isValidCategoryAxis()) {
			updateTickLabelForCategoryAxis();
		} else if (axis.isLogScaleEnabled()) {
			updateTickLabelForLogScale();
		} else {
			updateTickLabelForLinearScale(length);
		}

		updateTickLabelPosition(length + 2);
		updateTickVisibility();
		updateTickLabelMaxLength();
	}

	/**
	 * Updates tick label for category axis.
	 */
	private void updateTickLabelForCategoryAxis() {
		tickLabels.clear();

		double min = axis.getRange().lower;
		double max = axis.getRange().upper;

		String[] series = axis.getCategorySeries();
		for (int i = (int) min; i <= max; i++) {
			if (series != null && series.length > i && i >= 0) {
				tickLabels.add(series[i]);
			}
		}
	}

	/**
	 * Updates tick label for log scale.
	 */
	private void updateTickLabelForLogScale() {
		tickLabels.clear();

		double min = axis.getRange().lower;
		double max = axis.getRange().upper;

		int digitMin = (int) Math.ceil(Math.log10(min));
		int digitMax = (int) Math.ceil(Math.log10(max));

		final BigDecimal MIN = new BigDecimal(new Double(min).toString());
		BigDecimal tickStep = pow(10, digitMin - 1);
		BigDecimal firstPosition;

		if (MIN.remainder(tickStep).doubleValue() <= 0) {
			firstPosition = MIN.subtract(MIN.remainder(tickStep));
		} else {
			firstPosition = MIN.subtract(MIN.remainder(tickStep)).add(tickStep);
		}

		for (int i = digitMin; i <= digitMax; i++) {
			for (BigDecimal j = firstPosition; j.doubleValue() <= pow(10, i)
					.doubleValue(); j = j.add(tickStep)) {
				if (j.doubleValue() > max) {
					break;
				}
				String label = new DecimalFormat("############.###########")
						.format(j.doubleValue());
				tickLabels.add(label);
			}
			tickStep = tickStep.multiply(pow(10, 1));
			firstPosition = tickStep.add(pow(10, i));
		}

	}

	/**
	 * Calculates the value of the first argument raised to the power of the
	 * second argument.
	 * 
	 * @param base
	 *            the base
	 * @param expornent
	 *            the exponent
	 * @return the value <tt>a<sup>b</sup></tt> in <tt>BigDecimal</tt>
	 */
	private BigDecimal pow(double base, int expornent) {
		BigDecimal value;
		if (expornent > 0) {
			value = new BigDecimal(new Double(base).toString()).pow(expornent);
		} else {
			value = BigDecimal.ONE.divide(new BigDecimal(new Double(base)
					.toString()).pow(-expornent));
		}
		return value;
	}

	/**
	 * Updates tick label for normal scale.
	 * 
	 * @param length
	 *            axis length (>0)
	 */
	private void updateTickLabelForLinearScale(int length) {
		tickLabels.clear();

		double min = axis.getRange().lower;
		double max = axis.getRange().upper;
		final BigDecimal MIN = new BigDecimal(new Double(min).toString());
		final BigDecimal TICKSTEP = getGridStep(length, min, max);
		BigDecimal firstPosition;

		/* if (min % tickStep <= 0) */
		if (MIN.remainder(TICKSTEP).doubleValue() <= 0) {
			/* firstPosition = min - min % tickStep */
			firstPosition = MIN.subtract(MIN.remainder(TICKSTEP));
		} else {
			/* firstPosition = min - min % tickStep + tickStep */
			firstPosition = MIN.subtract(MIN.remainder(TICKSTEP)).add(TICKSTEP);
		}

		for (BigDecimal b = firstPosition; b.doubleValue() <= max; b = b
				.add(TICKSTEP)) {
			String label = new DecimalFormat("############.###########")
					.format(b.doubleValue());
			tickLabels.add(label);
		}
	}

	/**
	 * Gets max length of tick label.
	 */
	private void updateTickLabelMaxLength() {
		int maxLength = 0;
		for (int i = 0; i < tickLabels.size(); i++) {
			if (tickVisibilities.size() > i && tickVisibilities.get(i) == true) {
				Point p = Util.getExtentInGC(axis.getTick().getFont(),
						tickLabels.get(i));
				if (p.x > maxLength) {
					maxLength = p.x;
				}
			}
		}
		tickLabelMaxLength = maxLength;
	}

	/**
	 * Updates the tick label position.
	 * 
	 * @param length
	 *            axis length (>0)
	 */
	private void updateTickLabelPosition(int length) {
		tickLabelPositions.clear();

		for (int j = 0; j < tickLabels.size(); j++) {
			double tickLabelPosition = 0;
			double min = axis.getRange().lower;
			double max = axis.getRange().upper;
			if (axis.isLogScaleEnabled()) {
				if (axis.isValidCategoryAxis()) {
					tickLabelPosition = length * (j + 0.5) / tickLabels.size();
				} else {
					double digitMax = Math.log10(max);
					double digitMin = Math.log10(min);
					tickLabelPosition = (Math.log10(Double.valueOf(tickLabels
							.get(j))) - digitMin)
							/ (digitMax - digitMin) * length;
				}
			} else {
				if (axis.isValidCategoryAxis()) {
					tickLabelPosition = length * (j + 0.5) / tickLabels.size();
				} else {
					tickLabelPosition = (Double.valueOf(tickLabels.get(j)) - min)
							/ (max - min) * length;
				}
			}
			tickLabelPositions.add((int) tickLabelPosition - 1);
		}
	}

	/**
	 * Updates visibility of tick labels.
	 */
	private void updateTickVisibility() {
		tickVisibilities.clear();

		for (int i = 0; i < tickLabels.size(); i++) {
			tickVisibilities.add(Boolean.TRUE);
		}

		int previousPosition = Integer.MAX_VALUE;
		for (int i = tickLabels.size() - 1; i >= 0; i--) {
			Point p = Util.getExtentInGC(axis.getTick().getFont(), tickLabels
					.get(i));
			int interval = previousPosition - tickLabelPositions.get(i);
			int textLength = axis.isHorizontalAxis() ? p.x : p.y;
			if (interval < textLength) {
				tickVisibilities.set(i, Boolean.FALSE);
			} else {
				previousPosition = tickLabelPositions.get(i);
			}
		}
	}

	/**
	 * Gets the grid step.
	 * 
	 * @param lengthInPixels
	 *            axis length in pixels
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @return rounded value.
	 */
	private BigDecimal getGridStep(int lengthInPixels, double min, double max)
			throws IllegalArgumentException {
		if (lengthInPixels <= 0) {
			throw new IllegalArgumentException(
					"lengthInPixels must be positive value.");
		}
		if (min >= max) {
			throw new IllegalArgumentException("min must be less than max.");
		}
		double length = Math.abs(max - min);
		double gridStepHint = length / lengthInPixels
				* axis.getTick().getTickMarkStepHint();

		// gridStepHint --> mantissa * 10 ** exponent
		// e.g. 724.1 --> 7.241 * 10 ** 2
		double mantissa = gridStepHint;
		int exponent = 0;
		if (mantissa < 1) {
			while (mantissa < 1) {
				mantissa *= 10.0;
				exponent--;
			}
		} else {
			while (mantissa >= 10) {
				mantissa /= 10.0;
				exponent++;
			}
		}

		// calculate the grid step with hint.
		BigDecimal gridStep;
		if (mantissa > 7.5) {
			// gridStep = 10.0 * 10**exponent
			gridStep = BigDecimal.TEN.multiply(pow(10, exponent));
		} else if (mantissa > 3.5) {
			// gridStep = 5.0 * 10 ** exponent
			gridStep = new BigDecimal(new Double(5).toString()).multiply(pow(
					10, exponent));
		} else if (mantissa > 1.5) {
			// gridStep = 2.0 * 10 ** exponent
			gridStep = new BigDecimal(new Double(2).toString()).multiply(pow(
					10, exponent));
		} else {
			// gridStep = 1.0 * 10 ** exponent
			gridStep = pow(10, exponent);
		}
		return gridStep;
	}

	/**
	 * Gets the tick label positions.
	 * 
	 * @return the tick label positions
	 */
	public ArrayList<Integer> getTickLabelPositions() {
		return tickLabelPositions;
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
				height = Axis.MARGIN
						+ Util.getExtentInGC(axis.getTick().getAxisTickLabels()
								.getFont(), "dummy").y;
			} else {
				width = tickLabelMaxLength + Axis.MARGIN;
			}
		}
		setLayoutData(new ChartLayoutData(width, height));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events
	 * .PaintEvent)
	 */
	@Override
	public void paintControl(PaintEvent e) {
		if (axis.isHorizontalAxis()) {
			drawXTick(e.gc);
		} else {
			drawYTick(e.gc);
		}
	}

	/**
	 * Draw the X tick.
	 * 
	 * @param gc
	 *            the graphics context
	 */
	private void drawXTick(GC gc) {
		int offset = axis.getTick().getAxisTickMarks().getBounds().x;

		// draw tick labels
		gc.setFont(axis.getTick().getFont());
		for (int i = 0; i < tickLabelPositions.size(); i++) {
			if (tickVisibilities.get(i) == true) {
				String text = tickLabels.get(i);
				int fontWidth = gc.textExtent(text).x;
				int x = (int) (tickLabelPositions.get(i) - fontWidth / 2.0 + offset);
				gc.drawText(text, x, 0);
			}
		}
	}

	/**
	 * Draw the Y tick.
	 * 
	 * @param gc
	 *            the graphics context
	 */
	private void drawYTick(GC gc) {
		int height = getSize().y;
		int margin = Axis.MARGIN + AxisTickMarks.TICK_LENGTH;

		// draw tick labels
		gc.setFont(axis.getTick().getFont());
		int figureHeight = gc.textExtent("dummy").y;
		for (int i = 0; i < tickLabelPositions.size(); i++) {
			if (tickVisibilities.size() == 0 || tickLabels.size() == 0) {
				break;
			}

			if (tickVisibilities.get(i) == true) {
				String text = tickLabels.get(i);
				int x = 0;
				if (tickLabels.get(0).startsWith("-") && !text.startsWith("-")) {
					x += gc.textExtent("-").x;
				}
				int y = (int) (height - 1 - tickLabelPositions.get(i)
						- figureHeight / 2.0 - margin);
				gc.drawText(text, x, y);
			}
		}
	}
}
