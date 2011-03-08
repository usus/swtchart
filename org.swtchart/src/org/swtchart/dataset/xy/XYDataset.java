/**
 *
 */
package org.swtchart.dataset.xy;

import java.util.ArrayList;

/**
 * @author nbraun
 *
 */
public class XYDataset implements IXYDataset {

	ArrayList<Double> valuesX;
	ArrayList<Double> valuesY;

	public XYDataset() {
		valuesX = new ArrayList<Double>();
		valuesY = new ArrayList<Double>();
	}

	@Override
	public void add(double x, double y) {
		valuesX.add(x);
		valuesY.add(y);
	}

	@Override
	public double[] getXvalues() {
		double[] ret = new double[valuesX.size()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = valuesX.get(i);
		}

		return ret;
	}

	@Override
	public double[] getYvalues() {
		double[] ret = new double[valuesY.size()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = valuesY.get(i);
		}
		return ret;
	}

	@Override
	public int size() {
		return valuesX.size();
	}

}
