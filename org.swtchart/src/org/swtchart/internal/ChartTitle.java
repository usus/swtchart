package org.swtchart.internal;

import org.swtchart.Chart;

/**
 * A chart title.
 */
public class ChartTitle extends Title {

    /** the default text */
    private static final String DEFAULT_TEXT = "Chart Title";

    /**
     * Constructor.
     * 
     * @param chart
     *            the plot chart
     * @param style
     *            the style
     */
    public ChartTitle(Chart chart, int style) {
        super(chart, style);
    }

    /*
     * @see Title#getDefaultText()
     */
    @Override
    protected String getDefaultText() {
        return DEFAULT_TEXT;
    }
}
