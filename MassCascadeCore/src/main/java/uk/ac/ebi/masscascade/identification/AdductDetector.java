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

package uk.ac.ebi.masscascade.identification;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class implementing an adduct search method for a list of peaks. The input file containing adduct information
 * must follow the following format:
 * <p/>
 * # comment line (x times)
 * 1.008,Hydrogen
 * ...
 * ...
 * <p/>
 * One or many comment lines followed by lines containing the major isotopic mass and its label in comma-separated
 * format. The adduct masses are corrected according to the specified ion mode.
 */
public class AdductDetector {

    private static final Logger LOGGER = Logger.getLogger(AdductDetector.class);

    /**
     * Reference: "The isotopic Mass Defect", E. Thurman, et al.
     * Mass defect: +/- 0.003 u
     */
    private static final double DEFAULT_MASS_TOLERANCE = 5;

    /**
     * Text constants for the adduct file format.
     */
    private static final String COMMENT = "#";
    private static final String SEPARATOR = ",";

    private final String MH;

    private List<Profile> profileList;
    private List<AdductSingle> adductList;

    private final Constants.ION_MODE ionMode;
    private final boolean neutralLoss;
    private final double ppm;

    /**
     * Constructor for the adduct detector. Instantiates an empty adduct map.
     *
     * @param ionMode the acquisition mode
     */
    public AdductDetector(Constants.ION_MODE ionMode, boolean neutralLoss) {
        this(DEFAULT_MASS_TOLERANCE, ionMode, neutralLoss);
    }

    /**
     * Constructor for the adduct detector. Instantiates an empty adduct map.
     *
     * @param ppm         the mass tolerance for the isotope and adduct search [ppm]
     * @param ionMode     the acquisition mode
     * @param neutralLoss whether the adduct(s) results from a neutral loss
     */
    public AdductDetector(double ppm, Constants.ION_MODE ionMode, boolean neutralLoss) {

        this.ppm = ppm;
        this.ionMode = ionMode;
        this.neutralLoss = neutralLoss;

        if (ionMode.equals(Constants.ION_MODE.POSITIVE)) MH = "M+H";
        else if (ionMode.equals(Constants.ION_MODE.NEGATIVE)) MH = "M-H";
        else MH = "";

        adductList = new ArrayList<>();
    }

    /**
     * Sets a new adduct map defining the adduct detector.
     *
     * @param adductFile the file containing the adducts
     */
    public void setAdductList(File adductFile) {

        try {
            Reader adductFileReader = new FileReader(adductFile);
            BufferedReader br = new BufferedReader(adductFileReader);

            readAdductList(br);
        } catch (FileNotFoundException exception) {
            LOGGER.log(Level.WARN, "Adduct input file not found. " + exception.getMessage());
        }
    }

    /**
     * Reads the file line by line and parses the 'mass,label' records.
     */
    private void readAdductList(BufferedReader br) {

        try {
            String line = "";
            while ((line = br.readLine()) != null) {

                if (line.startsWith(COMMENT)) continue;
                String[] lineElements = line.split(SEPARATOR);
                if (lineElements.length != 3) continue;

                adductList.add(new AdductSingle(lineElements[0], Integer.parseInt(lineElements[1]),
                        Double.parseDouble(lineElements[2]), ionMode, neutralLoss));
            }
        } catch (Exception exception) {
            LOGGER.log(Level.INFO, "Adduct record not readable. " + exception.getMessage());
        }
    }

    /**
     * Sets a new adduct list defining the adduct detector.
     *
     * @param adductList the adduct list
     */
    public void setAdductList(List<AdductSingle> adductList) {
        this.adductList = adductList;
    }

    public void findAdducts(List<Profile> profileList) {

        this.profileList = profileList;

        Collections.sort(this.profileList, new ProfileMassComparator());
        double[][] peakMassDeltas = getPeakMassDeltas(this.profileList);

        for (AdductSingle adduct : adductList) {
            if (adduct.isCluster() || adduct.getMass() < -0.5 || adduct.getMass() >= 0.5)
                findAdductMassInDeltaArray(adduct, peakMassDeltas);
        }
    }

