package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

/**
 * An example for large series chart.
 */
public class LargeSeriesExample {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Large Series Example");
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());

		// create a chart
		Chart chart = new Chart(shell, SWT.NONE);

		// set titles
		chart.getTitle().setText("Large Series Example");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Data Points");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Amplitude");

		// create line series
		ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet()
				.createSeries(SeriesType.LINE, "line series");
		lineSeries.setYSeries(getSeries());
		lineSeries.setSymbolType(PlotSymbolType.NONE);

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

	private static double[] getSeries() {
		double[] series = new double[1048576];
		for (int i = 0; i < series.length; i++) {
			series[i] = Math.sin(i  * 33 * Math.PI/ series.length)
					+ Math.sin(i * 15 * Math.PI/ series.length);
		}
		return series;
	}
}