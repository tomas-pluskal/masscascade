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

package uk.ac.ebi.masscascade.utilities;

import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLSpectrum;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.buffer.Base64;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Class providing utility methods for spectrum operations.
 */
public class ScanUtils {

    /**
     * Defines the number of bytes required in an unencoded byte array to hold a single double value.
     */
    public static final int BYTES_64_PRECISION = 8;
    /**
     * Defines the number of bytes required in an unencoded byte array to hold a single float value.
     */
    public static final int BYTES_32_PRECISION = 4;
    /**
     * Defines the number of data points a spectrum must have to qualify as acquired in profile mode.
     */
    private static final int MIN_PROFILE = 10;

    /**
     * Checks if the spectrum is centroided.
     *
     * @param xyList a spectrum m/z-intensity list
     * @return boolean whether the spectrum is centroided
     */
    public static boolean isCentroided(XYList xyList) {

        int dps = xyList.size();
        if (dps <= MIN_PROFILE) return true;

        boolean centroid = false;
        Range mzRange;
        boolean hasZeroDP = false;

        mzRange = new ExtendableRange(xyList.getFirst().x);

        for (int i = 0; i < dps; i++) {
            mzRange.extendRange(xyList.get(i).x);
            if (xyList.get(i).y == 0) hasZeroDP = true;
        }

        if (!hasZeroDP) return true;

        double massStep = mzRange.getSize() / dps;
        double previousMass = xyList.getFirst().x;
        double diff;

        for (int j = 0; j < dps; j++) {
            diff = Math.abs(xyList.get(j).x - previousMass);
            previousMass = xyList.get(j).x;
            if (xyList.get(j).y == 0) continue;
            else if (diff > (massStep * 1.5d)) return true;
        }

        return centroid;
    }

    /**
     * Removes data points with zero intensity from the data set.
     *
     * @param xyList          a spectrum m/z-intensity list
     * @param acquisitionMode an acquisition mode
     * @return the cleaned spectrum data set
     */
    public static XYList removeZeroAndDuplicateDataPoints(XYList xyList, Constants.ACQUISITION_MODE acquisitionMode) {

        XYPoint prevDp = new XYPoint(-1, -1);
        boolean isValid = true;
        for (XYPoint point : xyList) {
            if (point.y == 0 || point.x == prevDp.x) {
                isValid = false;
                break;
            }
            prevDp = point;
        }

        if (isValid) return xyList;

        prevDp = new XYPoint(-1, -1);
        XYList newXYList = new XYList();
        for (int i = 0; i < xyList.size(); i++) {

            XYPoint xyPoint = xyList.get(i);

            if (xyPoint.x == prevDp.x) {
                prevDp = xyPoint;
                continue;
            }
            prevDp = xyPoint;

            if (xyPoint.y > 0) {
                newXYList.add(new XYPoint(xyPoint.x, xyPoint.x));
                continue;
            }

            if (acquisitionMode != Constants.ACQUISITION_MODE.CENTROID) {
                if ((i > 0) && (xyList.get(i - 1).y > 0)) {
                    newXYList.add(xyList.get(i));
                    continue;
                }
                if ((i < xyList.size() - 1) && (xyList.get(i + 1).y > 0)) {
                    newXYList.add(xyList.get(i));
                    continue;
                }
            }
        }

        if (newXYList.size() == xyList.size()) return xyList;

        return newXYList;
    }

    /**
     * Decompresses a compressed byte array using the ZLIB compression library.
     *
     * @param compressedData a compressed byte array
     * @return the decompressed byte array
     */
    public static byte[] decompress(byte[] compressedData) {

        byte[] decompressedData;

        // using a ByteArrayOutputStream to not having to define the result array size beforehand
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);
        // create an expandable byte array to hold the decompressed data
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                throw new MassCascadeException(
                        "Encountered wrong data CmlSpectrumFactory while trying to decompress binary data!", e);
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        decompressedData = bos.toByteArray();

