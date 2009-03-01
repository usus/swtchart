package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ISeries;

/**
 * The series label page on properties dialog.
 */
public class SeriesLabelPage extends AbstractSelectorPage {

    /** the series array */
    private ISeries[] series;

    /** the show label button */
    protected Button showLabelButton;

    /** the color label */
    private Label colorLabel;

    /** the color button */
    protected ColorSelector colorButton;

    /** the font size label */
    private Label fontSizeLabel;

    /** the font size spinner */
    protected Spinner fontSizeSpinner;

    /** the states indicating the visibility of series */
    protected boolean[] visibleStates;

    /** the colors */
    protected Color[] colors;

    /** the font size */
    protected int[] fontSizes;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param axes
     *            the axes
     * @param title
     *            the title
     */
    public SeriesLabelPage(Chart chart, String title) {
        super(chart, title, "Series:");

        series = chart.getSeriesSet().getSeries();

        visibleStates = new boolean[series.length];
        colors = new Color[series.length];
        fontSizes = new int[series.length];
    }

    /*
     * @see AbstractSelectorPage#getListItems()
     */
    @Override
    protected String[] getListItems() {
        String[] items = new String[series.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = String.valueOf(series[i].getId());
        }
        return items;
    }

    /*
     * @see AbstractSelectorPage#selectInitialValues()
     */
    @Override
    protected void selectInitialValues() {
        for (int i = 0; i < series.length; i++) {
            visibleStates[i] = series[i].getLabel().isVisible();
            colors[i] = series[i].getLabel().getForeground();
            fontSizes[i] = series[i].getLabel().getFont().getFontData()[0]
                    .getHeight();
        }
    }

    /*
     * @see AbstractSelectorPage#updateControlSelections()
     */
    @Override
    protected void updateControlSelections() {
        showLabelButton.setSelection(visibleStates[selectedIndex]);
        setControlsEnable(visibleStates[selectedIndex]);
        colorButton.setColorValue(colors[selectedIndex].getRGB());
        fontSizeSpinner.setSelection(fontSizes[selectedIndex]);
    }

    /*
     * @see AbstractSelectorPage#addRightPanelContents(Composite)
     */
    @Override
    protected void addRightPanelContents(Composite parent) {
        addSeriesLabelPanel(parent);
    }

    /**
     * Adds the series label panel.
     * 
     * @param parent
     *            the parent to add the series label panel
     */
    private void addSeriesLabelPanel(Composite parent) {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));

        showLabelButton = createCheckBoxControl(group, "Show label");
        showLabelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean visible = showLabelButton.getSelection();
                visibleStates[selectedIndex] = visible;
                setControlsEnable(visible);
            }
        });

        colorLabel = createLabelControl(group, "Color:");
        colorButton = createColorButtonControl(group);
        colorButton.addListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                colors[selectedIndex] = new Color(Display.getDefault(),
                        colorButton.getColorValue());
            }
        });

        fontSizeLabel = createLabelControl(group, "Font size:");
        fontSizeSpinner = createSpinnerControl(group, 8, 30);
        fontSizeSpinner.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                fontSizes[selectedIndex] = fontSizeSpinner.getSelection();
            }
        });
    }

    /**
     * Sets the enable state of controls.
     * 
     * @param enabled
     *            true if controls are enabled
     */
    protected void setControlsEnable(boolean enabled) {
        colorLabel.setEnabled(enabled);
        colorButton.setEnabled(enabled);
        fontSizeLabel.setEnabled(enabled);
        fontSizeSpinner.setEnabled(enabled);
    }

    /*
     * @see AbstractPreferencePage#apply()
     */
    @Override
    public void apply() {
        for (int i = 0; i < series.length; i++) {
            series[i].getLabel().setVisible(visibleStates[i]);
            series[i].getLabel().setForeground(colors[i]);
            FontData fontData = series[i].getLabel().getFont().getFontData()[0];
            Font font = new Font(series[i].getLabel().getFont().getDevice(),
                    fontData.getName(), fontSizes[i], fontData.getStyle());
            series[i].getLabel().setFont(font);
        }
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        visibleStates[selectedIndex] = false;
        colors[selectedIndex] = new Color(Display.getDefault(), Constants.BLACK);
        fontSizes[selectedIndex] = Constants.SMALL_FONT_SIZE;

        updateControlSelections();

        super.performDefaults();
    }
}
