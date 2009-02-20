/*******************************************************************************
 * Copyright (c) 2008-2009 SWTChart project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.swtchart;

import org.swtchart.ISeries.SeriesType;

/**
 * A series container.
 */
public interface ISeriesSet {

    /**
     * Creates the series. If series for given id already exists, the existing
     * series will be overwritten.
     * 
     * @param type
     *            the series type
     * @param id
     *            the id for series
     * @return the series
     */
    ISeries createSeries(SeriesType type, String id);

    /**
     * Gets the series for given id.
     * 
     * @param id
     *            the id for series
     * @return the series, or null if series doesn't exist for the given id.
     */
    ISeries getSeries(String id);

    /**
     * Gets the array of series
     * 
     * @return the array of series
     */
    ISeries[] getSeries();

    /**
     * Deletes the series for given id.
     * 
     * @param id
     *            the series id
     * @throws IllegalArgumentException
     *             if there is no series for the given id.
     */
    void deleteSeries(String id);
}