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

package uk.ac.ebi.masscascade.alignment;

import com.google.common.collect.Multimap;
import uk.ac.ebi.masscascade.alignment.featurebins.FeatureBin;
import uk.ac.ebi.masscascade.alignment.featurebins.FeatureBinGenerator;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Table model for arranging multiple feature containers in a <code> JTable </code>. The profiles in the containers are
 * grouped by their m/z and retention time values based on the given tolerance values.
 */
public class FeatureBinTableModel extends AbstractTableModel implements Iterable<FeatureBin> {

    private static final long serialVersionUID = 1526497097606220131L;

    private Multimap<Integer, Container> profileContainers;
    private double ppm;
    private double sec;

    private List<FeatureBin> featureBins;
    private String[] headers = new String[]{"m/z", "rt", "area", "label", "m/z dev", "shape"};

    /**
     * Constructs a table model for arranging multiple feature containers in a single table.
     *
     * @param profileContainers the group id by feature container list map
     * @param ppm               the m/z tolerance value in ppm
     * @param sec               the time tolerance value in seconds
     * @param missing           the percentage of max. allowed missing profiles
     */
    public FeatureBinTableModel(Multimap<Integer, Container> profileContainers, double ppm, double sec, double missing) {

        this.profileContainers = profileContainers;
        this.ppm = ppm;
        this.sec = sec / 2d;

        featureBins = FeatureBinGenerator.createBins(this.profileContainers, this.ppm, this.sec, missing);
    }

    /**
     * Returns a map of feature container ids to profiles, that are grouped within the row at the <code> rowIndex
     * </code>.
     *
     * @param rowIndex the row index for which the map should be retrieved
     * @return the feature container ids to profiles map
     */
    public Map<String, Feature> getProfilesForRow(int rowIndex) {

        Map<String, Feature> idToProfile = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : featureBins.get(rowIndex).getContainerIndexToFeatureId().entrySet()) {
            int counter = 0;
            for (int groupId : profileContainers.keySet()) {
                List<Container> featureCs = new ArrayList<>(profileContainers.get(groupId));
                // tmp solution: possibly working, needs more testing
                Collections.sort(featureCs, new ContainerComparator());
                for (Container container : featureCs) {
                    if (counter == entry.getKey()) {
                        Feature feature = ((FeatureContainer) container).getFeature(entry.getValue());
                        String id = "(" + entry.getKey() + "-" + feature.getId() + ")";
                        idToProfile.put(id, feature);
                    }
                    counter++;
                }
            }
        }
        return idToProfile;
    }

    /**
     * Returns the m/z tolerance value in ppm.
     *
     * @return the m/z tolerance value
     */
    public double getPpm() {
        return ppm;
    }

    /**
     * Returns the time tolerance value in seconds.
     *
     * @return the time tolerance value
     */
    public double getSec() {
        return sec;
    }

    /**
     * Returns the list of rows.
     *
     * @return te list of rows
     */
    public List<FeatureBin> getRows() {
        return featureBins;
    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return featureBins.size();
    }

    /**
     * Returns the number of columns in the table model
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return FeatureBin.COLUMNS + profileContainers.size();
    }

    /**
     * Returns the column name.
     *
     * @param col the column index
     * @return the column name
     */
    public String getColumnName(int col) {

        if (col < FeatureBin.COLUMNS) return headers[col];
        else {
            int index = col - FeatureBin.COLUMNS;
            int counter = 0;
            for (int groupId : profileContainers.keySet()) {
                List<Container> featureCs = new ArrayList<>(profileContainers.get(groupId));
                // tmp solution: possibly working, needs more testing
                Collections.sort(featureCs, new ContainerComparator());
                for (Container container : featureCs) {
                    if (counter == index) {
                        return "(" + (col - FeatureBin.COLUMNS) + ") " + container.getId();
                    }
                    counter++;
                }
            }
            return "(" + (col - FeatureBin.COLUMNS) + ") ";
        }
    }

    /**
     * Returns the value at a specific position in the table defined by its row and column index.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @return the value at the defined position
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value;
        switch (columnIndex) {
            case 0:
                value = featureBins.get(rowIndex).getMz();
                break;
            case 1:
                value = featureBins.get(rowIndex).getRt();
                break;
            case 2:
                value = featureBins.get(rowIndex).getArea();
                break;
            case 3:
                value = featureBins.get(rowIndex).getLabel();
                break;
            case 4:
                value = featureBins.get(rowIndex).getMzDev();
                break;
            case 5:
                value = featureBins.get(rowIndex).getChromatogram();
                break;
            default:
                value = featureBins.get(rowIndex).isPresent(columnIndex - FeatureBin.COLUMNS);
                break;
        }
        return value;
    }

    /**
     * Returns the class of the objects stored in a particular column.
     *
     * @param c the column index
     * @return the class of the objects in that column
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * Returns an iterator over a set of elements of type <code> FeatureBin </code>.
     *
     * @return an iterator
     */
    @Override
    public Iterator<FeatureBin> iterator() {
        return featureBins.iterator();
    }
}

class ContainerComparator implements Comparator<Container> {

    @Override
    public int compare(Container o1, Container o2) {
        return o1.getId().compareTo(o2.getId());
    }
}