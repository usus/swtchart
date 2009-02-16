package org.swtchart.internal.series;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ISeries;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressConfig;

/**
 * A series container.
 */
public class SeriesSet implements ISeriesSet {

    /** the chart */
    private Chart chart;

    /** the series */
    private LinkedHashMap<String, ISeries> seriesMap;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     */
    public SeriesSet(Chart chart) {
        this.chart = chart;

        seriesMap = new LinkedHashMap<String, ISeries>();
    }

    /*
     * @see ISeriesSet#createSeries(ISeries.SeriesType, String)
     */
    @SuppressWarnings("null")
    public ISeries createSeries(SeriesType type, String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        String identifier = id.trim();

        if ("".equals(identifier)) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        ISeries series = null;
        if (type == SeriesType.BAR) {
            series = new BarSeries(chart, identifier);
        } else if (type == SeriesType.LINE) {
            series = new LineSeries(chart, identifier);
        } else {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }

        int[] xAxisIds = chart.getAxisSet().getXAxisIds();
        int[] yAxisIds = chart.getAxisSet().getYAxisIds();
        series.setXAxisId(xAxisIds[0]);
        series.setYAxisId(yAxisIds[0]);

        seriesMap.put(identifier, series);

        Axis axis = (Axis) chart.getAxisSet().getXAxis(xAxisIds[0]);
        if (axis != null) {
            updateStackAndRiserData();
        }

        // legend will be shown if there is previously no series.
        chart.updateLayout();

        return series;
    }

    /*
     * @see ISeriesSet#getSeries(String)
     */
    public ISeries getSeries(String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        return seriesMap.get(id);
    }

    /*
     * @see ISeriesSet#getSeries()
     */
    public ISeries[] getSeries() {
        Set<String> keys = seriesMap.keySet();
        ISeries[] series = new ISeries[keys.size()];
        int i = 0;
        for (String key : keys) {
            series[i++] = seriesMap.get(key);
        }
        return series;
    }

