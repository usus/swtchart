package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.IAxis.Direction;

/**
 * The tick page on properties dialog.
 */
public class AxisTickPage extends AbstractSelectorPage {

	private IAxis[] axes;

	private Button showTickButton;

	private Label fontSizeLabel;

	private Spinner fontSizeSpinner;

	private Label foregroundLabel;

	private ColorSelector foregroundButton;

	private boolean[] visibilityStates;

	private int[] fontSizes;

	private Color[] foregroundColors;

	/**
	 * Constructor.
	 * 
	 * @param chart
	 *            the chart
	 * @param direction
	 *            the direction
	 * @param title
	 *            the title
	 */
	public AxisTickPage(Chart chart, Direction direction, String title) {
		super(chart, title, "Axes:");
		if (direction == Direction.X) {
			this.axes = chart.getAxisSet().getXAxes();
		} else if (direction == Direction.Y) {
			this.axes = chart.getAxisSet().getYAxes();
		}

		visibilityStates = new boolean[axes.length];
		fontSizes = new int[axes.length];
		foregroundColors = new Color[axes.length];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.ext.internal.properties.AbstractSelectorPage#getListItems()
	 */
	@Override
	protected String[] getListItems() {
		String[] items = new String[axes.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = String.valueOf(axes[i].getId());
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.ext.internal.properties.AbstractSelectorPage#selectInitialValues
	 * ()
	 */
	@Override
	protected void selectInitialValues() {
		for (int i = 0; i < axes.length; i++) {
			visibilityStates[i] = axes[i].getTick().isVisible();
			fontSizes[i] = axes[i].getTick().getFont().getFontData()[0]
					.getHeight();
			foregroundColors[i] = axes[i].getTick().getForeground();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.swtchart.ext.internal.properties.AbstractSelectorPage#
	 * updateControlSelections()
	 */
	@Override
	protected void updateControlSelections() {
		showTickButton.setSelection(visibilityStates[selectedIndex]);
		setControlsEnable(visibilityStates[selectedIndex]);
		fontSizeSpinner.setSelection(fontSizes[selectedIndex]);
		foregroundButton
				.setColorValue(foregroundColors[selectedIndex].getRGB());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.swtchart.ext.internal.properties.AbstractSelectorPage#
	 * addRightPanelContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addRightPanelContents(Composite parent) {
		addTickPanel(parent);
	}

	/**
	 * Create the tick panel.
	 * 
	 * @param parent
	 *            the parent to add the tick panel
	 */
	private void addTickPanel(Composite composite) {
		Composite group = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, true));

		showTickButton = createCheckBoxControl(group, "Show tick");
		showTickButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean visible = showTickButton.getSelection();
				visibilityStates[selectedIndex] = visible;
				setControlsEnable(visible);
			}
		});

		fontSizeLabel = createLabelControl(group, "Font size:");
		fontSizeSpinner = createSpinnerControl(group, 8, 30);
		fontSizeSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fontSizes[selectedIndex] = fontSizeSpinner.getSelection();
			}
		});

		foregroundLabel = createLabelControl(group, "Color:");
		foregroundButton = createColorButtonControl(group);
		foregroundButton.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				foregroundColors[selectedIndex] = new Color(Display
						.getDefault(), foregroundButton.getColorValue());
			}
		});
	}

	/**
	 * Sets the enable state of controls.
	 * 
	 * @param enabled
	 *            true if controls are enabled
	 */
	private void setControlsEnable(boolean enabled) {
		fontSizeLabel.setEnabled(enabled);
		fontSizeSpinner.setEnabled(enabled);
		foregroundLabel.setEnabled(enabled);
		foregroundButton.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.preference.AbstractPreferencePage#apply()
	 */
	@Override
	public void apply() {
		for (int i = 0; i < axes.length; i++) {
			axes[i].getTick().setVisible(visibilityStates[i]);
			FontData fontData = axes[i].getTick().getFont().getFontData()[0];
			Font font = new Font(axes[i].getTick().getFont().getDevice(),
					fontData.getName(), fontSizes[i], fontData.getStyle());
			axes[i].getTick().setFont(font);
			axes[i].getTick().setForeground(foregroundColors[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		visibilityStates[selectedIndex] = true;
		fontSizes[selectedIndex] = Constants.SMALL_FONT_SIZE;
		foregroundColors[selectedIndex] = new Color(Display.getDefault(),
				Constants.BLUE);

		updateControlSelections();

		super.performDefaults();
	}
}
