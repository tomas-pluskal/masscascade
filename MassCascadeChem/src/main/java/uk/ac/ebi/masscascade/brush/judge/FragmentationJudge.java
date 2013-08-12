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

import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.score.WeightedScorer;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentationJudge implements Judge {

    private final double TOLERANCE_PPM = 10.0;

    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        List<CompoundSpectrum> filteredCS = new ArrayList<>();
        WeightedScorer scorer = new WeightedScorer(0.5);

        for (CompoundSpectrum cs : compoundSpectra) {
            List<XYPoint> msn2 = cs.getPeakList2();
            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                List<XYPoint> msn2Explained = new ArrayList<>();
                for (int index : ce.getIndexToIdentity2().keySet()) {
                    msn2Explained.add(msn2.get(index - 1));
                }
                double score = scorer.getScore(msn2Explained, msn2) / 2d;
                if (score < 100) {

                } else if (score < 350) {

                } else {

                }
            }
        }

        return filteredCS;
    }

    @Override
    public int removed() {
        return 0;
    }
}