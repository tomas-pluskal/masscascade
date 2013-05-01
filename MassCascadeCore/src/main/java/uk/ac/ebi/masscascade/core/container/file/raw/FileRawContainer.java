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

package uk.ac.ebi.masscascade.core.container.file.raw;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileContainer;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.chromatogram.BasePeakChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.TotalIonChromatogram;
import uk.ac.ebi.masscascade.core.profile.ProfileIterator;
import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawIterator;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.tracebuilder.ProfileMsnHelper;
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
public class FileRawContainer extends FileContainer implements RawContainer {

    private static final Logger LOGGER = Logger.getLogger(FileRawContainer.class);

    private final String id;

    private RawInfo rawInfo;
    private final List<RawLevel> rawLevels;

    private final List<LinkedHashMap<Integer, Long>> scanNumbers;
    private final LinkedHashMap<Constants.MSN, Long> ticNumbers;
    private Long basePeakNumber;

    private LinkedHashMap<Constants.MSN, XYList> ticData;
    private XYList basePeakData;

    private final FileManager fileManager;

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id the file identifier
     */
    public FileRawContainer(String id) {
        this(id, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id               the file identifier
     * @param workingDirectory the working directory
     */
    public FileRawContainer(String id, String workingDirectory) {

        this.id = id;
        rawInfo = new RawInfo(id, "Unknown", null);
        rawLevels = new ArrayList<RawLevel>();

        ticData = new LinkedHashMap<Constants.MSN, XYList>();
        basePeakData = new XYList();
        ticNumbers = new LinkedHashMap<Constants.MSN, Long>();
        basePeakNumber = -1L;

        scanNumbers = new ArrayList<LinkedHashMap<Integer, Long>>();
        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs an empty mass spec raw file.
     *
     * @param id           the file identifier
     * @param oldContainer the old raw container
     */
    public FileRawContainer(String id, RawContainer oldContainer) {
        this(id, oldContainer.getWorkingDirectory());
        this.rawInfo = oldContainer.getRawInfo();
    }

    /**
     * Constructs fully configured raw file.
     *
     * @param id             the identifier
     * @param rawInfo        the file meta description
     * @param rawLevels      the collection of ms level information
     * @param basePeakNumber the randomaccesfile pointer to access the base profile chromatogram
     * @param ticNumbers     randomaccesfile pointers to access the total ion chromatograms
     * @param scanNumbers    scan index - randomaccesfile pointer associations
     * @param dataFile       the tmp data file
     */
    public FileRawContainer(String id, RawInfo rawInfo, List<RawLevel> rawLevels, Long basePeakNumber,
            LinkedHashMap<Constants.MSN, Long> ticNumbers, List<LinkedHashMap<Integer, Long>> scanNumbers,
            String dataFile) {

        this.id = id;
        this.rawInfo = rawInfo;
        this.rawLevels = rawLevels;

        ticData = null;
        basePeakData = null;

        this.ticNumbers = ticNumbers;
        this.basePeakNumber = basePeakNumber;
        this.scanNumbers = scanNumbers;

        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Adds a scan to the raw file and serializes the scan information.
     *
     * @param scan the scan to be added
     */
    @Override
    public void addScan(Scan scan) {

        Constants.MSN scanMSn = scan.getMsn();
        long fileIndex = fileManager.write(scan);

        if (scanMSn.getLvl() <= scanNumbers.size()) {
            scanNumbers.get(scanMSn.getLvl() - 1).put(scan.getIndex(), fileIndex);

            rawLevels.get(scanMSn.getLvl() - 1).getMzRange().extendRange(scan.getMzRange());
            rawLevels.get(scanMSn.getLvl() - 1).getScanRange().extendRange(scan.getRetentionTime());

            if (scanMSn == Constants.MSN.MS1 && !scan.getBasePeak().isEmpty())
                basePeakData.add(new XYPoint(scan.getRetentionTime(), scan.getBasePeak().get(0).y));
            ticData.get(scanMSn).add(new XYPoint(scan.getRetentionTime(), scan.getTotalIonCurrent()));
        } else {
            LinkedHashMap<Integer, Long> scanLevelMap = new LinkedHashMap<Integer, Long>();
            scanLevelMap.put(scan.getIndex(), fileIndex);
            scanNumbers.add(scanLevelMap);

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

        if (date != null) {
            rawInfo.setDate(date);
        }

        BasePeakChromatogram basePeakChromatogram = new BasePeakChromatogram(id, Constants.MSN.MS1, basePeakData);
        basePeakNumber = fileManager.write(basePeakChromatogram);

        for (Constants.MSN msn : Constants.MSN.values()) {
            if (msn.getLvl() > rawLevels.size()) break;

            XYList ticLevelData = ticData.get(msn);
            TotalIonChromatogram totalIonChromatogram = new TotalIonChromatogram(id, msn, ticLevelData);
            long ticPk = fileManager.write(totalIonChromatogram);
            ticNumbers.put(msn, ticPk);
        }
        ticData = null;
        basePeakData = null;

        fileManager.setTmp(false);
        fileManager.closeFile();
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
     *
     * @param msn a MSN level
     * @return the number of scans
     */
    @Override
    public int size(Constants.MSN msn) {
        return scanNumbers.get(msn.getLvl() - 1).size();
    }

    /**
     * Returns the scan index - file pointer map for the given MSn level.
     *
     * @param msn the MSn level
     * @return the scan index - file pointer map
     */
    public Map<Integer, Long> getScanNumbers(Constants.MSN msn) {

        if (scanNumbers.size() < msn.getLvl()) return null;
        return scanNumbers.get(msn.getLvl() - 1);
    }

    /**
     * Returns the base profile chromatogram.
     *
     * @return the base profile chromatogram
     */
    @Override
    public Chromatogram getBasePeakChromatogram() {

        if (basePeakNumber == -1) return null;

        Chromatogram chromatogram = fileManager.read(basePeakNumber, BasePeakChromatogram.class);

        return chromatogram;
    }

    /**
     * Returns the total ion chromatogram of level MSn.
     *
     * @param msn the MSn level
     * @return the total ion chromatogram
     */
    @Override
    public synchronized Chromatogram getTicChromatogram(Constants.MSN msn) {

        if (!ticNumbers.containsKey(msn)) return null;

        Chromatogram chromatogram = fileManager.read(ticNumbers.get(msn), TotalIonChromatogram.class);

        return chromatogram;
    }

    /**
     * The randomaccessfile pointer for the base profile chromatogram.
     *
     * @return the pointer
     */
    public Long getBasePeakNumber() {
        return basePeakNumber;
    }

    /**
     * The randomaccessfile pointer for the total ion chromatogram.
     *
     * @param msn the MSn level
     * @return the pointer
     */
    public Long getTicNumber(Constants.MSN msn) {
        return (!ticNumbers.containsKey(msn)) ? -1L : ticNumbers.get(msn);
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return fileManager.getWorkingDirectory();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    @Override
    public boolean removeAll() {
        return fileManager.removeFile();
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        return fileManager.getDataFile();
    }

    /**
     * Returns the size of the container
     *
     * @return the container's size
     */
    @Override
    public int size() {
        return scanNumbers.size();
    }

    /**
     * Returns the scan with the given scan index.
     *
     * @param i the scan index
     * @return the scan
     */
    @Override
    public synchronized Scan getScan(int i) {

        long fileIndex = -1;
        for (Map<Integer, Long> map : scanNumbers) {
            if (map.containsKey(i)) {
                fileIndex = map.get(i);
                break;
            }
        }
        if (fileIndex == -1) return null;
        Scan scan = fileManager.read(fileIndex, ScanImpl.class);

        return scan;
    }

    /**
     * Returns a list of scans for the given MSn level.
     *
     * @param msn the MSn level
     * @return the list of scans
     */
    public synchronized List<Scan> getScanList(Constants.MSN msn) {

        if (scanNumbers.size() < msn.getLvl()) return null;

        List<Scan> scans = new ArrayList<Scan>();
        for (long start : scanNumbers.get(msn.getLvl() - 1).values())
            scans.add(fileManager.read(start, ScanImpl.class));

        return scans;
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
    public synchronized ProfileMsnHelper getMsnHelper() {
        return new ProfileMsnHelper(this);
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

        if (!(aRawFile instanceof RawContainer)) return false;

        RawContainer rawContainer = (RawContainer) aRawFile;

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
        Scan scan;
        for (RawLevel level : this.getRawLevels()) {

            for (int scanIndex : this.getScanNumbers(level.getMsn()).keySet()) {
                scan = this.getScan(scanIndex);
                if (!scan.equals(rawContainer.getScan(scanIndex))) return false;
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
        return new RawIterator(new ArrayList<>(scanNumbers.get(0).values()), fileManager);
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
                return new RawIterator(new ArrayList<>(scanNumbers.get(msn.getLvl() - 1).values()), fileManager);
            }
        };
    }

    /**
     * Returns a profile iterator.
     *
     * @return the profile iterator
     */
    @Override
    public Iterable<Profile> profileIterator() {
        throw new MassCascadeException("Method not implemented.");
    }
}
