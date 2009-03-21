package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example to convert pixel coordinate into data coordinate.
 */
public class CoordinateConversionExample2 {

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
        shell.setText("Show Mouse Position Example");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());

        // create a chart
        final Chart chart = new Chart(shell, SWT.NONE);

        // set titles
        chart.getTitle().setText("Show Mouse Position Example");
        final IAxis xAxis = chart.getAxisSet().getXAxis(0);
        final IAxis yAxis = chart.getAxisSet().getYAxis(0);
        xAxis.getTitle().setText("Data Points");
        yAxis.getTitle().setText("Amplitude");

        // create line series
        ILineSeries series = (ILineSeries) chart.getSeriesSet().createSeries(
                SeriesType.LINE, "line series");
        series.setYSeries(ySeries);

        // adjust the axis range
        chart.getAxisSet().adjustRange();

        // add mouse move listener to show mouse position on tooltip
        chart.getPlotArea().addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                double x = xAxis.getDataCoordinate(e.x);
                double y = yAxis.getDataCoordinate(e.y);
                chart.getPlotArea().setToolTipText("x:" + x + ", y:" + y);
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