package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.LineStyle;
import org.swtchart.IAxis.Direction;

/**
 * The grid page on properties dialog.
 */
public class GridPage extends AbstractSelectorPage {

	private IAxis[] axes;

	private Combo styleCombo;

	private ColorSelector foregroundButton;

	private LineStyle[] styles;

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
	public GridPage(Chart chart, Direction direction, String title) {
		super(chart, title, "Axes:");
		if (direction == Direction.X) {
			this.axes = chart.getAxisSet().getXAxes();
		} else if (direction == Direction.Y) {
			this.axes = chart.getAxisSet().getYAxes();
		}
		styles = new LineStyle[axes.length];
		foregroundColors = new Color[axes.length];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.properties.AbstractSelectorPage#getListItems()
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
	 * @see org.swtchart.ext.internal.properties.AbstractSelectorPage#selectInitialValues()
	 */
	@Override
	protected void selectInitialValues() {
		for (int i = 0; i < axes.length; i++) {
			styles[i] = axes[i].getGrid().getStyle();
			foregroundColors[i] = axes[i].getGrid().getForeground();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.swtchart.ext.internal.properties.AbstractSelectorPage#updateControlSelections()
	 */
	@Override
	protected void updateControlSelections() {
		styleCombo.setText(String.valueOf(styles[selectedIndex]));
		foregroundButton.setColorValue(foregroundColors[selectedIndex].getRGB());
	}

	/*
	 * (non-Javadoc)
	 * @see org.swtchart.ext.internal.properties.AbstractSelectorPage#addRightPanelContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addRightPanelContents(Composite parent) {
		addGridPanel(parent);
	}

	/**
	 * Adds the grid panel.
	 * 
	 * @param parent
	 *            the parent to add the grid panel
	 */
	private void addGridPanel(Composite parent) {
		Composite group = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, true));

		createLabelControl(group, "Line style:");
		LineStyle[] values = LineStyle.values();
		String[] labels = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			labels[i] = values[i].label;
		}
		styleCombo = createComboControl(group, labels);
		styleCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String value = styleCombo.getText();
				LineStyle selectedStyle = LineStyle.NONE;
				for (LineStyle style : LineStyle.values()) {
					if (style.label.equals(value)) {
						selectedStyle = style;
					}
				}
				styles[selectedIndex] = selectedStyle;
			}
		});

		createLabelControl(group, "Color:");
		foregroundButton = createColorButtonControl(group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.preference.AbstractPreferencePage#apply()
	 */
	@Override
	public void apply() {
		for (int i = 0; i < axes.length; i++) {
			axes[i].getGrid().setStyle(styles[i]);
			axes[i].getGrid().setForeground(foregroundColors[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		styles[selectedIndex] = LineStyle.DOT;
		foregroundColors[selectedIndex] = Constants.GRAY;

		updateControlSelections();
		
		super.performDefaults();
	}
}
