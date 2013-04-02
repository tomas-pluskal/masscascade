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

package uk.ac.ebi.masscascade.core.raw;

import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;

/**
 * Class describing one MSn level in the raw file.
 */
public class RawLevel {

    private final Constants.ION_MODE ionMode;
    private final Constants.ACQUISITION_MODE acqusitionMode;
    private final Range scanRange;
    private final Range mzRange;
    private final double fragmentationEnergy;
    private final Constants.MSN msn;

    /**
     * Builder class supporting correct construction of the experimental level object.
     */
    public static class Builder {

        // required parameters
        private final Range scanRange;
        private final Range mzRange;
        private final Constants.ION_MODE ionMode;

        // optional parameters
        private Constants.ACQUISITION_MODE acqusitionMode = null;
        private double fragmentationEnergy = 0.0;
        private Constants.MSN msn = Constants.MSN.MS1;

        /**
         * Constructs minimal experimental level object.
         *
         * @param scanRange the scan range in the time domain
         * @param mzRange   the mass to charge range in the mz domain
         * @param ionMode   an ion mode parameter
         */
        public Builder(Range scanRange, Range mzRange, Constants.ION_MODE ionMode) {

            this.scanRange = scanRange;
            this.mzRange = mzRange;
            this.ionMode = ionMode;
        }

        /**
         * Adds an acquisition mode parameter to the level.
         *
         * @param acqusitionMode the acquisition mode
         * @return the compiled builder
         */
        public Builder acquistionMode(Constants.ACQUISITION_MODE acqusitionMode) {

            this.acqusitionMode = acqusitionMode;
            return this;
        }

        /**
         * Adds a fragment energy parameter to the level.
         *
         * @param fragmentationEnergy the fragmentation energy
         * @return the compiled builder
         */
        public Builder fragmentationEnergy(double fragmentationEnergy) {

            this.fragmentationEnergy = fragmentationEnergy;
            return this;
        }

        /**
         * Adds a mass spectrometry level number to the level.
         *
         * @param msn the MSn level
         * @return the compilder builder
         */
        public Builder msLevel(Constants.MSN msn) {
            this.msn = msn;
            return this;
        }

        /**
         * Constructs the experimental level using the provided parameters.
         *
         * @return the complete experimental setup level
         */
        public RawLevel build() {

            return new RawLevel(this);
        }
    }

    /**
     * Class holding all information about one mass spectrometry acquisition level.
     *
     * @param builder a builder class supporting the construction process
     */
    private RawLevel(Builder builder) {

        this.ionMode = builder.ionMode;
        this.acqusitionMode = builder.acqusitionMode;
        this.msn = builder.msn;
        this.fragmentationEnergy = builder.fragmentationEnergy;
        this.mzRange = builder.mzRange;
        this.scanRange = builder.scanRange;
    }

    /**
     * Gets the ion mode.
     *
     * @return the ion mode
     */
    public final Constants.ION_MODE getIonMode() {

        return ionMode;
    }

    /**
     * Gets the acquisition mode.
     *
     * @return the acquisition mode
     */
    public final Constants.ACQUISITION_MODE getAcqusitionMode() {

        return acqusitionMode;
    }

    /**
     * Gets the scan range for the time domain.
     *
     * @return the scan range
     */
    public final Range getScanRange() {

        return scanRange;
    }

    /**
     * Gets the mass range in the mz domain.
     *
     * @return the mass range
     */
    public final Range getMzRange() {

        return mzRange;
    }

    /**
     * Gets the fragment energy.
     *
     * @return the fragment energy
     */
    public final double getFragmentationEnergy() {

        return fragmentationEnergy;
    }

    /**
     * Gets the mass spectrometry level number.
     *
     * @return the level number
     */
    public final Constants.MSN getMsn() {

        return msn;
    }

    /**
     * Checks if the information about the two MSn level are identifical.
     *
     * @param aRawFileLevel the MSn level to be compared to
     * @return boolean if the information is identical
     */
    @Override
    public boolean equals(Object aRawFileLevel) {

        if (this == aRawFileLevel) return true;

        if (!(aRawFileLevel instanceof RawLevel)) return false;

        RawLevel rawLevel = (RawLevel) aRawFileLevel;

        return this.getIonMode().equals(rawLevel.getIonMode()) &&
                this.getMsn() == rawLevel.getMsn() &&
                this.getMzRange().equals(rawLevel.mzRange) &&
                this.getScanRange().equals(rawLevel.scanRange);
    }

    /**
     * Returns the hash code of the MSn level.
     *
     * @return the value
     */
    @Override
    public int hashCode() {

        int hash = 1;

        hash = hash * 17 + ionMode.hashCode();
        hash = hash * 17 + msn.getLvl();
        hash = hash * 17 + mzRange.hashCode();
        hash = hash * 17 + scanRange.hashCode();

        return hash;
    }
}
