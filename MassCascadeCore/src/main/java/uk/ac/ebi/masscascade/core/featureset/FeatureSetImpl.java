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

package uk.ac.ebi.masscascade.core.featureset;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.core.scan.ScanImpl;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureMassComparator;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class holding all information for an annotated featureset.
 */
public class FeatureSetImpl extends ScanImpl implements uk.ac.ebi.masscascade.interfaces.FeatureSet {

    private final PropertyManager propertyManager;
    private final Map<Integer, Feature> profileMap;
    private final Range rtRange;

    /**
     * Default constructor for deserialization purposes.
     */
    public FeatureSetImpl() {

        profileMap = null;
        rtRange = null;
        propertyManager = null;
    }

    /**
     * Constructs a fully configured pseudo featureset.
     *
     * @param scanIndex  the scan index
     * @param rtRange    the retention time range
     * @param rt         the retention time
     * @param featureSet the feature set
     */
    public FeatureSetImpl(int scanIndex, XYList xyList, Range rtRange, double rt, Set<Feature> featureSet) {

        super(scanIndex, Constants.MSN.MS1, Constants.ION_MODE.UNKNOWN, xyList, rt, -1, 0, -1);

        profileMap = new HashMap();
        this.rtRange = rtRange;
        propertyManager = new PropertyManager();

        addProfiles(featureSet);
    }

    /**
     * Adds a feature to the featureset.
     *
     * @param feature the feature to be added
     */
    public void addProfile(Feature feature) {

        profileMap.put(feature.getId(), feature);
        rtRange.extendRange(feature.getRetentionTime());
    }

    /**
     * Adds a feature set to the featureset.
     *
     * @param features the peak set to be added
     */
    public void addProfiles(Set<Feature> features) {

        List<Feature> featureList = new ArrayList<>(features);
        Collections.sort(featureList, new FeatureMassComparator());
        for (Feature peak : featureList)
            addProfile(peak);
    }

    /**
     * Returns a feature by id.
     *
     * @param profileId the feature identifier
     * @return the feature
     */
    public Feature getFeature(int profileId) {

        if (profileMap.containsKey(profileId)) return profileMap.get(profileId);
        return null;
    }

    /**
     * Returns all profiles.
     *
     * @return the feature list
     */
    public List<Feature> getFeaturesList() {
        return new ArrayList<>(profileMap.values());
    }

    /**
     * Returns the id - feature map.
     *
     * @return the id - feature map
     */
    public Map<Integer, Feature> getFeaturesMap() {
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
    public <T> Set<T> getProperty(PropertyType type, Class<T> typeClass) {
        return propertyManager.getProperty(type, typeClass);
    }

    /**
     * Whether the property is set.
     *
     * @return boolean if property set
     */
    @Override
    public boolean hasProperty(PropertyType type) {
        return propertyManager.hasProperty(type);
    }

    /**
     * Sets a featureset property.
     *
     * @param property the property to be set
     */
    @Override
    public void setProperty(Property property) {
        propertyManager.addProperty(property);
    }

    /**
     * Gets an 'empty' featureset without data.
     *
     * @return the featureset
     */
    public static uk.ac.ebi.masscascade.interfaces.FeatureSet getEmptySpectrum() {
        return new FeatureSetImpl(-1, new XYList(), new ExtendableRange(), -1, new HashSet<Feature>());
    }

    /**
     * Sets the featureset's parent.
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
    public Iterator<Feature> iterator() {
        return profileMap.values().iterator();
    }

    /**
     * Returns the size of the featureset, i.e., the number of profiles.
     *
     * @return the size
     */
    public int size() {
        return profileMap.size();
    }
}
