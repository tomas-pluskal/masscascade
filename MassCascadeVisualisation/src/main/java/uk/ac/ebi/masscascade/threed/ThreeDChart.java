package uk.ac.ebi.masscascade.threed;

import org.apache.commons.math3.util.FastMath;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.CameraKeyController;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.MousePickingController;
import org.jzy3d.chart.controllers.mouse.selection.ScatterMouseSelector;
import org.jzy3d.chart.controllers.mouse.selection.SphereMouseSelector;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.IntegerTickRenderer;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.HashMap;
import java.util.Map;

public class ThreeDChart {

    private double mzBinSize;
    private Range mzRange;
    private int nMzBins;
    private double timeBinSize;
    private Range timeRange;
    private int nTimeBins;

    public ThreeDChart(Range timeRange, double timeBinSize, Range mzRange, double mzBinSize) {

        this.mzBinSize = mzBinSize;
        this.mzRange = mzRange;
        this.timeBinSize = timeBinSize;
        this.timeRange = timeRange;

        nTimeBins = (int) FastMath.ceil(timeRange.getRange() / timeBinSize);
        nMzBins = (int) FastMath.ceil(mzRange.getRange() / mzBinSize);
    }

    public void plot(RawContainer container) {

        double zMax = 0;
        double[][] bCoordinates = new double[nTimeBins][nMzBins];
        for (Scan scan : container) {
            int rtBin = (int) FastMath.floor((scan.getRetentionTime() - timeRange.getMin()) / timeBinSize);
            for (XYPoint dp : scan.getData()) {
                int mzBin = (int) FastMath.floor((dp.x - mzRange.getMin()) / mzBinSize);
                bCoordinates[rtBin][mzBin] += dp.y;
                if (bCoordinates[rtBin][mzBin] > zMax) zMax = bCoordinates[rtBin][mzBin];
            }
        }

        Map<XYPoint, Double> coordMap = new HashMap<>();
        for (int row = 0; row < bCoordinates.length; row++) {
            for (int col = 0; col < bCoordinates[row].length; col++) {
                if (bCoordinates[row][col] == 0) continue;
                coordMap.put(new XYPoint(row * timeBinSize, col * mzBinSize), bCoordinates[row][col] * 1000d / zMax);
            }
        }

        Mapper mapper = new ThreeDMapper(coordMap);
        OrthonormalGrid grid = new OrthonormalGrid(timeRange, nTimeBins + 1, mzRange, nMzBins + 1);
        CompileableComposite composite = Builder.buildOrthonormalBig(grid, mapper);
        composite.setColorMapper(new ColorMapper(new ColorMapRainbow(), 0f, 50f));

        // Create a chart and add scatter
        Chart chart = new Chart();

        AbstractCameraController mouseController = new ThreeDMouseController(chart);
        chart.addController(mouseController);
        chart.getView().setSquared(false);
        chart.getView().setAxeBoxDisplayed(false);

//        chart.addController(new CameraMouseController());
        chart.addController(new CameraKeyController());

        chart.getAxeLayout().setFaceDisplayed(false);
        chart.getAxeLayout().setMainColor(Color.BLACK);

        ITickProvider xTickProvider = new SmartTickProvider(10);
        ITickRenderer xTickRenderer = new IntegerTickRenderer();
        chart.getAxeLayout().setZTickProvider(xTickProvider);
        chart.getAxeLayout().setZTickRenderer(xTickRenderer);
        chart.getAxeLayout().setXAxeLabel("Time [s]");

        ITickProvider yTickProvider = new SmartTickProvider(10);
        ITickRenderer yTickRenderer = new DefaultDecimalTickRenderer();
        chart.getAxeLayout().setZTickProvider(yTickProvider);
        chart.getAxeLayout().setZTickRenderer(yTickRenderer);
        chart.getAxeLayout().setYAxeLabel("m/z [amu]");

        ITickProvider zTickProvider = new SmartTickProvider(10);
        ITickRenderer zTickRenderer = new IntegerTickRenderer();
        chart.getAxeLayout().setZTickProvider(zTickProvider);
        chart.getAxeLayout().setZTickRenderer(zTickRenderer);
        chart.getAxeLayout().setZAxeLabel("Abundance [units]");

        chart.getView().setBackgroundColor(Color.WHITE);
        chart.getScene().add(composite);

        ChartLauncher.openChart(chart);
    }
}
