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

package uk.ac.ebi.masscascade.core.container.file.scan;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileContainer;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.chromatogram.BasePeakChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.TotalIonChromatogram;
import uk.ac.ebi.masscascade.core.scan.ScanInfo;
import uk.ac.ebi.masscascade.core.scan.ScanIterator;
import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.core.scan.ScanImpl;
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
public class FileScanContainer extends FileContainer implements ScanContainer {

    private static final Logger LOGGER = Logger.getLogger(FileScanContainer.class);

    private final String id;

    private ScanInfo scanInfo;
    private final List<ScanLevel> scanLevels;

    private final List<LinkedHashMap<Integer, Long>> scanNumbers;
    private final LinkedHashMap<Constants.MSN, Long> ticNumbers;
    private Long basePeakNumber;

    private LinkedHashMap<Constants.MSN, XYList> ticData;
    private XYList basePeakData;

    private final FileManager fileManager;

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id the file identifier
     */
    public FileScanContainer(String id) {
        this(id, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id               the file identifier
     * @param workingDirectory the working directory
     */
    public FileScanContainer(String id, String workingDirectory) {

        this.id = id;
        scanInfo = new ScanInfo(id, "Unknown", null);
        scanLevels = new ArrayList<ScanLevel>();

        ticData = new LinkedHashMap<>();
        basePeakData = new XYList();
        ticNumbers = new LinkedHashMap<>();
        basePeakNumber = -1L;

        scanNumbers = new ArrayList<>();
        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs an empty mass spec scan file.
     *
     * @param id           the file identifier
     * @param oldContainer the old scan container
     */
    public FileScanContainer(String id, ScanContainer oldContainer) {
        this(id, oldContainer.getWorkingDirectory());
        this.scanInfo = oldContainer.getScanInfo();
    }

    /**
     * Constructs fully configured scan file.
     *
     * @param id             the identifier
     * @param scanInfo       the file meta description
     * @param scanLevels     the collection of ms level information
     * @param basePeakNumber the randomaccesfile pointer to access the base feature chromatogram
     * @param ticNumbers     randomaccesfile pointers to access the total ion chromatograms
     * @param scanNumbers    scan index - randomaccesfile pointer associations
     * @param dataFile       the tmp data file
     */
    public FileScanContainer(String id, ScanInfo scanInfo, List<ScanLevel> scanLevels, Long basePeakNumber,
                             LinkedHashMap<Constants.MSN, Long> ticNumbers, List<LinkedHashMap<Integer, Long>> scanNumbers,
                             String dataFile) {

        this.id = id;
        this.scanInfo = scanInfo;
        this.scanLevels = scanLevels;

        ticData = null;
        basePeakData = null;

        this.ticNumbers = ticNumbers;
        this.basePeakNumber = basePeakNumber;
        this.scanNumbers = scanNumbers;

        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Adds a scan to the scan file and serializes the scan information.
     *
     * @param scan the scan to be added
     */
    @Override
    public void addScan(Scan scan) {

        Constants.MSN scanMSn = scan.getMsn();
        long fileIndex = fileManager.write(scan);

        if (scanMSn.getLvl() <= scanNumbers.size()) {
            scanNumbers.get(scanMSn.getLvl() - 1).put(scan.getIndex(), fileIndex);

            scanLevels.get(scanMSn.getLvl() - 1).getMzRange().extendRange(scan.getMzRange());
            scanLevels.get(scanMSn.getLvl() - 1).getScanRange().extendRange(scan.getRetentionTime());

            if (scanMSn == Constants.MSN.MS1 && !scan.getBasePeak().isEmpty())
                basePeakData.add(new XYPoint(scan.getRetentionTime(), scan.getBasePeak().get(0).y));
            ticData.get(scanMSn).add(new XYPoint(scan.getRetentionTime(), scan.getTotalIonCurrent()));
        } else {
            LinkedHashMap<Integer, Long> scanLevelMap = new LinkedHashMap<Integer, Long>();
            scanLevelMap.put(scan.getIndex(), fileIndex);
            scanNumbers.add(scanLevelMap);

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

        if (date != null) {
            scanInfo.setDate(date);
        }

        BasePeakChromatogram basePeakChromatogram = new BasePeakChromatogram(id, Constants.MSN.MS1, basePeakData);
        basePeakNumber = fileManager.write(basePeakChromatogram);

        for (Constants.MSN msn : Constants.MSN.values()) {
            if (msn.getLvl() > scanLevels.size()) break;

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
     * Returns the base feature chromatogram.
     *
     * @return the base feature chromatogram
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
     * The randomaccessfile pointer for the base feature chromatogram.
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
    public synchronized FeatureMsnHelper getMsnHelper() {
        return new FeatureMsnHelper(this);
    }

    /**
     * Checks if two mass spec data container are identical.
     *
     * @param aScanFile the mass spec data container to be compared to
     * @return boolean if the container is identical
     */
    @Override
    public boolean equals(Object aScanFile) {

        if (this == aScanFile) return true;

        if (!(aScanFile instanceof ScanContainer)) return false;

        ScanContainer scanContainer = (ScanContainer) aScanFile;

        // check data container meta information
        if (this.getId().equals(scanContainer.getId())) {

            if (this.getScanInfo().equals(scanContainer.getScanInfo())) {

                int i = 0;
                for (ScanLevel scanLevel : scanLevels) {

                    if (!scanLevel.equals(scanContainer.getScanLevels().get(i))) return false;
                    i++;
                }
            }
        } else {
            return false;
        }

        // check data container data
        Scan scan;
        for (ScanLevel level : this.getScanLevels()) {

            for (int scanIndex : this.getScanNumbers(level.getMsn()).keySet()) {
                scan = this.getScan(scanIndex);
                if (!scan.equals(scanContainer.getScan(scanIndex))) return false;
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
        return new ScanIterator(new ArrayList<>(scanNumbers.get(0).values()), fileManager);
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
                return new ScanIterator(new ArrayList<>(scanNumbers.get(msn.getLvl() - 1).values()), fileManager);
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
