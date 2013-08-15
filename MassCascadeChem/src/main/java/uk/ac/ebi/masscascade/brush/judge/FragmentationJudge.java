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

package uk.ac.ebi.masscascade.brush.judge;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.score.WeightedScorer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Judge rationalising the MS2 information provided for compound entities of a compound spectrum.
 * <p/>
 * The judge can increase the total score of a compound entity by a maximum of 500.
 */
public class FragmentationJudge implements Judge {

    private static final Logger LOGGER = Logger.getLogger(FragmentationJudge.class);

    private final double TOLERANCE_AMU = 0.5;

    private int removed = 0;

    /**
     * The core method of the judge executing the filtering process.
     *
     * @param compoundSpectra the input list of compound spectra
     * @return the filtered input list
     */
    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        LOGGER.log(Level.DEBUG, "Starting Fragmentation Judge...");

        List<CompoundSpectrum> filteredCS = new ArrayList<>();
        WeightedScorer scorer = new WeightedScorer(TOLERANCE_AMU);

        for (CompoundSpectrum cs : compoundSpectra) {
            List<XYPoint> msn2 = cs.getPeakList2();

            if (msn2.size() == 0) {
//                System.out.println("Skip");
                LOGGER.log(Level.DEBUG, "Skip");
                filteredCS.add(cs);
                continue;
            }

            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                List<XYPoint> msn2Explained = new ArrayList<>();

                if (ce.getIndexToIdentity2() == null) {
                    continue;
                }

                for (int index : ce.getIndexToIdentity2().keySet()) {
                    msn2Explained.add(msn2.get(index - 1));
                }
                double score = scorer.getScore(msn2Explained, msn2) / 2d;
                if (score < 100) {
                    LOGGER.log(Level.DEBUG, "Removed: " + ce.getNotation(ce.getId()) + " - " + score);
//                    System.out.println("Removed: " + ce.getNotation(ce.getId()) + " - " + score);
                    iter.remove();
                    removed++;
                } else if (score < 350) {
                    ce.addScore((int) score);
                    ce.setStatus(Status.INTERMEDIATE);
                    ce.setEvidence(Evidence.MSI_2);
                } else {
                    ce.addScore((int) score);
                    if (ce.getStatus() == Status.INTERMEDIATE) {
                        ce.setStatus(Status.STRONG);
                    } else {
                        ce.setStatus(Status.INTERMEDIATE);
                    }
                    ce.setEvidence(Evidence.MSI_2);
                }
                LOGGER.log(Level.DEBUG, "Score: " + ce.getNotation(ce.getId()) + " - " + score);
//                System.out.println("Score: " + ce.getNotation(ce.getId()) + " - " + score);
            }

            if (cs.getCompounds().size() > 0) {
                filteredCS.add(cs);
            }
        }

        return filteredCS;
    }

    /**
     * Returns the number of removed or filtered compound spectra.
     *
     * @return the number of removed or filtered compound spectra
     */
    @Override
    public int removed() {
        return removed;
    }
}