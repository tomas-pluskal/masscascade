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

package uk.ac.ebi.masscascade.core.container.memory.raw;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.core.chromatogram.BasePeakChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.TotalIonChromatogram;
import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core class holding all mass spec relevant information.
 */
public class MemoryRawContainer extends MemoryContainer implements RawContainer {

    private static final Logger LOGGER = Logger.getLogger(MemoryRawContainer.class);

    private final String id;

    private RawInfo rawInfo;
    private final List<RawLevel> rawLevels;

    private final List<LinkedHashMap<Integer, Scan>> scans;
    private final LinkedHashMap<Constants.MSN, TotalIonChromatogram> tics;
    private BasePeakChromatogram basePeakChromatogram;

    private LinkedHashMap<Constants.MSN, XYList> ticData;
    private XYList basePeakData;

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id the file identifier
     */
    public MemoryRawContainer(String id) {

        this.id = id;
        rawInfo = new RawInfo(id, "Unknown", null);
        rawLevels = new ArrayList<RawLevel>();

        ticData = new LinkedHashMap<Constants.MSN, XYList>();
        basePeakData = new XYList();
        tics = new LinkedHashMap<Constants.MSN, TotalIonChromatogram>();
        basePeakChromatogram = new BasePeakChromatogram();

        scans = new ArrayList<LinkedHashMap<Integer, Scan>>();
    }

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id      the file identifier
     * @param rawInfo the raw file info container
     */
    public MemoryRawContainer(String id, RawInfo rawInfo) {
        this(id);
        this.rawInfo = rawInfo;
    }

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id           the file identifier
     * @param oldContainer the old raw container
     */
    public MemoryRawContainer(String id, MemoryRawContainer oldContainer) {
        this(id, oldContainer.getRawInfo());
    }

    /**
     * Constructs fully configured raw file.
     *
     * @param id                   the identifier
     * @param rawInfo              the file meta description
     * @param rawLevels            the collection of ms level information
     * @param basePeakChromatogram the randomaccesfile pointer to access the base profile chromatogram
     * @param tics                 randomaccesfile pointers to access the total ion chromatograms
     * @param scans                scan index - randomaccesfile pointer associations
     * @param dataFile             the tmp data file
     */
    public MemoryRawContainer(String id, RawInfo rawInfo, List<RawLevel> rawLevels,
            BasePeakChromatogram basePeakChromatogram, LinkedHashMap<Constants.MSN, TotalIonChromatogram> tics,
            List<LinkedHashMap<Integer, Scan>> scans, String dataFile) {

        this.id = id;
        this.rawInfo = rawInfo;
        this.rawLevels = rawLevels;

        ticData = null;
        basePeakData = null;

        this.tics = tics;
        this.basePeakChromatogram = basePeakChromatogram;
        this.scans = scans;
    }

    /**
     * Adds a scan to the raw file and serializes the scan information.
     *
     * @param scan the scan to be added
     */
    @Override
    public void addScan(Scan scan) {

        Constants.MSN scanMSn = scan.getMsn();

        if (scanMSn.getLvl() <= scans.size()) {
            scans.get(scanMSn.getLvl() - 1).put(scan.getIndex(), scan);

            rawLevels.get(scanMSn.getLvl() - 1).getMzRange().extendRange(scan.getMzRange());
            rawLevels.get(scanMSn.getLvl() - 1).getScanRange().extendRange(scan.getRetentionTime());

            if (scanMSn == Constants.MSN.MS1 && !scan.getBasePeak().isEmpty())
                basePeakData.add(new XYPoint(scan.getRetentionTime(), scan.getBasePeak().get(0).y));
            ticData.get(scanMSn).add(new XYPoint(scan.getRetentionTime(), scan.getTotalIonCurrent()));
        } else {
            LinkedHashMap<Integer, Scan> scanLevelMap = new LinkedHashMap<Integer, Scan>();
            scanLevelMap.put(scan.getIndex(), scan);
            scans.add(scanLevelMap);

            ExtendableRange rtExtendableRange = new ExtendableRange(scan.getRetentionTime(), scan.getRetentionTime());
            rawLevels.add(new RawLevel.Builder(rtExtendableRange, scan.getMzRange(), scan.getIonMode()).msLevel(
                    scanMSn).build());

            if (scanMSn == Constants.MSN.MS1 && !scan.getBasePeak().isEmpty())
                basePeakData.add(new XYPoint(scan.getRetentionTime(), scan.getBasePeak().get(0).y));
            XYList xyList = new XYList();
            xyList.add(new XYPoint(scan.getRetentionTime(), scan.getTotalIonCurrent()));
            ticData.put(scanMSn, xyList);
        }
    }

    /**
     * Adds a list of scans and serializes the information.
     *
     * @param scans the list of scans to be added
     */
    @Override
    public void addScanList(List<Scan> scans) {
        for (Scan scan : scans) this.addScan(scan);
    }

    /**
     * Wraps up loose ends and closes the raw file construction process.
     *
     * @param date the creation date
     */
    @Override
    public void finaliseFile(String date) {

        if (date != null) rawInfo.setDate(date);

        basePeakChromatogram = new BasePeakChromatogram(id, Constants.MSN.MS1, basePeakData);

        for (Constants.MSN msn : Constants.MSN.values()) {
            if (msn.getLvl() > rawLevels.size()) break;

            XYList ticLevelData = ticData.get(msn);
            tics.put(msn, new TotalIonChromatogram(id, msn, ticLevelData));
        }
        ticData = null;
        basePeakData = null;
    }