        if (decompressedData == null) {
            throw new IllegalStateException("Decompression of binary data prodeuced no result (null)!");
        }
        return decompressedData;
    }

    /**
     * Compresses a byte array using the ZLIB compression library.
     *
     * @param uncompressedData the compressed byte array
     * @return the compressed byte array
     */
    public static byte[] compress(byte[] uncompressedData) {

        byte[] data;

        // create a temporary byte array big enough to hold the compressed data
        // with the worst compression (the length of the initial (uncompressed) data)
        byte[] temp = new byte[uncompressedData.length];
        // compress
        Deflater compresser = new Deflater();
        compresser.setInput(uncompressedData);
        compresser.finish();
        int cdl = compresser.deflate(temp);
        // create a new array with the size of the compressed data (cdl)
        data = new byte[cdl];
        System.arraycopy(temp, 0, data, 0, cdl);

        return data;
    }

    /**
     * Converts an array of type double to binary with 64 bit precision. The binary data is set as content to a cmlAxis
     * object using MiG Base64 encoding. Optionally the binary data can be compressed.
     *
     * @param value    a data value to convert
     * @param compress an option to compress the binary data
     * @param cmlArray a x- or y-axis CML object
     * @return the encoded bit length
     */
    public static int set64BitFloatArrayAsBinaryData(double[] value, boolean compress, CMLArray cmlArray) {

        int dataLength;
        ByteBuffer buffer = ByteBuffer.allocate(value.length * BYTES_64_PRECISION);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (double aDoubleArray : value) {
            buffer.putDouble(aDoubleArray);
        }
        byte[] data = buffer.array();
        dataLength = data.length;

        if (compress) {
            // data needs compressing
            data = compress(data);
        }
        String dataString = Base64.encodeToString(data, true);
        cmlArray.setStringContent(dataString);

        return dataLength;
    }

    /**
     * Converts an array of type double to binary with 64 bit precision. The binary data is set as content to a cmlAxis
     * object using MiG Base64 encoding. Optionally the binary data can be compressed.
     *
     * @param value       a data value to convert
     * @param compress    option to compress the binary data
     * @param cmlSpectrum a CML spectrum object
     * @return the encoded bit length
     */
    public static int set64BitLongArrayAsBinaryData(Long[] value, boolean compress, CMLSpectrum cmlSpectrum) {

        int dataLength;
        ByteBuffer buffer = ByteBuffer.allocate(value.length * BYTES_64_PRECISION);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (Long aLong : value) {
            buffer.putLong(aLong);
        }
        byte[] data = buffer.array();
        dataLength = data.length;

        if (compress) {
            // data needs compressing
            data = compress(data);
        }
        String dataString = Base64.encodeToString(data, true);
        cmlSpectrum.setStringContent(dataString);

        return dataLength;
    }

    /**
     * Converts a Base64 encoded string into an array of type double with 64 bit precision. The string is first decoded
     * into binary data and optionally decompressed. The binary data is converted into the double array.
     *
     * @param value      a string to convert
     * @param decompress an option to decompress the binary data
     * @return the decoded double array
     */
    public static double[] get64BitFloatArrayFromBinaryData(String value, boolean decompress) {

        byte[] data = Base64.decode(value.toCharArray());
        if (decompress) {
            // data needs decompressing
            data = decompress(data);
        }
        // create a double array of sufficient size
        double[] resultArray = new double[data.length / BYTES_64_PRECISION];
        // create a buffer around the data array for easier retrieval
        ByteBuffer bb = ByteBuffer.wrap(data);
        // the order is always LITTLE_ENDIAN
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // progress in steps of 8 bytes
        double d;
        for (int indexOut = 0; indexOut < data.length; indexOut += BYTES_64_PRECISION) {
            d = bb.getDouble(indexOut);
            resultArray[indexOut / BYTES_64_PRECISION] = d;
        }
        return resultArray;
    }

    /**
     * Converts a Base64 encoded string into an array of type double with 64 bit precision. The string is first decoded
     * into binary data and optionally decompressed. The binary data is converted into the double array.
     *
     * @param value      a string to convert
     * @param decompress an option to decompress the binary data
     * @return the decoded double array
     */
    public static LinkedHashMap<Integer, Long> get64BitLongArrayFromBinaryData(String value, boolean decompress) {

        byte[] data = Base64.decode(value.toCharArray());
        if (decompress) {
            // data needs decompressing
            data = decompress(data);
        }
        // create a buffer around the data array for easier retrieval
        ByteBuffer bb = ByteBuffer.wrap(data);
        // the order is always LITTLE_ENDIAN
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // progress in steps of 8 bytes
        LinkedHashMap<Integer, Long> results = new LinkedHashMap<Integer, Long>();
        int i;
        Long p;
        for (int indexOut = 0; indexOut < data.length; indexOut += BYTES_64_PRECISION * 2) {
            i = (int) bb.getLong(indexOut);
            p = bb.getLong(indexOut + BYTES_64_PRECISION);
            results.put(i, p);
        }
        return results;
    }

    /**
     * Method returning a deep clone of a scan with new scan data.
     *
     * @param scan       a scan template
     * @param dataPoints new scan data
     * @return the new scan
     */
    public static Scan getModifiedScan(Scan scan, XYList dataPoints) {

        return new ScanImpl(scan.getIndex(), scan.getMsn(), scan.getIonMode(), dataPoints, scan.getRetentionTime(),
                scan.getParentScan(), scan.getParentCharge(), scan.getParentMz());
    }
}
