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

package uk.ac.ebi.masscascade;

import info.monitorenter.gui.chart.views.ChartPanel;
import org.junit.Test;
import uk.ac.ebi.masscascade.charts.SimpleSpectrum;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataSet;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class TicViewerTest {

    @Test
    public void testTicViewer() {

        // get mzML resource
        URL url = this.getClass().getResource("/uk/ac/ebi/masscascade/data/Sample.mzML");
        File file = new File(url.getFile());

        // build the output data container
        RawContainer container = MemoryContainerBuilder.getInstance().newInstance(RawContainer.class, file.getName());

        // build the parameter map for the reader task
        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.RAW_CONTAINER, container);

        // create and run the task to read the file
        CallableTask task = new PsiMzmlReader(params);
        RawContainer outContainer = (RawContainer) task.call();

        // get the total ion chromatogram from the data container
        Chromatogram tic = outContainer.getTicChromatogram(Constants.MSN.MS1);
        DataSet ticData = new DataSet.Builder(tic.getData(), container.getId()).color(Color.BLUE).build();

        // create the chart and add the TIC data
        SimpleSpectrum ticChart = new SimpleSpectrum();
        ticChart.addData(ticData);
        ticChart.setAxisTitle("time [s]", "intensity");

        // create the frame
        JFrame frame = new JFrame("TIC");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // embed the chart in a chart panel to enable zooming and the popup menu
        ChartPanel panel = new ChartPanel(ticChart);
        frame.add(panel);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        TicViewerTest ticViewer = new TicViewerTest();
        ticViewer.testTicViewer();
    }
}
