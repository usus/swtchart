package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example for axis orientation.
 */
public class OrientationExample {

	private static final double[] ySeries = { 0.2, 1.1, 1.9, 2.3, 1.8, 1.5,
			1.8, 2.6, 2.9, 3.2 };

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Orientation Example");
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());

		// create a chart
		Chart chart = new Chart(shell, SWT.NONE);
		
		// set the chart orientation
		chart.setOrientation(SWT.VERTICAL);
		
		// set titles
		chart.getTitle().setText("Orientation Example");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Data Points");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Amplitude");

		// create bar series
		ISeries barPlot = chart.getSeriesSet().createSeries(SeriesType.BAR,
				"bar series");
		barPlot.setYSeries(ySeries);

		// adjust the axis range
		chart.getAxisSet().adjustRange();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}