    /*
     * @see ISeriesSet#deleteSeries(String)
     */
    public void deleteSeries(String id) {
        if (id == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (seriesMap.get(id) == null) {
            throw new IllegalArgumentException("Given series id doesn't exist");
        }

        seriesMap.remove(id);

        updateStackAndRiserData();

        // legend will be hidden if this is the last series
        chart.updateLayout();
    }

    /**
     * Gets the X range of series.
     * 
     * @return the X range of series
     */
    protected Range getXRange() {
        double minX = Double.NaN;
        double maxX = Double.NaN;

        for (ISeries series : getSeries()) {
            if (((Series) series).getXRange().lower < minX
                    || Double.isNaN(minX)) {
                minX = ((Series) series).getXRange().lower;
            }
            if (((Series) series).getXRange().upper < maxX
                    || Double.isNaN(maxX)) {
                maxX = ((Series) series).getXRange().upper;
            }
        }

        return new Range(minX, maxX);
    }

    /**
     * Gets the Y range of series.
     * 
     * @return the Y range of series
     */
    protected Range getYRange() {
        double minY = Double.NaN;
        double maxY = Double.NaN;

        for (ISeries series : getSeries()) {
            if (((Series) series).getYRange().lower < minY
                    || Double.isNaN(minY)) {
                minY = ((Series) series).getYRange().lower;
            }
            if (((Series) series).getYRange().upper > maxY
                    || Double.isNaN(maxY)) {
                maxY = ((Series) series).getYRange().upper;
            }
        }

        return new Range(minY, maxY);
    }

    /**
     * Compresses all series data.
     */
    public void compressAllSeries() {

        CompressConfig config = new CompressConfig();

        final int PRECISION = 2;
        Point p = chart.getPlotArea().getSize();
        int width = p.x * PRECISION;
        int height = p.y * PRECISION;
        config.setSizeInPixel(width, height);

        for (ISeries series : getSeries()) {
            int xAxisId = series.getXAxisId();
            int yAxisId = series.getYAxisId();

            IAxis xAxis = chart.getAxisSet().getXAxis(xAxisId);
            IAxis yAxis = chart.getAxisSet().getYAxis(yAxisId);
            if (xAxis == null || yAxis == null) {
                continue;
            }
            Range xRange = xAxis.getRange();
            Range yRange = yAxis.getRange();

            if (xRange == null || yRange == null) {
                continue;
            }

            double xMin = xRange.lower;
            double xMax = xRange.upper;
            double yMin = yRange.lower;
            double yMax = yRange.upper;

            config.setXLogScale(xAxis.isLogScaleEnabled());
            config.setYLogScale(yAxis.isLogScaleEnabled());

            double lower = xMin - (xMax - xMin) * 0.015;
            double upper = xMax + (xMax - xMin) * 0.015;
            if (xAxis.isLogScaleEnabled()) {
                lower = ((Series) series).getXRange().lower;
            }
            config.setXRange(lower, upper);
            lower = yMin - (yMax - yMin) * 0.015;
            upper = yMax + (yMax - yMin) * 0.015;
            if (yAxis.isLogScaleEnabled()) {
                lower = ((Series) series).getYRange().lower;
            }
            config.setYRange(lower, upper);

            ((Series) series).getCompressor().compress(config);
        }
    }

    /**
     * Updates the stack and riser data.
     */
    public void updateStackAndRiserData() {
        for (IAxis xAxis : chart.getAxisSet().getXAxes()) {
            for (IAxis yAxis : chart.getAxisSet().getYAxes()) {
                updateStackAndRiserData(xAxis, yAxis);
            }
        }
    }

    /**
     * Updates the stack and riser data for given axes.
     * 
     * @param xAxis
     *            the X axis
     * @param yAxis
     *            the Y axis
     */
    private void updateStackAndRiserData(IAxis xAxis, IAxis yAxis) {

        int riserCnt = 0;
        int stackRiserPosition = -1;
        double[] stackBarSeries = null;
        double[] stackLineSeries = null;

        if (((Axis) xAxis).isValidCategoryAxis()) {
            int size = xAxis.getCategorySeries().length;
            stackBarSeries = new double[size];
            stackLineSeries = new double[size];
        }

        for (ISeries series : getSeries()) {
            if (series.getXAxisId() != xAxis.getId()
                    || series.getYAxisId() != yAxis.getId()
                    || !series.isVisible()) {
                continue;
            }

            if (series.isStackEnabled() && ((Axis) xAxis).isValidCategoryAxis()) {
                if (series.getType() == SeriesType.BAR) {
                    if (stackRiserPosition == -1) {
                        stackRiserPosition = riserCnt;
                        riserCnt++;
                    }
                    ((BarSeries) series).setRiserIndex(stackRiserPosition);
                    setStackSeries(stackBarSeries, series);
                } else if (series.getType() == SeriesType.LINE) {
                    setStackSeries(stackLineSeries, series);
                }
            } else {
                if (series.getType() == SeriesType.BAR) {
                    ((BarSeries) series).setRiserIndex(riserCnt++);
                }
            }
        }

        ((Axis) xAxis).setNumRisers(riserCnt);
    }

    /**
     * Sets the stack series.
     * 
     * @param stackSeries
     *            the stack series
     * @param series
     *            the series
     */
    private void setStackSeries(double[] stackSeries, ISeries series) {
        double[] ySeries = series.getYSeries();
        for (int i = 0; i < stackSeries.length; i++) {
            if (i > ySeries.length) {
                break;
            }
            stackSeries[i] = new BigDecimal(new Double(stackSeries[i])
                    .toString()).add(
                    new BigDecimal(new Double(ySeries[i]).toString()))
                    .doubleValue();
        }
        double[] copiedStackSeries = new double[stackSeries.length];
        System.arraycopy(stackSeries, 0, copiedStackSeries, 0,
                stackSeries.length);
        ((Series) series).setStackSeries(copiedStackSeries);
    }
}
