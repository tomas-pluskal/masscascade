/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.parameters;

import uk.ac.ebi.masscascade.alignment.FastDtwAlignment;
import uk.ac.ebi.masscascade.background.BackgroundSubtraction;
import uk.ac.ebi.masscascade.background.CodaFilter;
import uk.ac.ebi.masscascade.background.DurbinWatsonFilter;
import uk.ac.ebi.masscascade.background.NoiseReduction;
import uk.ac.ebi.masscascade.binning.MzFileBinning;
import uk.ac.ebi.masscascade.binning.RtBinning;
import uk.ac.ebi.masscascade.centroiding.WaveletPeakPicking;
import uk.ac.ebi.masscascade.deconvolution.BiehmanDeconvolution;
import uk.ac.ebi.masscascade.deconvolution.SavitzkyGolayDeconvolution;
import uk.ac.ebi.masscascade.distance.BiehmanSimilarity;
import uk.ac.ebi.masscascade.distance.CosineSimilarityDistance;
import uk.ac.ebi.masscascade.filter.MzFilter;
import uk.ac.ebi.masscascade.filter.ProfileFilter;
import uk.ac.ebi.masscascade.filter.ScanFilter;
import uk.ac.ebi.masscascade.gapfilling.SplineFilling;
import uk.ac.ebi.masscascade.identification.AdductFinder;
import uk.ac.ebi.masscascade.identification.IonFinder;
import uk.ac.ebi.masscascade.identification.IsotopeFinder;
import uk.ac.ebi.masscascade.identification.IsotopeKeeper;
import uk.ac.ebi.masscascade.identification.IsotopeRemover;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Index;
import uk.ac.ebi.masscascade.io.CmlReader;
import uk.ac.ebi.masscascade.io.CmlWriter;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.io.XCaliburReader;
import uk.ac.ebi.masscascade.smoothing.RunningMedianSmoothing;
import uk.ac.ebi.masscascade.smoothing.SavitzkyGolaySmoothing;
import uk.ac.ebi.masscascade.tracebuilder.ProfileBuilder;
import uk.ac.ebi.masscascade.tracebuilder.ProfileBuilderOld;
import uk.ac.ebi.masscascade.tracebuilder.ProfileJoiner;
import uk.ac.ebi.masscascade.tracebuilder.ProfileSplitter;

/**
 * Enumerates all available tasks.
 */
public enum CoreTasks implements Index {

    // dummy
    DUMMY(null, PLACEHOLDER()),

    // reader and writer
    CML_WRITER(CmlWriter.class, PLACEHOLDER()),
    CML_READER(CmlReader.class, PLACEHOLDER()),
    PSI_READER(PsiMzmlReader.class, PLACEHOLDER()),
    RAW_READER(XCaliburReader.class, PLACEHOLDER()),

    // smoother
    SAVITZGY_GOLAY(SavitzkyGolaySmoothing.class, "SG"),
    RUNNING_MEDIAN(RunningMedianSmoothing.class, "RM"),

    // filter
    PROFILE_FILTER(ProfileFilter.class, "PF"),
    MASS_FILTER(MzFilter.class, "MF"),
    SCAN_FILTER(ScanFilter.class, "SF"),

    // utilities
    FAST_DTW(FastDtwAlignment.class, "DTW"),
    RETENTION_TIME(RtBinning.class, "RT"),
    MASS_DOMAIN(MzFileBinning.class, "MZ"),
    GAP_FILLING(SplineFilling.class, "GF"),

    // profile picker and extractor
    WAVELET(WaveletPeakPicking.class, "WV"),
    MASS_TRACE_BUILDER(ProfileBuilder.class, "TB"),
    MASS_TRACE_BUILDER_OLD(ProfileBuilderOld.class, "TB"),
    MASS_TRACE_EXTRACTION(ProfileJoiner.class, "MT"),
    MASS_TRACE_EXPLOSION(ProfileSplitter.class, "MP"),
    COSINE_SIMILARITY(CosineSimilarityDistance.class, "CS"),
    BIEHMAN_SIMILARITY(BiehmanSimilarity.class, "BS"),

    // deconvolution
    BIEHMAN(BiehmanDeconvolution.class, "BB"),
    SG_DECONVOLUTION(SavitzkyGolayDeconvolution.class, "SGD"),

    // adducts and identification
    ION_FINDER(IonFinder.class, "IS"),
    ADDUCT_FINDER(AdductFinder.class, "AF"),
    ISOTOPE_FINDER(IsotopeFinder.class, "IF"),
    ISOTOPE_REMOVER(IsotopeRemover.class, "IR"),
    ISOTOPE_KEEPER(IsotopeKeeper.class, "IK"),

    // background and noise reduction
    NOISE_REDUCTION(NoiseReduction.class, "NRA"),
    BACKGROUND_SUBTRACTION(BackgroundSubtraction.class, "BGS"),

    // profile selection
    DURBIN(DurbinWatsonFilter.class, "DW"),
    CODA(CodaFilter.class, "CD");

    private final Class<? extends ACallableTask> className;
    private final String identifier;

    /**
     * Constructs a processing task.
     *
     * @param className  the task class
     * @param identifier the abbreviated identifier
     */
    private CoreTasks(Class<? extends ACallableTask> className, String identifier) {

        this.className = className;
        this.identifier = "-" + identifier;
    }

    /**
     * Returns the callable class.
     *
     * @return the callable class
     */
    public synchronized Class<? extends ACallableTask> getCallableClass() {

        return className;
    }

    /**
     * Returns the abbreviated identifier.
     *
     * @return the identifier
     */
    public synchronized String getIdentifier() {

        return identifier;
    }

    /**
     * Simple placeholder for 'empty' default values.
     *
     * @return the placeholder
     */
    private static String PLACEHOLDER() {

        return "";
    }

    /**
     * Returns the corresponding enum for a task class.
     *
     * @param callableClass the task class
     * @return the enum
     */
    public static CoreTasks getEnumFor(Class<? extends ACallableTask> callableClass) {

        for (CoreTasks x : CoreTasks.values())
            if (x.getCallableClass() == callableClass) return x;

        return null;
    }
}