package uk.ac.ebi.masscascade.utilities.xyz;

/**
 * Class implementing a 3D data point. The implementation's default comparison uses the y value only.
 */
public class XYZPoint implements Comparable<XYZPoint> {

    public double x;
    public double y;
    public double z;

    /**
     * Constructs a data point where x = 0, y = 0, and z = 0.
     */
    public XYZPoint() {

        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Constructs a custom data point.
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     */
    public XYZPoint(double x, double y, double z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(x)).hashCode();
        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(y)).hashCode();
        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(z)).hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass()) return false;

        final XYZPoint other = (XYZPoint) obj;
        return !(this.x != other.x || this.y != other.y || this.z != other.z);
    }

    @Override
    public int compareTo(XYZPoint dp) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == dp) return EQUAL;
        if (y < dp.y) return BEFORE;
        if (y > dp.y) return AFTER;

        return EQUAL;
    }
}
