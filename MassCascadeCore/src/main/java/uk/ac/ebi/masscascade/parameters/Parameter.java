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

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.binning.MzBinning;
import uk.ac.ebi.masscascade.identification.AdductSingle;
import uk.ac.ebi.masscascade.interfaces.Option;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Enumeration of valid task parameters.
 */
public enum Parameter implements Option {

    /*
     * Meta parameters for file input/output.
     */
    OUTPUT_DIRECTORY("Output directory", System.getProperty(Constants.JAVA_TMP)),
    WORKING_DIRECTORY("Working directory", System.getProperty(Constants.JAVA_TMP)),
    SECURITY_TOKEN("Security token", ""),
    COLLISION_ENERGY("Collision energy", 30),

    /*
     * File parameters for the raw, profile, and spectrum entities.
     */
    RAW_CONTAINER("Raw file", null, RawContainer.class),
    REFERENCE_RAW_CONTAINER("Reference raw file", null, RawContainer.class),
    REFERENCE_RAW_MAP("Reference raw map", null, TreeMultimap.class),
    PROFILE_CONTAINER("Profile file", null, ProfileContainer.class),
    SPECTRUM_CONTAINER("Spectrum file", null, SpectrumContainer.class),

    /*
     * Additional file parameters for generic data and reference pointers.
     */
    DATA_FILE("Data file", null, File.class),
    REFERENCE_FILE("Reference file", null, File.class),

    /*
     * Parameters for the intensity domain.
     */
    NOISE_INTENSITY("Noise intensity [units]", 1000d),
    MIN_PROFILE_INTENSITY("Min. profile intensity [units]", 1000d),

    /*
     * Parameters for the m/z domain.
     */
    MZ_RANGE("m/z range", new ExtendableRange(0, 100)),
    MZ_WINDOW_PPM("m/z window [ppm]", 10d),
    MZ_WINDOW_AMU("m/z window [amu]", 10d),

    /*
     * Wavelet parameters.
     */
    SCALE_FACTOR("Scale [units]", 10d),
    WAVELET_WIDTH("Width of the wavelet [%]", 0.02),

    /*
     * Parameters for the chromatographic domain.
     */
    TIME_WINDOW("Time window [s]", 5d),
    TIME_FACTOR("Time gap factor", 2.5),
    SCAN_WINDOW("Time window [scans]", 5),
    DATA_WINDOW("Data points window", 10),
    MIN_PROFILE_WIDTH("Min. profile width [scans]", 6),
    MAX_PROFILE_WIDTH("Max. profile width [scans]", 10),
    TIME_RANGE("Time range [s]", new ExtendableRange(0, 60)),
    PROFILE_RANGE("Profile range [scans]", new ExtendableRange(1, 10)),

    /*
     * Parameters for visualisation.
     */
    AUTOCONFIGURE("Auto configure", true),
    MASS_RESOLUTION("Mass resolution", 150),
    TIME_RESOLUTION("Time resolution", 150),

    /*
     * Descriptive spectra parameters.
     */
    MS_LEVEL("MSn level", Constants.MSN.MS1),
    POSITIVE_MODE("Positive ion mode", true),
    NEGATIVE_MODE("Negative ion mode", false),
    NEUTRAL_LOSS("Neutral loss", false),

    /*
     * Column parameters for the KNIME workflow environment.
     * (should be used to the KNIME-MassCascade plug-in)
     */
    ION_COLUMN("Ion column", ""),
    DATA_COLUMN("Data column", ""),
    PEAK_COLUMN("Peak column", ""),
    LABEL_COLUMN("Label column", ""),
    VALUE_COLUMN("Value column", ""),
    SPECTRUM_COLUMN("Spectrum column", ""),
    REFERENCE_COLUMN("Reference column", ""),
    REFERENCE_PROFILE_COLUMN("Reference column", ""),

    /*
     * List parameters.
     */
    ION_LIST("Ion list", new TreeMap<Double, String>()),
    ADDUCT_LIST("Adduct list", new ArrayList<AdductSingle>()),
    MZ_FOR_REMOVAL("Masses for removal", new TreeSet<Double>()),

    /*
     * Ion and isotope parameters.
     */
    KEEP_ISOTOPES("Filter isotopes", true),
    REMOVE_ISOTOPES("Remove isotopes", false),
    ION_CHARGE("Charge", Constants.ION_MODE.POSITIVE),
    ION_MODE("Ion mode", Constants.ION_MODE.POSITIVE),

    /*
     * Miscellaneous parameters.
     */
    OFFSET("Offset", 5),
    SHARPNESS("Sharpness", 1d),
    RESULTS("Max results", 50),
    INSTRUMENTS("Instruments", new ArrayList<String>()),
    DATABASES("Databases", new String[0]),
    CENTER("Center profiles", true),
    CODA("Chromatogram quality", 0.5),
    DURBIN("Durbin-Watson threshold", 0.5),
    ESTIMATE_NOISE("Estimate noise", false),
    NOISE_FACTOR("Noise factor", 5),
    POLYNOMIAL_ORDER("Polynomial order", 3),
    CORRELATION_THRESHOLD("Correlation threshold", 0.75),
    DERIVATIVE_THRESHOLD("Derivative threshold", 0.2),
    SG_LEVEL("SG Filter Level", 3),
    SCORE("Minimum Score", 0.8),
    MIN_PROFILES("Minimum no. of profiles", 3),

    /*
     * Obiwarp parameters.
     */
    MZ_BINS("Mz bins", new TreeMap<Double, Integer>()),
    GAP_INIT("Gap initiation", 0.3),
    GAP_EXTEND("Gap extension", 2.4),
    RESPONSE("Responsivness", 90d),
    EXECUTABLE("Executable", ""),

    /*
     * Parameters for binning.
     */
    BINS("Number of bins", 10),
    BIN_WIDTH("Bin width", 1.5),
    AGGREGATION("Aggregation", MzBinning.BinningType.AVG.name());

    private String description;
    private Object defaultValue;
    private Class<?> type;

    private Parameter(String description, Object defaultValue) {

        this.description = description;
        this.defaultValue = defaultValue;
        this.type = defaultValue.getClass();
    }

    private Parameter(String description, Object defaultValue, Class<?> type) {

        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Gets the description of the option.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the default value of the option.
     *
     * @return the default value
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the type of the option's value.
     *
     * @return the option's type
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}
