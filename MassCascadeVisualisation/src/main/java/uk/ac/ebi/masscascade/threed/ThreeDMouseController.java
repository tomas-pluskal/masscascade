package uk.ac.ebi.masscascade.threed;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Scale;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class ThreeDMouseController extends CameraMouseController {

    public ThreeDMouseController(Chart chart) {
        super(chart);
    }

    /**
     * Compute zoom
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (threadController != null) threadController.stop();

        float factor = 1 + (e.getWheelRotation() / 20.0f);

        BoundingBox3d b3d = super.chart().getView().getBounds();
        BoundingBox3d zb3d = b3d.scale(new Coord3d(factor, factor, factor));
        super.chart().getView().setBoundManual(zb3d);
    }

    /**
     * Compute shift or rotate
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Coord2d mouse = new Coord2d(e.getX(), e.getY());

        // Rotate
        if (MouseUtilities.isLeftDown(e)) {
            Coord2d move = mouse.sub(prevMouse).div(200);
            rotate(move);
        }
        // Shift
        else if (MouseUtilities.isRightDown(e)) {
            Coord2d move = mouse.sub(prevMouse).mul(10);

            BoundingBox3d b3d = super.chart().getView().getBounds();
            b3d.shift(new Coord3d(move, (float) Math.random() * 100));

            super.chart().getView().setBoundMode(ViewBoundMode.MANUAL);
            super.chart().getView().setBoundManual(b3d);
        }
        prevMouse = mouse;
    }
}
