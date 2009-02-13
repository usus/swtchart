package org.swtchart.internal.axis;

import org.eclipse.swt.SWT;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IGrid;
import org.swtchart.ISeries;
import org.swtchart.ITitle;
import org.swtchart.Range;
import org.swtchart.internal.Grid;
import org.swtchart.internal.series.Series;
import org.swtchart.internal.series.SeriesSet;

/**
 * An axis.
 */
public class Axis implements IAxis {

    /** the axis id */
    private int id;

    /** the axis direction */
    private Direction direction;

    /** the axis position */
    private Position position;

    /** the minimum value of axis range */
    private double min;

    /** the maximum value of axis range */
    private double max;

    /** the axis title */
    private AxisTitle title;

    /** the axis tick */
    private AxisTick tick;

    /** the grid */
    private Grid grid;

    /** the plot chart */
    private Chart chart;

    /** the state if the axis scale is log scale */
    private boolean logScaleEnabled;

    /** the ratio to be zoomed */
    private static final double ZOOM_RATIO = 0.2;

    /** the ratio to be scrolled */
    private static final double SCROLL_RATIO = 0.1;

    /** the maximum resolution with digits */
    private static final double MAX_RESOLUTION = 13;

    /** the state indicating if axis type is category */
    private boolean categoryAxisEnabled;

    /** the category series */
    private String[] categorySeries;

    /** the number of riser per category */
    private int numRisers;

    /** the margin in pixels */
    public final static int MARGIN = 5;

    /** the default minimum value of range */
    public final static double DEFAULT_MIN = 0d;

    /** the default maximum value of range */
    public final static double DEFAULT_MAX = 1d;

    /** the default minimum value of log scale range */
    public final static double DEFAULT_LOG_SCALE_MIN = 0.1d;

    /** the default maximum value of log scale range */
    public final static double DEFAULT_LOG_SCALE_MAX = 1d;

    /**
     * Constructor.
     * 
     * @param id
     *            the axis index
     * @param direction
     *            the axis direction (X or Y)
     * @param chart
     *            the chart
     */
    public Axis(int id, Direction direction, Chart chart) {
        this.id = id;
        this.direction = direction;
        this.chart = chart;

        grid = new Grid(this);
        title = new AxisTitle(chart, SWT.NONE, this, direction);
        tick = new AxisTick(chart, SWT.NONE, this);

        // sets initial default values
        position = Position.Primary;
        min = DEFAULT_MIN;
        max = DEFAULT_MAX;
        if (direction == Direction.X) {
            title.setText("X axis");
        } else if (direction == Direction.Y) {
            title.setText("Y axis");
        }
        logScaleEnabled = false;
        categoryAxisEnabled = false;
    }

    /*
     * @see IAxis#getId()
     */
    public int getId() {
        return id;
    }

    /*
     * @see IAxis#getDirection()
     */
    public Direction getDirection() {
        return direction;
    }

    /*
     * @see IAxis#getPosition()
     */
    public Position getPosition() {
        return position;
    }

