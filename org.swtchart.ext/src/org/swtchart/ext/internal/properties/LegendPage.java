package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ILegend;

/**
 * The legend property page on properties dialog.
 */
public class LegendPage extends AbstractPage {

	private Button showLegendButton;

	private Label backgroundLabel;

	private ColorSelector backgroundButton;

	private Label foregroundLabel;

	private ColorSelector foregroundButton;

	private Label fontSizeLabel;

	private Spinner fontSizeSpinner;

	private ILegend legend;

	/**
	 * Constructor.
	 * 
	 * @param chart
	 *            the chart
	 * @param title
	 *            the title
	 */
	public LegendPage(Chart chart, String title) {
		super(chart, title);
		legend = chart.getLegend();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		addLegendPanel(composite);

		selectValues();
		return composite;
	}

	/**
	 * Adds the legend panel.
	 * 
	 * @param parent
	 *            the parent to add the legend panel
	 */
	private void addLegendPanel(Composite parent) {

		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, true));

		showLegendButton = createCheckBoxControl(group, "Show legend");
		showLegendButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean visible = showLegendButton.getSelection();
				setControlsEnable(visible);
			}
		});

		backgroundLabel = createLabelControl(group, "Background:");
		backgroundButton = createColorButtonControl(group);

		foregroundLabel = createLabelControl(group, "Foreground:");
		foregroundButton = createColorButtonControl(group);

		fontSizeLabel = createLabelControl(group, "Font size:");
		fontSizeSpinner = createSpinnerControl(group, 8, 30);
	}

	/**
	 * Selects the values for controls.
	 */
	private void selectValues() {
		showLegendButton.setSelection(legend.isVisible());
		setControlsEnable(legend.isVisible());
		backgroundButton.setColorValue(legend.getBackground().getRGB());
		foregroundButton.setColorValue(legend.getForeground().getRGB());
		fontSizeSpinner.setSelection(legend.getFont().getFontData()[0]
				.getHeight());
	}

	/**
	 * Sets the enable state of controls.
	 * 
	 * @param enabled
	 *            true if controls are enabled
	 */
	private void setControlsEnable(boolean enabled) {
		backgroundLabel.setEnabled(enabled);
		backgroundButton.setEnabled(enabled);
		foregroundLabel.setEnabled(enabled);
		foregroundButton.setEnabled(enabled);
		fontSizeLabel.setEnabled(enabled);
		fontSizeSpinner.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.preference.AbstractPreferencePage#apply()
	 */
	@Override
	public void apply() {
		legend.setVisible(showLegendButton.getSelection());

		legend.setBackground(new Color(Display.getDefault(), backgroundButton
				.getColorValue()));
		legend.setForeground(new Color(Display.getDefault(), foregroundButton
				.getColorValue()));
		FontData fontData = legend.getFont().getFontData()[0];
		Font font = new Font(legend.getFont().getDevice(), fontData.getName(),
				fontSizeSpinner.getSelection(), fontData.getStyle());
		legend.setFont(font);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		showLegendButton.setSelection(true);
		setControlsEnable(true);
		
		backgroundButton.setColorValue(Constants.WHITE);
		foregroundButton.setColorValue(Constants.BLACK);
		fontSizeSpinner.setSelection(Constants.SMALL_FONT_SIZE);
		
		super.performDefaults();
	}
}
