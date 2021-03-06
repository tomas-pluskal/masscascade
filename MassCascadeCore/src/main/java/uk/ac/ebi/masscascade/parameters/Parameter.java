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
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.Option;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
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
    COLLISION_ENERGY("Collision energy", 20),

    /*
     * File parameters for the scan, feature, and featureset entities.
     */
    SCAN_CONTAINER("Raw file", null, ScanContainer.class),
    REFERENCE_SCAN_CONTAINER("Reference scan file", null, ScanContainer.class),
    REFERENCE_SCAN_MAP("Reference scan map", null, TreeMultimap.class),
    FEATURE_CONTAINER("Feature file", null, FeatureContainer.class),
    FEATURE_SET_CONTAINER("Feature Set file", null, FeatureSetContainer.class),
    REFERENCE_FEATURE_CONTAINER("Reference feature file", null, FeatureContainer.class),

    /*
     * Additional file parameters for generic data and reference pointers.
     */
    DATA_FILE("Data file", null, File.class),
    REFERENCE_FILE("Reference file", null, File.class),

    /*
     * Parameters for the intensity domain.
     */
    MIN_FEATURE_INTENSITY("Min. intensity [units]", 1000d),

    /*
     * Parameters for the m/z domain.
     */
    MZ_RANGE("m/z range (from - to)", new ExtendableRange(50, 500)),
    MZ_WINDOW_PPM("m/z tolerance [ppm]", 10d),
    MZ_WINDOW_AMU("m/z tolerance [amu]", 0.1),

    /*
     * Wavelet parameters.
     */
    SCALE_FACTOR("Scale [units]", 2),
    WAVELET_WIDTH("Width of the wavelet (0-1)", 0.02),

    /*
     * Parameters for the chromatographic domain.
     */
    TIME_WINDOW("Length [s]", 5d),
    TIME_FACTOR("Time gap factor", 2.5),
    SCAN_WINDOW("Length [scans]", 5),
    MIN_FEATURE_WIDTH("Min. length [scans]", 6),
    MAX_FEATURE_WIDTH("Max. feature width [scans]", 10),
    TIME_RANGE("Time range [s] (from - to)", new ExtendableRange(60, 300)),
    FEATURE_RANGE("Width range [scans] (from - to)", new ExtendableRange(5, 50)),

    /*
     * Descriptive spectra parameters.
     */
    MS_LEVEL("MSn level", Constants.MSN.MS1),
    POSITIVE_MODE("Positive ion mode", true),
    NEGATIVE_MODE("Negative ion mode", false),

    /*
     * Column parameters for the KNIME workflow environment.
     * (should be used to the KNIME-MassCascade plug-in)
     */
    ION_COLUMN("Ion column", ""),
    DATA_COLUMN("Data column", ""),
    FEATURE_COLUMN("Feature column", ""),
    LABEL_COLUMN("Label column", ""),
    VALUE_COLUMN("Value column", ""),
    FEATURE_SET_COLUMN("Feature Set column", ""),
    REFERENCE_COLUMN("Reference column", ""),
    REFERENCE_FEATURE_COLUMN("Reference column", ""),

    /*
     * List parameters.
     */
    EXACT_MASS_LIST("Exact mass list", new TreeMap<Double, String>()),
    ADDUCT_LIST("Adduct list", new ArrayList<AdductSingle>()),
    MZ_FOR_REMOVAL("Masses for removal", new TreeSet<Double>()),
    FEATURE_SET_LIST("Feature set list", new ArrayList<FeatureSetContainer>()),

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
    CENTER("Center features", true),
    CODA("Chromatogram quality", 0.5),
    DURBIN("Durbin-Watson threshold", 2.0),
    ESTIMATE_NOISE("Estimate noise", false),
    NOISE_FACTOR("Noise factor", 2),
    POLYNOMIAL_ORDER("Polynomial order", 3),
    CORRELATION_THRESHOLD("Correlation threshold", 0.95),
    DERIVATIVE_THRESHOLD("Derivative threshold", 0.2),
    SG_LEVEL("SG Filter Level", 3),
    SCORE("Minimum Score", 0.8),
    SCORE_METLIN("Minimum Metlin Score", 80),
    MIN_FEATURES("Minimum no. of features", 3),
    GAP_FILL("Fill gaps", false),
    MISSINGNESS("Missingness [%]", 25),
    NORM_METHOD("Method", Constants.NORM_METHOD.TOTAL_SIGNAL),
    DEFAULT("Default Intensity", 10000),

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
    BINS("Bins", 10),
    BIN_WIDTH("Bin width", 1.5),
    BIN_WIDTH_MZ("m/z bin width", 0.5),
    BIN_WIDTH_RT("Time bin width", 1.5),
    AGGREGATION("Aggregation", MzBinning.BinningType.AVG.name()),

    /*
     * Parameters for identification
     */
    ELEMENT_FILTER("Element Filter", true),
    ISOTOPE_FILTER("Isotope Filter", true),
    FRAGMENTATION_FILTER("Fragmentation Filter", true),
    RELATION_FILTER("Relation Filter", true);

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
