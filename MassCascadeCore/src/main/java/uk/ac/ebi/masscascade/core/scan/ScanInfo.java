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

package uk.ac.ebi.masscascade.core.scan;

/**
 * Class holding meta information about the mass spectrometry file.
 */
public class ScanInfo {

    private String date;
    private final String authors;
    private final String id;

    /**
     * Constructs the scan file info container.
     *
     * @param id      name of the sample
     * @param authors authors names
     * @param date    creation date
     */
    public ScanInfo(String id, String authors, String date) {

        this.id = id;
        this.authors = authors;
        this.date = date;
    }

    /**
     * Sets the creation date.
     *
     * @param date the creation date
     */
    public void setDate(String date) {

        this.date = date;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public String getDate() {

        return date;
    }

    /**
     * Gets the authors' names.
     *
     * @return the authors' names
     */
    public final String getAuthors() {

        return authors;
    }

    /**
     * Gets the id of the file.
     *
     * @return the sample name
     */
    public final String getId() {

        return id;
    }

    /**
     * Checks if the information about the mass spec file is identical.
     *
     * @param aScanFileInfo the mass spec information to be compared to
     * @return boolean if informatino is identical
     */
    @Override
    public boolean equals(Object aScanFileInfo) {

        if (this == aScanFileInfo) return true;

        if (!(aScanFileInfo instanceof ScanInfo)) return false;

        ScanInfo scanInfo = (ScanInfo) aScanFileInfo;

        return this.id.equals(scanInfo.getId()) &&
                this.date.equals(scanInfo.getDate()) &&
                this.authors.equals(scanInfo.getAuthors());
    }

    /**
     * Returns the hash code of the mass spec info object.
     *
     * @return the value
     */
    @Override
    public int hashCode() {

        int hash = 1;

        hash = hash * 17 + id.hashCode();
        hash = hash * 17 + date.hashCode();
        hash = hash * 17 + authors.hashCode();

        return hash;
    }
}