    /**
     * Iterates over the mass difference matrix trying to find the adduct mass difference.
     *
     * @param adduct         a single adduct
     * @param peakMassDeltas the mass difference matrix
     */
    private void findAdductMassInDeltaArray(AdductSingle adduct, double[][] peakMassDeltas) {

        double adductMass = adduct.getMass();
        Range adductMassRange = new ToleranceRange(FastMath.abs(adductMass), ppm);

        for (int row = 0; row < peakMassDeltas.length; row++) {
            for (int col = 0; col < peakMassDeltas.length; col++) {

                double peakMassDelta = peakMassDeltas[row][col];
                if (peakMassDelta == 0) break;
                if (!adductMassRange.contains(peakMassDelta) && !adduct.isCluster()) continue;

                Property[] adductAndReferenceProperty = null;

                // adduct lost
                if (adductMass < 0) {
                    if (adduct.isCluster()) {
                        double mz = correctMz(profileList.get(row).getMz(), adduct);
                        for (Profile profile : profileList) {
                            if (new ToleranceRange(mz, ppm).contains(profile.getMz())) {
                                adductAndReferenceProperty = getProperties(adduct, row, col);
                                break;
                            }
                        }
                    } else adductAndReferenceProperty = getProperties(adduct, row, col);

                    if (adductAndReferenceProperty != null) {
                        profileList.get(col).setProperty(adductAndReferenceProperty[0]);
                        profileList.get(row).setProperty(adductAndReferenceProperty[1]);
                    }

                    // adduct gained
                } else if (adductMass > 0) {
                    if (adduct.isCluster()) {
                        double mz = correctMz(profileList.get(row).getMz(), adduct);
                        for (Profile profile : profileList) {
                            if (new ToleranceRange(mz, ppm).contains(profile.getMz())) {
                                adductAndReferenceProperty = getProperties(adduct, col, row);
                                break;
                            }
                        }
                    } else adductAndReferenceProperty = getProperties(adduct, col, row);

                    if (adductAndReferenceProperty != null) {
                        profileList.get(row).setProperty(adductAndReferenceProperty[0]);
                        profileList.get(col).setProperty(adductAndReferenceProperty[1]);
                        if (adduct.isCluster()) break;
                    }
                }
            }
        }
    }

    /**
     * Corrects the adduct based on its cluster size to match the corresponding M+H signal.
     *
     * @param mz     the m/z value
     * @param adduct the adduct belonging to the m/z value
     * @return the corrected m/z value matching the M+H signal
     */
    private double correctMz(double mz, AdductSingle adduct) {

        if (neutralLoss) return mz - adduct.getMass();

        if (ionMode.equals(Constants.ION_MODE.POSITIVE))
            mz = (mz - adduct.getMass()) / adduct.getClusterSize() + Constants.PARTICLES.PROTON.getMass();
        else if (ionMode.equals(Constants.ION_MODE.NEGATIVE))
            mz = (mz - adduct.getMass()) / adduct.getClusterSize() - Constants.PARTICLES.PROTON.getMass();

        return mz;
    }

    private Property[] getProperties(AdductSingle adduct, int parent, int child) {

        Property[] props = new Property[2];
        int parentId = profileList.get(parent).getId();
        props[0] = new Adduct(adduct.getMass(), adduct.getName(), parentId, profileList.get(child).getId());
        props[1] = new Adduct(Constants.PARTICLES.PROTON.getMass(), MH, parentId, parentId);
        return props;
    }

    /**
     * Creates the symmetric mass difference matrix of size n x n.
     *
     * @param peakList the peaks used for the matrix
     * @return the mass difference matrix
     */
    private double[][] getPeakMassDeltas(List<Profile> peakList) {

        double[][] massDeltas = new double[peakList.size()][peakList.size()];

        int row = 0;
        Iterator<Profile> itRow = peakList.iterator();
        while (itRow.hasNext()) {
            double rowMass = itRow.next().getMz();

            int col = 0;
            Iterator<Profile> itCol = peakList.iterator();
            while (itCol.hasNext()) {
                double colMass = itCol.next().getMz();

                double delta = rowMass - colMass;
                if (delta == 0) break;
                massDeltas[row][col++] = delta;
            }
            row++;
        }

        return massDeltas;
    }
}

