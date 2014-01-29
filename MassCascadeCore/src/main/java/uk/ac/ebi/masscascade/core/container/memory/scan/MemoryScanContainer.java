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

package uk.ac.ebi.masscascade.core.container.memory.scan;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.core.chromatogram.BasePeakChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.TotalIonChromatogram;
import uk.ac.ebi.masscascade.core.scan.ScanInfo;
import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.featurebuilder.FeatureMsnHelper;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core class holding all mass spec relevant information.
 */
public class MemoryScanContainer extends MemoryContainer implements ScanContainer {

    private static final Logger LOGGER = Logger.getLogger(MemoryScanContainer.class);

    private final String id;

    private ScanInfo scanInfo;
    private final List<ScanLevel> scanLevels;

    private final List<LinkedHashMap<Integer, Scan>> scans;
    private final LinkedHashMap<Constants.MSN, TotalIonChromatogram> tics;
    private BasePeakChromatogram basePeakChromatogram;

    private LinkedHashMap<Constants.MSN, XYList> ticData;
    private XYList basePeakData;

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id the file identifier
     */
    public MemoryScanContainer(String id) {

        this.id = id;
        scanInfo = new ScanInfo(id, "Unknown", null);
        scanLevels = new ArrayList<ScanLevel>();

        ticData = new LinkedHashMap<Constants.MSN, XYList>();
        basePeakData = new XYList();
        tics = new LinkedHashMap<Constants.MSN, TotalIonChromatogram>();
        basePeakChromatogram = new BasePeakChromatogram();

        scans = new ArrayList<LinkedHashMap<Integer, Scan>>();
    }

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id      the file identifier
     * @param scanInfo the scan file info container
     */
    public MemoryScanContainer(String id, ScanInfo scanInfo) {
        this(id);
        this.scanInfo = scanInfo;
    }

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id           the file identifier
     * @param oldContainer the old scan container
     */
    public MemoryScanContainer(String id, MemoryScanContainer oldContainer) {
        this(id, oldContainer.getScanInfo());
    }

    /**
     * Constructs fully configured scan file.
     *
     * @param id                   the identifier
     * @param scanInfo              the file meta description
     * @param scanLevels            the collection of ms level information
     * @param basePeakChromatogram the randomaccesfile pointer to access the base feature chromatogram
     * @param tics                 randomaccesfile pointers to access the total ion chromatograms
     * @param scans                scan index - randomaccesfile pointer associations
     * @param dataFile             the tmp data file
     */
    public MemoryScanContainer(String id, ScanInfo scanInfo, List<ScanLevel> scanLevels,
                               BasePeakChromatogram basePeakChromatogram, LinkedHashMap<Constants.MSN, TotalIonChromatogram> tics,
                               List<LinkedHashMap<Integer, Scan>> scans, String dataFile) {

        this.id = id;
        this.scanInfo = scanInfo;
        this.scanLevels = scanLevels;

        ticData = null;
        basePeakData = null;

        this.tics = tics;
        this.basePeakChromatogram = basePeakChromatogram;
        this.scans = scans;
    }

    /**
     * Adds a scan to the scan file and serializes the scan information.
     *
     * @param scan the scan to be added
     */
    @Override
    public void addScan(Scan scan) {

        Constants.MSN scanMSn = scan.getMsn();

        if (scanMSn.getLvl() <= scans.size()) {
            scans.get(scanMSn.getLvl() - 1).put(scan.getIndex(), scan);

            scanLevels.get(scanMSn.getLvl() - 1).getMzRange().extendRange(scan.getMzRange());
            scanLevels.get(scanMSn.getLvl() - 1).getScanRange().extendRange(scan.getRetentionTime());

            if (scanMSn == Constants.MSN.MS1 && !scan.getBasePeak().isEmpty())
                basePeakData.add(new XYPoint(scan.getRetentionTime(), scan.getBasePeak().get(0).y));
            ticData.get(scanMSn).add(new XYPoint(scan.getRetentionTime(), scan.getTotalIonCurrent()));
        } else {
            LinkedHashMap<Integer, Scan> scanLevelMap = new LinkedHashMap<>();
            scanLevelMap.put(scan.getIndex(), scan);
            scans.add(scanLevelMap);

            ExtendableRange rtExtendableRange = new ExtendableRange(scan.getRetentionTime(), scan.getRetentionTime());
            scanLevels.add(new ScanLevel.Builder(rtExtendableRange, scan.getMzRange(), scan.getIonMode()).msLevel(
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
     * Wraps up loose ends and closes the scan file construction process.
     *
     * @param date the creation date
     */
    @Override
    public void finaliseFile(String date) {

        if (date != null) scanInfo.setDate(date);

        basePeakChromatogram = new BasePeakChromatogram(id, Constants.MSN.MS1, basePeakData);

        for (Constants.MSN msn : Constants.MSN.values()) {
            if (msn.getLvl() > scanLevels.size()) break;

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
    public List<ScanLevel> getScanLevels() {
        return scanLevels;
    }

    /**
     * Returns the file meta info.
     *
     * @return the meta info
     */
    @Override
    public ScanInfo getScanInfo() {
        return scanInfo;
    }

    /**
     * Returns the number of scans of a particular level.
     *
     * @param msn a MSN level
     * @return the number of scans
     */
    @Override
    public int size(Constants.MSN msn) {
        return scans.get(msn.getLvl() - 1).size();
    }

    /**
     * Returns the base feature chromatogram.
     *
     * @return the base feature chromatogram
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
    public FeatureMsnHelper getMsnHelper() {
        return new FeatureMsnHelper(this);
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

        if (!(aRawFile instanceof MemoryScanContainer)) return false;

        MemoryScanContainer rawContainer = (MemoryScanContainer) aRawFile;

        // check data container meta information
        if (this.getId().equals(rawContainer.getId())) {

            if (this.getScanInfo().equals(rawContainer.getScanInfo())) {

                int i = 0;
                for (ScanLevel scanLevel : scanLevels) {

                    if (!scanLevel.equals(rawContainer.getScanLevels().get(i))) return false;
                    i++;
                }
            }
        } else {
            return false;
        }

        // check data container data
        for (ScanLevel level : this.getScanLevels()) {
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
        hash = hash * 17 + scanInfo.hashCode();

        for (ScanLevel scanLevel : scanLevels) {
            hash = hash * 17 + scanLevel.hashCode();
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

    /**
     * Returns a feature iterator.
     *
     * @return the feature iterator
     */
    @Override
    public Iterable<Feature> featureIterator() {
        throw new MassCascadeException("Method not implemented.");
    }
}
