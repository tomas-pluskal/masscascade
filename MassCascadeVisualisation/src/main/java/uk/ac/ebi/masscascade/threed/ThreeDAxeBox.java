package uk.ac.ebi.masscascade.threed;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.rendering.view.Camera;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class ThreeDAxeBox extends AxeBox {

    public ThreeDAxeBox(BoundingBox3d bbox) {
        super(bbox);
    }

    /**
     * Set the parameters and data of the AxeBox.
     */
    @Override
    protected void setAxeBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {

        System.out.println(xmin);

        // Compute center
        center = new Coord3d((xmax + xmin) / 2, (ymax + ymin) / 2, (zmax + zmin) / 2);
        xrange = xmax - xmin;
        yrange = ymax - ymin;
        zrange = zmax - zmin;

        // Define configuration of 6 quads (faces of the box)
        quadx = new float[6][4];
        quady = new float[6][4];
        quadz = new float[6][4];

        // x near
        quadx[0][0] = xmax;
        quady[0][0] = ymin;
        quadz[0][0] = zmax;
        quadx[0][1] = xmax;
        quady[0][1] = ymin;
        quadz[0][1] = zmin;
        quadx[0][2] = xmax;
        quady[0][2] = ymax;
        quadz[0][2] = zmin;
        quadx[0][3] = xmax;
        quady[0][3] = ymax;
        quadz[0][3] = zmax;
        // x far
        quadx[1][0] = xmin;
        quady[1][0] = ymax;
        quadz[1][0] = zmax;
        quadx[1][1] = xmin;
        quady[1][1] = ymax;
        quadz[1][1] = zmin;
        quadx[1][2] = xmin;
        quady[1][2] = ymin;
        quadz[1][2] = zmin;
        quadx[1][3] = xmin;
        quady[1][3] = ymin;
        quadz[1][3] = zmax;
        // y near
        quadx[2][0] = xmax;
        quady[2][0] = ymax;
        quadz[2][0] = zmax;
        quadx[2][1] = xmax;
        quady[2][1] = ymax;
        quadz[2][1] = zmin;
        quadx[2][2] = xmin;
        quady[2][2] = ymax;
        quadz[2][2] = zmin;
        quadx[2][3] = xmin;
        quady[2][3] = ymax;
        quadz[2][3] = zmax;
        // y far
        quadx[3][0] = xmin;
        quady[3][0] = ymin;
        quadz[3][0] = zmax;
        quadx[3][1] = xmin;
        quady[3][1] = ymin;
        quadz[3][1] = zmin;
        quadx[3][2] = xmax;
        quady[3][2] = ymin;
        quadz[3][2] = zmin;
        quadx[3][3] = xmax;
        quady[3][3] = ymin;
        quadz[3][3] = zmax;
        // z top
        quadx[4][0] = xmin;
        quady[4][0] = ymin;
        quadz[4][0] = zmax;
        quadx[4][1] = xmax;
        quady[4][1] = ymin;
        quadz[4][1] = zmax;
        quadx[4][2] = xmax;
        quady[4][2] = ymax;
        quadz[4][2] = zmax;
        quadx[4][3] = xmin;
        quady[4][3] = ymax;
        quadz[4][3] = zmax;
        // z down
        quadx[5][0] = xmax;
        quady[5][0] = ymin;
        quadz[5][0] = zmin;
        quadx[5][1] = xmin;
        quady[5][1] = ymin;
        quadz[5][1] = zmin;
        quadx[5][2] = xmin;
        quady[5][2] = ymax;
        quadz[5][2] = zmin;
        quadx[5][3] = xmax;
        quady[5][3] = ymax;
        quadz[5][3] = zmin;

        // Define configuration of each quad's normal
        normx = new float[6];
        normy = new float[6];
        normz = new float[6];

        normx[0] = xmax;
        normy[0] = 0;
        normz[0] = 0;
        normx[1] = xmin;
        normy[1] = 0;
        normz[1] = 0;
        normx[2] = 0;
        normy[2] = ymax;
        normz[2] = 0;
        normx[3] = 0;
        normy[3] = ymin;
        normz[3] = 0;
        normx[4] = 0;
        normy[4] = 0;
        normz[4] = zmax;
        normx[5] = 0;
        normy[5] = 0;
        normz[5] = zmin;

        // Define quad intersections that generate an axe
        // axe{A}quads[i][q]
        // A = axe direction (X, Y, or Z)
        // i = axe id (0 to 4)
        // q = quad id (0 to 1: an intersection is made of two quads)
        int na = 4; // n axes per dimension
        int np = 2; // n points for an axe
        int nq = 2;
        int i;      // axe id

        axeXquads = new int[na][nq];
        axeYquads = new int[na][nq];
        axeZquads = new int[na][nq];

        i = 0;
        axeXquads[i][0] = 4;
        axeXquads[i][1] = 3; // quads making axe x0
        i = 1;
        axeXquads[i][0] = 3;
        axeXquads[i][1] = 5; // quads making axe x1
        i = 2;
        axeXquads[i][0] = 5;
        axeXquads[i][1] = 2; // quads making axe x2
        i = 3;
        axeXquads[i][0] = 2;
        axeXquads[i][1] = 4; // quads making axe x3
        i = 0;
        axeYquads[i][0] = 4;
        axeYquads[i][1] = 0; // quads making axe y0
        i = 1;
        axeYquads[i][0] = 0;
        axeYquads[i][1] = 5; // quads making axe y1
        i = 2;
        axeYquads[i][0] = 5;
        axeYquads[i][1] = 1; // quads making axe y2
        i = 3;
        axeYquads[i][0] = 1;
        axeYquads[i][1] = 4; // quads making axe y3
        i = 0;
        axeZquads[i][0] = 3;
        axeZquads[i][1] = 0; // quads making axe z0
        i = 1;
        axeZquads[i][0] = 0;
        axeZquads[i][1] = 2; // quads making axe z1
        i = 2;
        axeZquads[i][0] = 2;
        axeZquads[i][1] = 1; // quads making axe z2
        i = 3;
        axeZquads[i][0] = 1;
        axeZquads[i][1] = 3; // quads making axe z3

        // Define configuration of 4 axe per dimension:
        //  axe{A}d[i][p], where
        //
        //  A = axe direction (X, Y, or Z)
        //  d = dimension (x coordinate, y coordinate or z coordinate)
        //  i = axe id (0 to 4)
        //  p = point id (0 to 1)
        //
        // Note: the points making an axe are from - to +
        //       (i.e. direction is given by p0->p1)

        axeXx = new float[na][np];
        axeXy = new float[na][np];
        axeXz = new float[na][np];
        axeYx = new float[na][np];
        axeYy = new float[na][np];
        axeYz = new float[na][np];
        axeZx = new float[na][np];
        axeZy = new float[na][np];
        axeZz = new float[na][np];

        i = 0; // axe x0
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymin;
        axeXz[i][0] = zmax;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymin;
        axeXz[i][1] = zmax;
        i = 1; // axe x1
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymin;
        axeXz[i][0] = zmin;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymin;
        axeXz[i][1] = zmin;
        i = 2; // axe x2
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymax;
        axeXz[i][0] = zmin;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymax;
        axeXz[i][1] = zmin;
        i = 3; // axe x3
        axeXx[i][0] = xmin;
        axeXy[i][0] = ymax;
        axeXz[i][0] = zmax;
        axeXx[i][1] = xmax;
        axeXy[i][1] = ymax;
        axeXz[i][1] = zmax;
        i = 0; // axe y0
        axeYx[i][0] = xmax;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmax;
        axeYx[i][1] = xmax;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmax;
        i = 1; // axe y1
        axeYx[i][0] = xmax;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmin;
        axeYx[i][1] = xmax;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmin;
        i = 2; // axe y2
        axeYx[i][0] = xmin;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmin;
        axeYx[i][1] = xmin;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmin;
        i = 3; // axe y3
        axeYx[i][0] = xmin;
        axeYy[i][0] = ymin;
        axeYz[i][0] = zmax;
        axeYx[i][1] = xmin;
        axeYy[i][1] = ymax;
        axeYz[i][1] = zmax;
        i = 0; // axe z0
        axeZx[i][0] = xmax;
        axeZy[i][0] = ymin;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmax;
        axeZy[i][1] = ymin;
        axeZz[i][1] = zmax;
        i = 1; // axe z1
        axeZx[i][0] = xmax;
        axeZy[i][0] = ymax;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmax;
        axeZy[i][1] = ymax;
        axeZz[i][1] = zmax;
        i = 2; // axe z2
        axeZx[i][0] = xmin;
        axeZy[i][0] = ymax;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmin;
        axeZy[i][1] = ymax;
        axeZz[i][1] = zmax;
        i = 3; // axe z3
        axeZx[i][0] = xmin;
        axeZy[i][0] = ymin;
        axeZz[i][0] = zmin;
        axeZx[i][1] = xmin;
        axeZy[i][1] = ymin;
        axeZz[i][1] = zmax;

        layout.getXTicks(xmin, xmax); // prepare ticks to display in the layout tick buffer
        layout.getYTicks(ymin, ymax);
        layout.getZTicks(zmin, zmax);
    }
}
