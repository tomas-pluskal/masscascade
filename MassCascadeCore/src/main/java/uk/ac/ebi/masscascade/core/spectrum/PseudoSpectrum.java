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

package uk.ac.ebi.masscascade.core.spectrum;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class holding all information for an annotated spectrum.
 */
public class PseudoSpectrum extends ScanImpl implements Spectrum {

    private final PropertyManager propertyManager;
    private final Map<Integer, Profile> profileMap;
    private final Range rtRange;

    /**
     * Default constructor for deserialization purposes.
     */
    public PseudoSpectrum() {

        profileMap = null;
        rtRange = null;
        propertyManager = null;
    }

    /**
     * Constructs a fully configured pseudo spectrum.
     *
     * @param scanIndex the scan index
     * @param xyList    the data set collection
     * @param rtRange   the retention time range
     * @param rt        the retention time
     */
    public PseudoSpectrum(int scanIndex, XYList xyList, double rt, Range rtRange) {

        super(scanIndex, Constants.MSN.MS1, Constants.ION_MODE.IN_SILICO, xyList, rt, -1, 0, -1);

        profileMap = new HashMap<>();
        propertyManager = new PropertyManager();
        this.rtRange = rtRange;
    }

    /**
     * Constructs a fully configured pseudo spectrum.
     *
     * @param scanIndex  the scan index
     * @param xyList     the data set collection
     * @param rtRange    the retention time range
     * @param rt         the retention time
     * @param profileSet the profile set
     */
    public PseudoSpectrum(int scanIndex, XYList xyList, Range rtRange, double rt, Set<Profile> profileSet) {

        super(scanIndex, Constants.MSN.MS1, Constants.ION_MODE.IN_SILICO, xyList, rt, -1, 0, -1);

        profileMap = new HashMap();
        this.rtRange = rtRange;
        propertyManager = new PropertyManager();

        addProfiles(profileSet);
    }

    /**
     * Adds a profile to the spectrum.
     *
     * @param profile the profile to be added
     */
    public void addProfile(Profile profile) {

        xyList.add(profile.getMzIntDp());
        profileMap.put(profile.getId(), profile);
        rtRange.extendRange(profile.getRetentionTime());
    }

    /**
     * Adds a profile set to the spectrum.
     *
     * @param profiles the peak set to be added
     */
    public void addProfiles(Set<Profile> profiles) {

        for (Profile peak : profiles)
            addProfile(peak);
    }

    /**
     * Returns a profile by id.
     *
     * @param profileId the profile identifier
     * @return the profile
     */
    public Profile getProfile(int profileId) {

        if (profileMap.containsKey(profileId)) return profileMap.get(profileId);
        return null;
    }

    /**
     * Returns all profiles.
     *
     * @return the profile list
     */
    public List<Profile> getProfileList() {
        return new ArrayList<>(profileMap.values());
    }

    /**
     * Returns the id - profile map.
     *
     * @return the id - profile map
     */
    public Map<Integer, Profile> getProfileMap() {
        return profileMap;
    }

    /**
     * Returns the retention time range.
     *
     * @return the rt range
     */
    public Range getRtRange() {
        return rtRange;
    }

    /**
     * Retrieves the value of the selected property.
     *
     * @return the property value
     */
    @Override
    public Set<Property> getProperty(PropertyManager.TYPE type) {
        return propertyManager.getProperty(type);
    }

    /**
     * Whether the property is set.
     *
     * @return boolean if property set
     */
    @Override
    public boolean hasProperty(PropertyManager.TYPE type) {
        return propertyManager.hasProperty(type);
    }

    /**
     * Sets a spectrum property.
     *
     * @param property the property to be set
     */
    @Override
    public void setProperty(Property property) {
        propertyManager.addProperty(property);
    }

    /**
     * Gets an 'empty' spectrum without data.
     *
     * @return the spectrum
     */
    public static Spectrum getEmptySpectrum() {
        return new PseudoSpectrum(-1, new XYList(), -1, new ExtendableRange());
    }

    /**
     * Sets the spectrum's parent.
     *
     * @param scanId the parent scan id
     * @param mz     the parent m/z
     * @param charge the parent charge
     */
    @Override
    public void setParent(int scanId, double mz, int charge) {

        this.parentMz = mz;
        this.parentScan = scanId;
        this.parentCharge = charge;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Profile> iterator() {
        return profileMap.values().iterator();
    }

    /**
     * Returns the size of the spectrum, i.e., the number of profiles.
     *
     * @return the size
     */
    public int size() {
        return profileMap.size();
    }
}
