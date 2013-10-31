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
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.properties.Identity;

import java.util.HashMap;
import java.util.Map;

/**
 * A chemical entity associated with a signal of a compound featureset.
 */
public class CompoundEntity {

    // meta
    private final int id;
    private final String name;

    private int score;
    private Status status;
    private Evidence evidence;

    // MS 1
    private final Map<Integer, Identity> indexToIdentity;

    // MSn 2
    private final Multimap<Integer, Identity> indexToIdentity2;

    public CompoundEntity(int id, int score, String name, Status status, Evidence evidence,
            Map<Integer, Identity> indexToIdentity, Multimap<Integer, Identity> indexToIdentity2) {

        this.id = id;
        this.name = name;
        this.score = score;
        this.status = status;
        this.evidence = evidence;
        this.indexToIdentity = indexToIdentity;
        this.indexToIdentity2 = indexToIdentity2;
    }

    public CompoundEntity(int id, int score, String name, Status status, Evidence evidence) {

        this.id = id;
        this.name = name;
        this.score = score;
        this.status = status;
        this.evidence = evidence;
        this.indexToIdentity = new HashMap<>();
        this.indexToIdentity2 = HashMultimap.create();
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }

    public Map<Integer, Identity> getIndexToIdentity() {
        return indexToIdentity;
    }

   public String getNotation(int id) {
       if (indexToIdentity.containsKey(id))
           return indexToIdentity.get(id).getNotation();
       else
           return null;
   }

    public Multimap<Integer, Identity> getIndexToIdentity2() {
        return indexToIdentity2;
    }
}
