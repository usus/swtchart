package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example to get bounds of items on legend.
 */
public class CoordinateConversionExample5 {

    private static final double[] ySeries1 = { 0.1, 0.1, 0.2, 0.2, 0.3 };
    private static final double[] ySeries2 = { 0.5, 0.5, 0.4, 0.3, 0.2 };
    private static final double[] ySeries3 = { 0.3, 0.2, 0.3, 0.4, 0.4 };

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Legend Bounds Example");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());

        // create a chart
        final Chart chart = new Chart(shell, SWT.NONE);

        // create bar series
        IBarSeries series1 = (IBarSeries) chart.getSeriesSet().createSeries(
                SeriesType.BAR, "series 1");
        series1.setYSeries(ySeries1);
        series1.setBarColor(Display.getDefault()
                .getSystemColor(SWT.COLOR_GREEN));
        IBarSeries series2 = (IBarSeries) chart.getSeriesSet().createSeries(
                SeriesType.BAR, "series 2");
        series2.setYSeries(ySeries2);
        series2.setBarColor(Display.getDefault().getSystemColor(
                SWT.COLOR_MAGENTA));
        IBarSeries series3 = (IBarSeries) chart.getSeriesSet().createSeries(
                SeriesType.BAR, "series 3");
        series3.setYSeries(ySeries3);

        // adjust the axis range
        chart.getAxisSet().adjustRange();

        // add mouse move listener to legend
        final Control legend = (Control) chart.getLegend();
        legend.addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                for (ISeries series : chart.getSeriesSet().getSeries()) {
                    Rectangle r = chart.getLegend().getBounds(series.getId());
                    if (r.x < e.x && e.x < r.x + r.width && r.y < e.y
                            && e.y < r.y + r.height) {
                        legend.setToolTipText(series.getId());
                        return;
                    }
                }
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