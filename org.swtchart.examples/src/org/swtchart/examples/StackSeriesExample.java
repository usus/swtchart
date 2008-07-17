package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example for chart with stack series.
 */
public class StackSeriesExample {

	private static final double[] ySeries1 = { 1.3, 2.4, 3.9, 2.6, 1.1 };
	private static final double[] ySeries2 = { 3.0, 2.1, 1.9, 2.3, 3.2 };

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Stack Series Example");
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());

		// create a chart
		Chart chart = new Chart(shell, SWT.NONE);

		// set titles
		chart.getTitle().setText("Stack Series Example");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Month");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Amplitude");

		// set category
		chart.getAxisSet().getXAxis(0).enableCategory(true);
		chart.getAxisSet().getXAxis(0).setCategorySeries(
				new String[] { "Jan", "Feb", "Mar", "Apr", "May" });

		// create bar series
		IBarSeries barSeries1 = (IBarSeries) chart.getSeriesSet().createSeries(
				SeriesType.BAR, "bar series 1");
		barSeries1.setYSeries(ySeries1);
		barSeries1.setBarColor(new Color(Display.getDefault(), new RGB(80, 240,
				180)));

		IBarSeries barSeries2 = (IBarSeries) chart.getSeriesSet().createSeries(
				SeriesType.BAR, "bar series 2");
		barSeries2.setYSeries(ySeries2);

		// enable stack series
		barSeries1.enableStack(true);
		barSeries2.enableStack(true);
		
		// fit the axis scale
		chart.getAxisSet().autoScale();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}