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

package uk.ac.ebi.masscascade.alignment;

import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Table model for arranging multiple profile containers in a <code> JTable </code>. The profiles in the containers are
 * grouped by their m/z and retention time values based on the given tolerance values.
 */
public class ProfileBinTableModel extends AbstractTableModel implements Iterable<ProfileBin> {

    private static final long serialVersionUID = 1526497097606220131L;

    private List<ProfileContainer> profileContainers;
    private double ppm;
    private double sec;

    private List<ProfileBin> profileBins;
    private String[] headers = new String[]{"m/z", "rt", "area", "label", "m/z dev", "shape"};

    /**
     * Constructs a table model for arranging multiple profile containers in a single table.
     *
     * @param profileContainers the list of profile containers
     * @param ppm               the m/z tolerance value in ppm
     * @param sec               the time tolerance value in seconds
     */
    public ProfileBinTableModel(List<ProfileContainer> profileContainers, double ppm, double sec) {

        this.profileContainers = profileContainers;
        this.ppm = ppm;
        this.sec = sec / 2d;

        init();
    }

    /**
     * Returns a map of profile container ids to profiles, that are grouped within the row at the <code> rowIndex
     * </code>.
     *
     * @param rowIndex the row index for which the map should be retrieved
     * @return the profile container ids to profiles map
     */
    public Map<String, Profile> getProfilesForRow(int rowIndex) {

        Map<String, Profile> idToProfile = new HashMap<String, Profile>();
        for (Map.Entry<Integer, Integer> entry : profileBins.get(rowIndex).getContainerIndexToProfileId().entrySet()) {
            Profile profile = profileContainers.get(entry.getKey()).getProfile(entry.getValue());
            String id = "(" + entry.getKey() + "-" + profile.getId() + ")";
            idToProfile.put(id, profile);
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
    public List<ProfileBin> getRows() {
        return profileBins;
    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return profileBins.size();
    }

    /**
     * Returns the number of columns in the table model
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return ProfileBin.COLUMNS + profileContainers.size();
    }

    /**
     * Returns the column name.
     *
     * @param col the column index
     * @return the column name
     */
    public String getColumnName(int col) {

        if (col < ProfileBin.COLUMNS) return headers[col];
        else return "(" + (col - ProfileBin.COLUMNS) + ") " + profileContainers.get(col - ProfileBin.COLUMNS).getId();
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
                value = profileBins.get(rowIndex).getMz();
                break;
            case 1:
                value = profileBins.get(rowIndex).getRt();
                break;
            case 2:
                value = profileBins.get(rowIndex).getArea();
                break;
            case 3:
                value = profileBins.get(rowIndex).getLabel();
                break;
            case 4:
                value = profileBins.get(rowIndex).getMzDev();
                break;
            case 5:
                value = profileBins.get(rowIndex).getChromatogram();
                break;
            default:
                value = profileBins.get(rowIndex).isPresent(columnIndex - ProfileBin.COLUMNS);
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
     * Initiates the table model by grouping all profiles of all profile containers into m/z and rt bins based on the
     * given m/z and time tolerance values.
     */
    private void init() {

        int index = -1;
        ProfileMap timeBins = new ProfileMap();
        for (ProfileContainer container : profileContainers) {
            List<Profile> profiles = container.getProfileList();
            Collections.sort(profiles, new ProfileMassComparator());
            index++;
            for (Profile profile : profiles) {
                double rt = profile.getRetentionTime();
                XYTrace mzTrace = new XYTrace(profile.getMzIntDp());
                Trace closestMzTrace = DataUtils.getClosestKey(mzTrace, timeBins);

                ProfileBin timeBin = new ProfileBin(index, profile, profileContainers.size());
                if (closestMzTrace != null && timeBins.containsKey(closestMzTrace)) {

                    if (new ToleranceRange(closestMzTrace.getAvg(), ppm).contains(profile.getMz())) {

                        int cIndex = 0;
                        List<ProfileBin> mzTimeBins = timeBins.get(closestMzTrace);
                        ProfileBin cTimeBin = mzTimeBins.get(cIndex);
                        for (int i = 1; i < mzTimeBins.size(); i++) {
                            ProfileBin nTimeBin = mzTimeBins.get(i);
                            if (Math.abs(nTimeBin.getRt() - rt) < Math.abs(cTimeBin.getRt() - rt)) {
                                cTimeBin = nTimeBin;
                                cIndex = i;
                            }
                        }
                        if (cTimeBin.getRt() - sec <= rt && cTimeBin.getRt() + sec > rt) {
                            cTimeBin.add(index, profile);
                            timeBins.add(closestMzTrace, cTimeBin, cIndex);
                        } else timeBins.put(closestMzTrace, timeBin);

                        continue;
                    }
                }
                timeBins.put(mzTrace, timeBin);
            }
        }

        profileBins = new ArrayList<ProfileBin>();
        for (List<ProfileBin> bin : timeBins.values()) profileBins.addAll(bin);
    }

    /**
     * Returns an iterator over a set of elements of type <code> ProfileBin </code>.
     *
     * @return an iterator
     */
    @Override
    public Iterator<ProfileBin> iterator() {
        return profileBins.iterator();
    }
}
