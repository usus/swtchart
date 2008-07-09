package org.swtchart.ext.internal.properties;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis;
import org.swtchart.Range;
import org.swtchart.IAxis.Direction;
import org.swtchart.IAxis.Position;

/**
 * The axis page on properties dialog.
 */
public class AxisPage extends AbstractSelectorPage {

	private IAxis[] axes;

	private Direction direction;

	private Button showTitleButton;

	private Label titleLabel;

	private Text titleText;

	private Label fontSizeLabel;

	private Spinner fontSizeSpinner;

	private Label titleColorLabel;

	private ColorSelector titleColorButton;

	private Text minRangeText;

	private Text maxRangeText;

	private Combo positionCombo;

	private Button categoryButton;

	private Button logScaleButton;

	private boolean[] titleVisibleStates;

	private String[] titleTexts;

	private int[] titleFontSizes;

	private Color[] titleColors;

	private double[] minRanges;

	private double[] maxRanges;

	private Position[] positions;

	private boolean[] categoryStates;

	private boolean[] logScaleStates;

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
	public AxisPage(Chart chart, Direction direction, String title) {
		super(chart, title, "Axes:");
		this.direction = direction;
		if (direction == Direction.X) {
			this.axes = chart.getAxisSet().getXAxes();
		} else if (direction == Direction.Y) {
			this.axes = chart.getAxisSet().getYAxes();
		}

		titleVisibleStates = new boolean[axes.length];
		titleTexts = new String[axes.length];
		titleFontSizes = new int[axes.length];
		titleColors = new Color[axes.length];
		minRanges = new double[axes.length];
		maxRanges = new double[axes.length];
		positions = new Position[axes.length];
		if (direction == Direction.X) {
			categoryStates = new boolean[axes.length];
		}
		logScaleStates = new boolean[axes.length];
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
			titleVisibleStates[i] = axes[i].getTitle().isVisible();
			titleTexts[i] = axes[i].getTitle().getText();
			titleFontSizes[i] = axes[i].getTitle().getFont().getFontData()[0]
					.getHeight();
			titleColors[i] = axes[i].getTitle().getForeground();
			minRanges[i] = axes[i].getRange().lower;
			maxRanges[i] = axes[i].getRange().upper;
			positions[i] = axes[i].getPosition();
			if (direction == Direction.X) {
				categoryStates[i] = axes[i].isCategoryEnabled();
			}
			logScaleStates[i] = axes[i].isLogScaleEnabled();
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
		showTitleButton.setSelection(titleVisibleStates[selectedIndex]);
		setControlsEnable(titleVisibleStates[selectedIndex]);
		titleText.setText(titleTexts[selectedIndex]);
		fontSizeSpinner.setSelection(titleFontSizes[selectedIndex]);
		titleColorButton.setColorValue(titleColors[selectedIndex].getRGB());

		minRangeText.setText(String.valueOf(minRanges[selectedIndex]));
		maxRangeText.setText(String.valueOf(maxRanges[selectedIndex]));
		positionCombo.setText(String.valueOf(positions[selectedIndex]));
		logScaleButton.setSelection(logScaleStates[selectedIndex]);
		if (direction == Direction.X) {
			categoryButton.setSelection(categoryStates[selectedIndex]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.swtchart.ext.internal.properties.AbstractSelectorPage#
	 * addRightPanelContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addRightPanelContents(Composite parent) {
		addAxisPanel(parent);
		addTitleGroup(parent);
	}

	/**
	 * Adds axis panel.
	 * 
	 * @param parent
	 *            the parent to add the axis panel
	 */
	private void addAxisPanel(Composite parent) {
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, true));

		createLabelControl(group, "Minimum range value:");
		minRangeText = createTextControl(group);
		minRangeText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent e) {
				minRanges[selectedIndex] = Double.valueOf(minRangeText
						.getText());
			}
		});

		createLabelControl(group, "Maximum range value:");
		maxRangeText = createTextControl(group);
		maxRangeText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// do nothing
			}

			@Override
			public void focusLost(FocusEvent e) {
				maxRanges[selectedIndex] = Double.valueOf(maxRangeText
						.getText());
			}
		});

		createLabelControl(group, "Position:");
		String[] items = new String[] { Position.Primary.name(),
				Position.Seconday.name() };

		positionCombo = createComboControl(group, items);
		positionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				positions[selectedIndex] = Position.valueOf(positionCombo
						.getText());
			}
		});

		logScaleButton = createCheckBoxControl(group, "Enable log scale");
		logScaleButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logScaleStates[selectedIndex] = logScaleButton.getSelection();
			}
		});

		if (direction == Direction.X) {
			categoryButton = createCheckBoxControl(group, "Enable category");
			categoryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					categoryStates[selectedIndex] = categoryButton
							.getSelection();
				}
			});
		}
	}

	/**
	 * Adds title group.
	 * 
	 * @param parent
	 *            the parent to add the title group
	 */
	private void addTitleGroup(Composite parent) {

		Group group = createGroupControl(parent, "Title:", false);

		showTitleButton = createCheckBoxControl(group, "Show title");
		showTitleButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean visible = showTitleButton.getSelection();
				titleVisibleStates[selectedIndex] = visible;
				setControlsEnable(visible);
			}
		});

		titleLabel = createLabelControl(group, "Text:");
		titleText = createTextControl(group);
		titleText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				titleTexts[selectedIndex] = titleText.getText();
			}
		});

		fontSizeLabel = createLabelControl(group, "Font size:");
		fontSizeSpinner = createSpinnerControl(group, 8, 30);
		fontSizeSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				titleFontSizes[selectedIndex] = fontSizeSpinner.getSelection();
			}
		});

		titleColorLabel = createLabelControl(group, "Color:");
		titleColorButton = createColorButtonControl(group);
		titleColorButton.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				titleColors[selectedIndex] = new Color(Display.getDefault(),
						titleColorButton.getColorValue());
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
		titleLabel.setEnabled(enabled);
		titleText.setEnabled(enabled);
		fontSizeLabel.setEnabled(enabled);
		fontSizeSpinner.setEnabled(enabled);
		titleColorLabel.setEnabled(enabled);
		titleColorButton.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ext.internal.preference.AbstractPreferencePage#apply()
	 */
	@Override
	public void apply() {
		for (int i = 0; i < axes.length; i++) {
			axes[i].getTitle().setVisible(titleVisibleStates[i]);
			axes[i].getTitle().setText(titleTexts[i]);
			FontData fontData = axes[i].getTitle().getFont().getFontData()[0];
			Font font = new Font(axes[i].getTitle().getFont().getDevice(),
					fontData.getName(), titleFontSizes[i], fontData.getStyle());
			axes[i].getTitle().setFont(font);
			axes[i].getTitle().setForeground(titleColors[i]);
			axes[i].setRange(new Range(minRanges[i], maxRanges[i]));
			axes[i].setPosition(positions[i]);
			try {
				axes[i].enableLogScale(logScaleStates[i]);
			} catch (IllegalStateException e) {
				axes[i].enableLogScale(false);
				logScaleButton.setSelection(false);
			}
			if (direction == Direction.X) {
				axes[i].enableCategory(categoryStates[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		titleVisibleStates[selectedIndex] = true;
		if (direction == Direction.X) {
			titleTexts[selectedIndex] = "X Axis";
			categoryStates[selectedIndex] = false;
		} else if (direction == Direction.Y) {
			titleTexts[selectedIndex] = "Y Axis";
		}
		positions[selectedIndex] = Position.Primary;
		titleFontSizes[selectedIndex] = Constants.MEDIUM_FONT_SIZE;
		titleColors[selectedIndex] = new Color(Display.getDefault(),
				Constants.BLUE);
		minRanges[selectedIndex] = 0.0;
		maxRanges[selectedIndex] = 1.0;
		logScaleStates[selectedIndex] = false;

		updateControlSelections();

		super.performDefaults();
	}
}
