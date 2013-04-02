/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

package uk.ac.ebi.masscascade.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class to parse the Ole date format.
 */
public class OleDate extends java.util.Date {

    private long ONE_DAY = 24L * 60 * 60 * 1000;

    /**
     * Returns the Ole date as <code> java.util.Date</code>.
     *
     * @param d an Ole date
     * @return the Java date
     */
    public Date getDate(double d) {

        return getDate(d, TimeZone.getDefault());
    }

    /**
     * Returns the Ole date as <code> java.util.Date</code>.
     *
     * @param d  an Ole date
     * @param tz a time zone
     * @return the Java date
     */
    public Date getDate(double d, TimeZone tz) {

        long wholeDays = (long) d;
        double fracDays = Math.abs(d - wholeDays);

        long offset = (ONE_DAY * wholeDays) + (long) (fracDays * ONE_DAY);

        Date base = baseFor(tz);
        return new Date(base.getTime() + offset);
    }

    private Date baseFor(TimeZone tz) {

        Calendar c = Calendar.getInstance(tz);
        c.clear();
        c.set(1899, 11, 30, 0, 0, 0);
        return c.getTime();
    }
}