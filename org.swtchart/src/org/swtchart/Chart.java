package org.swtchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.swtchart.internal.ChartLayout;
import org.swtchart.internal.ChartLayoutData;
import org.swtchart.internal.ChartTitle;
import org.swtchart.internal.Legend;
import org.swtchart.internal.PlotArea;
import org.swtchart.internal.Title;
import org.swtchart.internal.axis.AxisSet;

/**
 * A chart which are composed of title, legend, axes and plot area.
 */
public class Chart extends Composite implements Listener {

	/** the title */
	private Title title;

	/** the legend */
	private Legend legend;

	/** the set of axes */
	private AxisSet axisSet;

	/** the plot area */
	private PlotArea plotArea;

	/** the orientation of chart which can be horizontal or vertical */
	private int orientation;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite on which chart is placed
	 * @param style
	 *            the style of widget to construct
	 */
	public Chart(Composite parent, int style) {
		super(parent, style);
		
		orientation = SWT.HORIZONTAL;
		
		parent.layout();

		setLayout(new ChartLayout());

		title = new ChartTitle(this, SWT.NONE);
		title.setLayoutData(new ChartLayoutData(SWT.DEFAULT, 100));
		legend = new Legend(this, SWT.NONE);
		legend.setLayoutData(new ChartLayoutData(200, SWT.DEFAULT));
		plotArea = new PlotArea(this, SWT.NONE);
		axisSet = new AxisSet(this);

		updateLayout();

		addListener(SWT.Resize, this);
	}

	/**
	 * Gets the chart title.
	 * 
	 * @return the chart title
	 */
	public ITitle getTitle() {
		return title;
	}

	/**
	 * Gets the legend.
	 * 
	 * @return the legend
	 */
	public ILegend getLegend() {
		return legend;
	}

	/**
	 * Gets the set of axes.
	 * 
	 * @return the set of axes
	 */
	public IAxisSet getAxisSet() {
		return axisSet;
	}

	/**
	 * Gets the plot area.
	 * 
	 * @return the plot area
	 */
	public Composite getPlotArea() {
		return plotArea;
	}

	/**
	 * Gets the set of series.
	 * 
	 * @return the set of series
	 */
	public ISeriesSet getSeriesSet() {
		return plotArea.getSeriesSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(Color color) {
		super.setBackground(color);

		for (Control child : getChildren()) {
			if (!(child instanceof PlotArea) && !(child instanceof Legend)) {
				child.setBackground(color);
			}
		}
	}

	/**
	 * Gets the background color in plot area. This method is identical with
	 * <tt>getPlotArea().getBackground()</tt>.
	 * 
	 * @return the background color in plot area
	 */
	public Color getBackgroundInPlotArea() {
		return plotArea.getBackground();
	}

	/**
	 * Sets the background color in plot area.
	 * 
	 * @param color
	 *            the background color in plot area. If <tt>null</tt> is
	 *            given, default background color will be set.
	 * @exception IllegalArgumentException
	 *                if given color is disposed
	 */
	public void setBackgroundInPlotArea(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		plotArea.setBackground(color);
	}

	/**
	 * Sets the state of chart orientation. The horizontal orientation means
	 * that X axis is horizontal as usual, while the vertical orientation means
	 * that Y axis is horizontal.
	 * 
	 * @param orientation
	 *            the orientation which can be SWT.HORIZONTAL or SWT.VERTICAL
	 */
	public void setOrientation(int orientation) {
		if (orientation == SWT.HORIZONTAL || orientation == SWT.VERTICAL) {
			this.orientation = orientation;
		}
		updateLayout();
	}

	/**
	 * Gets the state of chart orientation. The horizontal orientation means
	 * that X axis is horizontal as usual, while the vertical orientation means
	 * that Y axis is horizontal.
	 * 
	 * @return the orientation which can be SWT.HORIZONTAL or SWT.VERTICAL
	 */
	public int getOrientation() {
		return orientation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.Resize:
			updateLayout();
			redraw();
			break;
		default:
			break;
		}
	}

	/**
	 * Updates the layout of chart elements.
	 */
	public void updateLayout() {
		if (legend != null) {
			legend.updateLayoutData();
		}

		if (title != null) {
			title.updateLayoutData();
		}

		if (axisSet != null) {
			axisSet.updateLayoutData();
		}

		layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#redraw()
	 */
	@Override
	public void redraw() {
		super.redraw();
		for (Control child : getChildren()) {
			child.redraw();
		}
	}
}
