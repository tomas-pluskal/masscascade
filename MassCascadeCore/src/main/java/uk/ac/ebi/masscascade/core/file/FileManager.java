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

package uk.ac.ebi.masscascade.core.file;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.chromatogram.BasePeakChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.core.chromatogram.TotalIonChromatogram;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.RunManager;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.properties.Label;
import uk.ac.ebi.masscascade.properties.Score;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;
import uk.ac.ebi.masscascade.utilities.range.MovingRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Class managing all random access file operations.
 */
public class FileManager implements RunManager, Serializable {

    private static final Logger LOGGER = Logger.getLogger(FileManager.class);
    private static final long serialVersionUID = -5655253365433590239L;

    private File dataFile;
    private RandomAccessFile randomAccessFile;
    private Kryo kryo;

    /**
     * Constructs a raw file manager.
     *
     * @param workingDirectory the working directory
     */
    public FileManager(String workingDirectory) {

        try {
            dataFile = File.createTempFile("masscascade_", ".tmp", new File(workingDirectory));
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "Tmp file could could not be opened: " + exception.getMessage());
        }

        buildAndRegisterKryo();
    }

    /**
     * Constructs a raw file manager with an existing data file.
     *
     * @param dataFile the data file
     */
    public FileManager(File dataFile) {

        this.dataFile = dataFile;

        buildAndRegisterKryo();
    }

    /**
     * Register all relevant objects with the Kryo serialization instance.
     */
    private void buildAndRegisterKryo() {

        kryo = new Kryo();

        kryo.register(ScanImpl.class);
        kryo.register(ProfileImpl.class);
        kryo.register(PseudoSpectrum.class);
        kryo.register(TotalIonChromatogram.class);
        kryo.register(BasePeakChromatogram.class);
        kryo.register(MassChromatogram.class);
        kryo.register(ExtendableRange.class);
        kryo.register(ToleranceRange.class);
        kryo.register(MovingRange.class);
        kryo.register(XYList.class);
        kryo.register(XYPoint.class);
        kryo.register(XYZList.class);
        kryo.register(XYZPoint.class);
        kryo.register(YMinPoint.class);
        kryo.register(PropertyManager.class);
        kryo.register(Constants.ION_MODE.class);
        kryo.register(Constants.ACQUISITION_MODE.class);
        kryo.register(Constants.DOMAIN.class);
        kryo.register(Constants.MSN.class);
        kryo.register(Constants.MSN_MODE.class);
        kryo.register(Constants.FILE_FORMATS.class);
        kryo.register(Constants.PARTICLES.class);
        kryo.register(PropertyManager.TYPE.class);
        kryo.register(Label.class);
        kryo.register(Isotope.class);
        kryo.register(Adduct.class);
        kryo.register(Score.class);
        kryo.register(Identity.class);
        kryo.register(boolean[].class);
        kryo.register(Double[].class);
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeMultimap.class);
        kryo.register(LinkedHashMap.class);
        kryo.register(LinkedHashSet.class);
    }

    /**
     * Opens the file for serialization/deserialization.
     *
     * @return is success
     */
    public void openFile() throws MassCascadeException {

        try {
            randomAccessFile = new RandomAccessFile(dataFile, "rw");
        } catch (FileNotFoundException exception) {
            throw new MassCascadeException("File Manager on open: " + exception.getMessage());
        }
    }

    /**
     * Closes the file for serialization/deserialization
     *
     * @return is success
     */
    public void closeFile() throws MassCascadeException {

        try {
            randomAccessFile.close();
        } catch (IOException exception) {
            throw new MassCascadeException("File Manager on close: " + exception.getMessage());
        }
    }

    /**
     * Serializes the object given.
     *
     * @param object the object for serialization
     * @return the pointer
     */
    public long write(Object object) {

        long start = -1L;
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 256 * 32);
            kryo.writeObject(buffer, object);

            int length = buffer.position();
            byte[] data = buffer.array();
            start = randomAccessFile.length();

            randomAccessFile.seek(start);
            randomAccessFile.writeInt(length);
            randomAccessFile.write(data, 0, length);
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "File Manager on write: " + exception.getMessage());
        }
        return start;
    }

    /**
     * Deserializes an object.
     *
     * @param start the object pointer
     * @return the object
     */
    public synchronized <T> T read(long start, Class<T> objectClass) {

        if (start == -1) return null;

        Object object = null;
        try {
            openFile();
            randomAccessFile.seek(start);
            int length = randomAccessFile.readInt();

            byte[] data = new byte[length];
            randomAccessFile.readFully(data);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            object = kryo.readObject(buffer, objectClass);
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "File Manager on read: " + exception.getMessage());
        } finally {
            closeFile();
        }

        return objectClass.cast(object);
    }

    /**
     * Deserializes a list of scans.
     *
     * @param startPositions scan pointers
     * @return the list of scans
     */
    public List<Object> read(Collection<Long> startPositions, Class objectClass) {

        List<Object> objectList = new ArrayList<Object>();
        for (long start : startPositions) objectList.add(this.read(start, objectClass));
        return objectList;
    }

    /**
     * Returns the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return dataFile.getName();
    }

    /**
     * Returns the absolute file name.
     *
     * @return the absolute file name
     */
    public String getAbsoluteFileName() {
        return dataFile.getAbsolutePath();
    }

    /**
     * Returns the path of the working directory.
     *
     * @return the working directory
     */
    public String getWorkingDirectory() {

        String absPath = dataFile.getAbsolutePath();
        return absPath.substring(0, absPath.lastIndexOf(File.separator));
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    public File getDataFile() {
        return dataFile;
    }

    /**
     * Deletes the file.
     *
     * @return if successful
     */
    public boolean removeFile() {
        return dataFile.delete();
    }
}