    /**
     * Returns the file id.
     *
     * @return the file id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns the list of MSn information.
     *
     * @return the MSn information
     */
    @Override
    public List<RawLevel> getRawLevels() {
        return rawLevels;
    }

    /**
     * Returns the file meta info.
     *
     * @return the meta info
     */
    @Override
    public RawInfo getRawInfo() {
        return rawInfo;
    }

    /**
     * Returns the number of scans of a particular level.
     * @param msn a MSN level
     * @return the number of scans
     */
    @Override
    public int size(Constants.MSN msn) {
        return scans.get(msn.getLvl() - 1).size();
    }

    /**
     * Returns the base profile chromatogram.
     *
     * @return the base profile chromatogram
     */
    @Override
    public Chromatogram getBasePeakChromatogram() {
        return basePeakChromatogram;
    }

    /**
     * Returns the total ion chromatogram of level MSn.
     *
     * @param msn the MSn level
     * @return the total ion chromatogram
     */
    @Override
    public Chromatogram getTicChromatogram(Constants.MSN msn) {

        return (tics.containsKey(msn)) ? tics.get(msn) : null;
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return "";
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    @Override
    public boolean removeAll() {
        scans.clear();
        return (scans.size() == 0);
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        throw new MassCascadeException("Memory containers are not file based.");
    }

    /**
     * Returns the size of the container
     *
     * @return the container's size
     */
    @Override
    public int size() {
        return scans.size();
    }

    /**
     * Returns the scan with the given scan index.
     *
     * @param i the scan index
     * @return the scan
     */
    @Override
    public Scan getScan(int i) {

        for (Map<Integer, Scan> map : scans) {
            if (map.containsKey(i)) return map.get(i);
        }

        return null;
    }

    /**
     * Returns the scan with the given array index.
     *
     * @param i the array index
     * @return the scan at the index
     */
    @Override
    public Scan getScanByIndex(int i) {

        Scan scanAtIndex = null;
        int j = 0;
        for (Scan scan : this) {
            if (i == j) {
                scanAtIndex = scan;
                break;
            }
            j++;
        }

        return scanAtIndex;
    }

    /**
     * Returns the parent scan -> daughter scan -> parent mass association map.
     *
     * @return the map
     */
    @Override
    public Map<Integer, HashMap<Integer, Double>> getMSnParentDaughterMap() {

        Map<Integer, HashMap<Integer, Double>> resultMap = new HashMap<Integer, HashMap<Integer, Double>>();

        if (scans.size() < 2) return resultMap;

        Scan scan;
        for (Integer scanNo : scans.get(1).keySet()) {

            scan = getScan(scanNo);

            if (scan.getParentMz() == 0) continue;

            if (resultMap.containsKey(scan.getParentScan())) {
                break;
            }

            HashMap<Integer, Double> dIndexdMass = new HashMap<Integer, Double>();
            dIndexdMass.put(scan.getIndex(), scan.getParentMz());
            resultMap.put(scan.getParentScan(), dIndexdMass);
        }

        return resultMap;
    }

    /**
     * Checks if two mass spec data container are identical.
     *
     * @param aRawFile the mass spec data container to be compared to
     * @return boolean if the container is identical
     */
    @Override
    public boolean equals(Object aRawFile) {

        if (this == aRawFile) return true;

        if (!(aRawFile instanceof MemoryRawContainer)) return false;

        MemoryRawContainer rawContainer = (MemoryRawContainer) aRawFile;

        // check data container meta information
        if (this.getId().equals(rawContainer.getId())) {

            if (this.getRawInfo().equals(rawContainer.getRawInfo())) {

                int i = 0;
                for (RawLevel rawLevel : rawLevels) {

                    if (!rawLevel.equals(rawContainer.getRawLevels().get(i))) return false;
                    i++;
                }
            }
        } else {
            return false;
        }

        // check data container data
        for (RawLevel level : this.getRawLevels()) {
            for (Scan scan : this.iterator(level.getMsn())) {
                if (!scan.equals(scan)) return false;
            }
        }

        return true;
    }

    /**
     * Returns the hash code of the object.
     *
     * @return the value
     */
    @Override
    public int hashCode() {

        int hash = 1;

        hash = hash * 17 + id.hashCode();
        hash = hash * 17 + rawInfo.hashCode();

        for (RawLevel rawLevel : rawLevels) {
            hash = hash * 17 + rawLevel.hashCode();
        }

        return hash;
    }

    /**
     * Returns an iterator over a set of elements of type Scan.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Scan> iterator() {
        return scans.get(0).values().iterator();
    }

    /**
     * Returns an iterator over a set of elements of type Scan for a given MSn level.
     *
     * @return an Iterator.
     */
    @Override
    public Iterable<Scan> iterator(final Constants.MSN msn) {
        return new Iterable<Scan>() {

            public Iterator<Scan> iterator() {
                return scans.get(msn.getLvl() - 1).values().iterator();
            }
        };
    }
}