    /*
     * @see IAxis#setPosition(Position)
     */
    public void setPosition(Position position) {
        if (position == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        if (this.position == position) {
            return;
        }

        this.position = position;

        chart.updateLayout();
    }

    /*
     * @see IAxis#setRange(Range)
     */
    public void setRange(Range range) {
        if (range == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
            return; // to suppress warnings...
        }

        if (Double.isNaN(range.lower) || Double.isNaN(range.upper)
                || range.lower > range.upper) {
            throw new IllegalArgumentException("Illegal range: " + range);
        }

        if (min == range.lower && max == range.upper) {
            return;
        }

        if (isValidCategoryAxis()) {
            min = (int) range.lower;
            max = (int) range.upper;
            if (min < 0) {
                min = 0;
            }
            if (max > categorySeries.length - 1) {
                max = categorySeries.length - 1;
            }
        } else {
            if (range.lower == range.upper) {
                throw new IllegalArgumentException("Given range is invalid");
            }

            if (logScaleEnabled && range.lower <= 0) {
                range.lower = min;
            }

            if (Math.abs(range.lower / (range.upper - range.lower)) > Math.pow(
                    10, MAX_RESOLUTION)) {
                return;
            }

            min = range.lower;
            max = range.upper;
        }

        chart.updateLayout();

        ((SeriesSet) chart.getSeriesSet()).compressAllSeries();
    }

    /*
     * @see IAxis#getRange()
     */
    public Range getRange() {
        return new Range(min, max);
    }

    /*
     * @see IAxis#getTitle()
     */
    public ITitle getTitle() {
        return title;
    }

    /*
     * @see IAxis#getTick()
     */
    public AxisTick getTick() {
        return tick;
    }

    /*
     * @see IAxis#enableLogScale(boolean)
     */
    public void enableLogScale(boolean enabled) throws IllegalStateException {

        if (logScaleEnabled == enabled) {
            return;
        }

        if (enabled) {
            if (chart.getSeriesSet().getSeries().length == 0) {
                if (min <= 0) {
                    min = DEFAULT_LOG_SCALE_MIN;
                }
                if (max < min) {
                    max = DEFAULT_LOG_SCALE_MAX;
                }
            } else {
                // check if series contain negative value
                double minSeriesValue = getMinSeriesValue();
                if (enabled && minSeriesValue <= 0) {
                    throw new IllegalStateException(
                            "Series contain negative value.");
                }

                // adjust the range in order not to have negative value
                if (min <= 0) {
                    min = minSeriesValue;
                }
            }

            // disable category axis
            categoryAxisEnabled = false;
        }

        logScaleEnabled = enabled;

        chart.updateLayout();

        ((SeriesSet) chart.getSeriesSet()).compressAllSeries();
    }

    /**
     * Gets the minimum value of series belonging to this axis.
     * 
     * @return the minimum value of series belonging to this axis
     */
    private double getMinSeriesValue() {
        double minimum = Double.MAX_VALUE;
        for (ISeries series : chart.getSeriesSet().getSeries()) {
            double lower;
            if (direction == Direction.X && series.getXAxisId() == getId()) {
                lower = ((Series) series).getXRange().lower;
            } else if (direction == Direction.Y
                    && series.getYAxisId() == getId()) {
                lower = ((Series) series).getYRange().lower;
            } else {
                continue;
            }

            if (lower < minimum) {
                minimum = lower;
            }
        }
        return minimum;
    }

    /*
     * @see IAxis#isLogScaleEnabled()
     */
    public boolean isLogScaleEnabled() {
        return logScaleEnabled;
    }

    /*
     * @see IAxis#getGrid()
     */
    public IGrid getGrid() {
        return grid;
    }

    /*
     * @see IAxis#adjustRange()
     */
    public void adjustRange() {
        if (isValidCategoryAxis()) {
            setRange(new Range(0, categorySeries.length - 1));
        } else {
            double minimum = Double.MAX_VALUE;
            double maximum = Double.MIN_VALUE;
            for (ISeries series : chart.getSeriesSet().getSeries()) {
                int axisId = direction == Direction.X ? series.getXAxisId()
                        : series.getYAxisId();
                if (!series.isVisible() || getId() != axisId) {
                    continue;
                }

                Range range;
                if (direction == Direction.X) {
                    range = ((Series) series)
                            .getXRangeToDraw(isLogScaleEnabled());
                } else {
                    range = ((Series) series)
                            .getYRangeToDraw(isLogScaleEnabled());
                }

                if (range.lower < minimum) {
                    minimum = range.lower;
                }
                if (range.upper > maximum) {
                    maximum = range.upper;
                }
            }
            setRange(new Range(minimum, maximum));
        }
    }

    /*
     * @see IAxis#zoomIn()
     */
    public void zoomIn() {
        double lower = min;
        double upper = max;
        if (isValidCategoryAxis()) {
            if (min != max) {
                lower = min + 1;
                if (min != max) {
                    upper = max - 1;
                }
            }
        } else if (isLogScaleEnabled()) {
            double digitMin = Math.log10(min);
            double digitMax = Math.log10(max);
            lower = Math.pow(10, digitMin + (digitMax - digitMin)
                    * SCROLL_RATIO);
            upper = Math.pow(10, digitMax - (digitMax - digitMin)
                    * SCROLL_RATIO);
        } else {
            lower = min + (max - min) * ZOOM_RATIO;
            upper = max - (max - min) * ZOOM_RATIO;
        }

        setRange(new Range(lower, upper));
    }

    /*
     * @see IAxis#zoomOut()
     */
    public void zoomOut() {
        double lower = min;
        double upper = max;
        if (isValidCategoryAxis()) {
            if (lower >= 1) {
                lower = min - 1;
            }
            if (upper < categorySeries.length - 1) {
                upper = max + 1;
            }
        } else if (isLogScaleEnabled()) {
            double digitMax = Math.log10(upper);
            double digitMin = Math.log10(lower);
            lower = Math.pow(10, digitMin - (digitMax - digitMin)
                    * SCROLL_RATIO);
            upper = Math.pow(10, digitMax + (digitMax - digitMin)
                    * SCROLL_RATIO);
        } else {
            lower = min - (max - min) / (1 - ZOOM_RATIO * 2) * ZOOM_RATIO;
            upper = max + (max - min) / (1 - ZOOM_RATIO * 2) * ZOOM_RATIO;
        }

        setRange(new Range(lower, upper));
    }

    /*
     * @see IAxis#scrollUp()
     */
    public void scrollUp() {
        double lower = min;
        double upper = max;
        if (isValidCategoryAxis()) {
            if (upper < categorySeries.length - 1) {
                lower = min + 1;
                upper = max + 1;
            }
        } else if (isLogScaleEnabled()) {
            double digitMax = Math.log10(upper);
            double digitMin = Math.log10(lower);
            upper = Math.pow(10, digitMax + (digitMax - digitMin)
                    * SCROLL_RATIO);
            lower = Math.pow(10, digitMin + (digitMax - digitMin)
                    * SCROLL_RATIO);
        } else {
            lower = min + (max - min) * SCROLL_RATIO;
            upper = max + (max - min) * SCROLL_RATIO;
        }

        setRange(new Range(lower, upper));
    }

    /*
     * @see IAxis#scrollDown()
     */
    public void scrollDown() {
        double lower = min;
        double upper = max;
        if (isValidCategoryAxis()) {
            if (lower >= 1) {
                lower = min - 1;
                upper = max - 1;
            }
        } else if (isLogScaleEnabled()) {
            double digitMax = Math.log10(upper);
            double digitMin = Math.log10(lower);
            upper = Math.pow(10, digitMax - (digitMax - digitMin)
                    * SCROLL_RATIO);
            lower = Math.pow(10, digitMin - (digitMax - digitMin)
                    * SCROLL_RATIO);
        } else {
            lower = min - (max - min) * SCROLL_RATIO;
            upper = max - (max - min) * SCROLL_RATIO;
        }

        setRange(new Range(lower, upper));
    }

    /**
     * Gets the state indicating if the axis is valid category axis.
     * 
     * @return true if the axis is valid category axis
     */
    public boolean isValidCategoryAxis() {
        return categoryAxisEnabled && categorySeries != null
                && categorySeries.length != 0;
    }

    /*
     * @see IAxis#isCategoryEnabled()
     */
    public boolean isCategoryEnabled() {
        return categoryAxisEnabled;
    }

    /*
     * @see IAxis#enableCategory(boolean)
     */
    public void enableCategory(boolean enabled) {
        if (categoryAxisEnabled == enabled) {
            return;
        }

        if (direction == Direction.Y) {
            throw new IllegalStateException("Y axis cannot be category axis.");
        }

        categoryAxisEnabled = enabled;

        if (isValidCategoryAxis()) {
            min = (min < 0) ? 0 : (int) min;
            max = (max >= categorySeries.length) ? max = categorySeries.length - 1
                    : (int) max;
        }

        chart.updateLayout();

        ((SeriesSet) chart.getSeriesSet()).updateStackAndRiserData();
    }

    /*
     * @see IAxis#setCategorySeries(String[])
     */
    public void setCategorySeries(String[] series) {
        if (series == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
            return; // to suppress warnings...
        }

        if (direction == Direction.Y) {
            throw new IllegalStateException("Y axis cannot be category axis.");
        }

        String[] copiedSeries = new String[series.length];
        System.arraycopy(series, 0, copiedSeries, 0, series.length);
        categorySeries = copiedSeries;

        if (isValidCategoryAxis()) {
            min = (min < 0) ? 0 : (int) min;
            max = (max >= categorySeries.length) ? max = categorySeries.length - 1
                    : (int) max;
        }

        chart.updateLayout();

        ((SeriesSet) chart.getSeriesSet()).updateStackAndRiserData();
    }

    /*
     * @see IAxis#getCategorySeries()
     */
    public String[] getCategorySeries() {

        String[] copiedCategorySeries = null;

        if (categorySeries != null) {
            copiedCategorySeries = new String[categorySeries.length];
            System.arraycopy(categorySeries, 0, copiedCategorySeries, 0,
                    categorySeries.length);
        }

        return copiedCategorySeries;
    }

    /*
     * @see IAxis#getPixelCoordinate(double)
     */
    @Override
    public int getPixelCoordinate(double dataCoordinate) {
        int pixelCoordinate;
        if (isHorizontalAxis()) {
            int width = chart.getPlotArea().getBounds().width;

            if (logScaleEnabled) {
                pixelCoordinate = (int) ((Math.log10(dataCoordinate) - Math
                        .log10(min))
                        / (Math.log10(max) - Math.log10(min)) * width);
            } else if (categoryAxisEnabled) {
                pixelCoordinate = (int) ((dataCoordinate + 0.5 - min)
                        / (max + 1 - min) * width);
            } else {
                pixelCoordinate = (int) ((dataCoordinate - min) / (max - min) * width);
            }
        } else {
            int height = chart.getPlotArea().getBounds().height;

            if (logScaleEnabled) {
                pixelCoordinate = (int) ((Math.log10(max) - Math
                        .log10(dataCoordinate))
                        / (Math.log10(max) - Math.log10(min)) * height);
            } else if (categoryAxisEnabled) {
                pixelCoordinate = (int) ((max - dataCoordinate + 0.5)
                        / (max + 1 - min) * height);
            } else {
                pixelCoordinate = (int) ((max - dataCoordinate) / (max - min) * height);
            }
        }
        return pixelCoordinate;
    }

    /*
     * @see IAxis#getDataCoordinate(int)
     */
    @Override
    public double getDataCoordinate(int pixelCoordinate) {
        double dataCoordinate;
        if (isHorizontalAxis()) {
            int width = chart.getPlotArea().getBounds().width;

            if (logScaleEnabled) {
                dataCoordinate = Math
                        .pow(10, pixelCoordinate / (double) width
                                * (Math.log10(max) - Math.log10(min))
                                + Math.log10(min));
            } else if (categoryAxisEnabled) {
                dataCoordinate = pixelCoordinate / (double) width
                        * (max + 1 - min) + min - 0.5;
            } else {
                dataCoordinate = pixelCoordinate / (double) width * (max - min)
                        + min;
            }
        } else {
            int height = chart.getPlotArea().getBounds().height;

            if (logScaleEnabled) {
                dataCoordinate = Math
                        .pow(10, Math.log10(max) - pixelCoordinate
                                / (double) height
                                * (Math.log10(max) - Math.log10(min)));
            } else if (categoryAxisEnabled) {
                dataCoordinate = max + 0.5 - pixelCoordinate / (double) height
                        * (max + 1 - min);
            } else {
                dataCoordinate = (height - pixelCoordinate) / (double) height
                        * (max - min) + min;
            }
        }
        return dataCoordinate;
    }

    /**
     * Sets the number of risers per category.
     * 
     * @param numRisers
     *            the number of risers per category
     */
    public void setNumRisers(int numRisers) {
        this.numRisers = numRisers;
    }

    /**
     * Gets the number of risers per category.
     * 
     * @return number of riser per category
     */
    public int getNumRisers() {
        return numRisers;
    }

    /**
     * Checks if the axis is horizontal. X axis is not always horizontal. Y axis
     * can be horizontal with <tt>Chart.setOrientation(SWT.VERTICAL)</tt>.
     * 
     * @return true if the axis is horizontal
     */
    public boolean isHorizontalAxis() {
        int orientation = chart.getOrientation();
        return (direction == Direction.X && orientation == SWT.HORIZONTAL)
                || (direction == Direction.Y && orientation == SWT.VERTICAL);
    }

    /**
     * Disposes the OS resources.
     */
    protected void dispose() {
        tick.getAxisTickLabels().dispose();
        tick.getAxisTickMarks().dispose();
        title.dispose();
    }

    /**
     * Updates the layout data.
     */
    public void updateLayoutData() {
        title.updateLayoutData();
        tick.updateLayoutData();
    }

    /**
     * Gets the state indicating if date is enabled.
     * 
     * @return true if date is enabled
     */
    public boolean isDateEnabled() {
        if (!isHorizontalAxis()) {
            return false;
        }

        for (ISeries series : chart.getSeriesSet().getSeries()) {
            if (series.getXAxisId() != id) {
                continue;
            }

            if (((Series) series).isDateSeries() && series.isVisible()) {
                return true;
            }
        }
        return false;
    }
}
