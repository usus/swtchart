package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example to convert data coordinate into pixel coordinate.
 */
public class CoordinateConversionExample1 {

    private static final int MARGIN = 5;

    private static final double[] ySeries = { 0.0, 0.38, 0.71, 0.92, 1.0, 0.92,
            0.71, 0.38, 0.0, -0.38, -0.71, -0.92, -1.0, -0.92, -0.71, -0.38 };

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Show Threshold Example");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());

        // create a chart
        Chart chart = new Chart(shell, SWT.NONE);

        // get Y axis
        final IAxis yAxis = chart.getAxisSet().getYAxis(0);

        // create line series
        ILineSeries series = (ILineSeries) chart.getSeriesSet().createSeries(
                SeriesType.LINE, "line series");
        series.setYSeries(ySeries);

        // adjust the axis range
        chart.getAxisSet().adjustRange();

        // add paint listener to draw threshold
        chart.getPlotArea().addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                int y = yAxis.getPixelCoordinate(0.65);
                e.gc.drawLine(0, y, e.width, y);
                e.gc.drawText("y=0.65", MARGIN, y + MARGIN);
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}