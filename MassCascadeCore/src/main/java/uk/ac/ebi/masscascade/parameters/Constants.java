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

package uk.ac.ebi.masscascade.parameters;

/**
 * Class holding constants for frequently used parameters/variables.
 */
public class Constants {

    // Java temporary directory path
    public static String JAVA_TMP = "java.io.tmpdir";

    // Parts per million
    public static final double PPM = Math.pow(10.0, 6.0);

    // Abundance values
    public static final double MAX_ABUNDANCE = 1000d;
    public static final double MIN_ABUNDANCE = 0d;

    // Separator
    public static final String SEPARATOR = " | ";
    public static final String DELIMITER = "~";

    // Number of threads used for web services
    public static final int NTHREADS = 10;

    // Number of data points for padded data sets
    public static final int PADDING = 3;

    /*
     * MSn level.
     */
    public enum MSN {
        MS1(1), MS2(2), MS3(3), MS4(4), MSn(5);

        private int ms;

        private MSN(int ms) {
            this.ms = ms;
        }

        public int getLvl() {
            return ms;
        }

        public MSN up() {
            return this == MS1 ? MS1 : get(ms - 1);
        }

        public MSN down() {
            return this == MSn ? MSn : get(ms + 1);
        }

        public static MSN get(int value) {
            return MSN.valueOf("MS" + value);
        }

        public static MSN get(String value) {
            return MSN.valueOf("MS" + value);
        }
    }

    /*
     * Supported file formats.
     */
    public enum FILE_FORMATS {
        RAW, MZML, CML
    }

    /*
     * Values for ion mode.
     */
    public enum ION_MODE {
        POSITIVE, NEGATIVE, IN_SILICO, NEUTRAL
    }

    /*
     * Acquisition mode.
     */
    public enum ACQUISITION_MODE {
        CENTROID, PROFILE
    }

    /*
     * MSn mode.
     */
    public enum MSN_MODE {
        PRECURSOR_ION_SCAN, PRODUCT_ION_SCAN, NEUTRAL_LOSS_SCAN
    }

    /*
     * Frequent dimensions.
     */
    public enum DOMAIN {
        MASS_CHARGE, INTENSITY, CHROMATOGRAPHIC
    }

    /*
     * Subatomic particles.
     */
    public enum PARTICLES {
        PROTON(1.007276467),
        ELECTRON(0.00054858);

        private double mass;

        private PARTICLES(double mass) {
            this.mass = mass;
        }

        public double getMass() {
            return mass;
        }
    }
}
