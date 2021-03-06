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

package uk.ac.ebi.masscascade.compound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ArrayUtils;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.*;

/**
 * A compound featureset consisting of related signals that are believed to originate from the same compound.
 */
public class CompoundSpectrum {

    // featureset id
    private final int id;

    List<CompoundEntity> compounds;

    // MS 1
    private int majorPeak;
    private List<XYPoint> peakList;
    private Map<Integer, Isotope> indexToIsotope;
    private Multimap<Integer, Adduct> indexToAdduct;

    // meta data
    private double retentionTime;

    // MSn 2
    private List<XYPoint> peakList2;

    public CompoundSpectrum(int id) {

        this.id = id;

        compounds = new ArrayList<>();

        majorPeak = 0;
        peakList = new ArrayList<>();
        peakList2 = new ArrayList<>();

        indexToIsotope = new HashMap<>();
        indexToAdduct = HashMultimap.create();

        retentionTime = 0;
    }

    public List<CompoundEntity> getBest(int number) {

        List<CompoundEntity> cCompounds = new ArrayList<>(compounds);
        Collections.sort(cCompounds, new Comparator<CompoundEntity>() {
            // orders the chemical entities by score in descending order
            @Override
            public int compare(CompoundEntity o1, CompoundEntity o2) {

                final int BEFORE = -1;
                final int EQUAL = 0;
                final int AFTER = 1;

                if (o1.getScore() < o2.getScore()) return AFTER;
                if (o1.getScore() > o2.getScore()) return BEFORE;

                return EQUAL;
            }
        });

        return cCompounds.subList(0, (number < cCompounds.size()) ? number : cCompounds.size());
    }

    public int getId() {
        return id;
    }

    public int getMajorPeak() {
        return majorPeak;
    }

    public void setMajorPeak(int majorPeak) {
        this.majorPeak = majorPeak;
    }

    public List<XYPoint> getPeakList() {
        return peakList;
    }

    public void setPeakList(List<XYPoint> peakList) {
        this.peakList = peakList;
    }

    public double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public List<XYPoint> getPeakList2() {
        return peakList2;
    }

    public void setPeakList2(List<XYPoint> peakList2) {
        this.peakList2 = peakList2;
    }

    public Map<Integer, Isotope> getIndexToIsotope() {
        return indexToIsotope;
    }

    public void setIndexToIsotope(Map<Integer, Isotope> indexToIsotope) {
        this.indexToIsotope = indexToIsotope;
    }

    public Multimap<Integer, Adduct> getIndexToAdduct() {
        return indexToAdduct;
    }

    public void setIndexToAdduct(Multimap<Integer, Adduct> indexToAdduct) {
        this.indexToAdduct = indexToAdduct;
    }

    public List<CompoundEntity> getCompounds() {
        return compounds;
    }

    public void setCompounds(List<CompoundEntity> compounds) {
        this.compounds = compounds;
    }

    public void addCompound(CompoundEntity compound) {
        compounds.add(compound);
    }

    public CompoundEntity getCompound(int compoundIndex) {
        return compounds.get(compoundIndex);
    }
}
