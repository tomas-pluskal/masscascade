package uk.ac.ebi.masscascade.threed;

import org.apache.commons.math3.util.FastMath;
import org.jzy3d.maths.Coord3d;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.List;

public class ThreeDParser {

    private static final float SCALE = 1000f;

    private double mzBinSize;
    private Range mzRange;
    private int nMzBins;
    private double timeBinSize;
    private Range timeRange;
    private int nTimeBins;

    public ThreeDParser(Range timeRange, double timeBinSize, Range mzRange, double mzBinSize) {

        this.mzBinSize = mzBinSize;
        this.mzRange = mzRange;
        this.timeBinSize = timeBinSize;
        this.timeRange = timeRange;

        nTimeBins = (int) FastMath.ceil((timeRange.getUpperBounds() - timeRange.getLowerBounds()) / timeBinSize);
        nMzBins = (int) FastMath.ceil((mzRange.getUpperBounds() - mzRange.getLowerBounds()) / mzBinSize);
    }

    public List<Coord3d> getBinnedData(RawContainer container) {

        double zMax = 0;
        float[][] bCoordinates = new float[nTimeBins][nMzBins];
        for (Scan scan : container) {
            int rtBin = (int) FastMath.floor((scan.getRetentionTime() - timeRange.getLowerBounds()) / timeBinSize);
            for (XYPoint dp : scan.getData()) {
                int mzBin = (int) FastMath.floor((dp.x - mzRange.getLowerBounds()) / mzBinSize);
                bCoordinates[rtBin][mzBin] += (float) dp.y;
                if (bCoordinates[rtBin][mzBin] > zMax) zMax = bCoordinates[rtBin][mzBin];
            }
        }

        List<Coord3d> coordinates = new ArrayList<>();
        for (int iTimeBin = 0; iTimeBin < bCoordinates.length; iTimeBin++) {
            for (int iMzBin = 0; iMzBin < bCoordinates[iTimeBin].length; iMzBin++) {
                if (bCoordinates[iTimeBin][iMzBin] == 0) continue;
                coordinates.add(new Coord3d((float) (iTimeBin * timeBinSize), (float) (iMzBin * mzBinSize),
                        (float) (bCoordinates[iTimeBin][iMzBin] * SCALE / zMax)));
            }
        }

        return coordinates;
    }
}
