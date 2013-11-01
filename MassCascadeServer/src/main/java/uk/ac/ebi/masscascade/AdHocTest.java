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
import uk.ac.ebi.masscascade.background.BaselineSubtraction;
import uk.ac.ebi.masscascade.charts.SimpleSpectrum;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.featurebuilder.SequentialFeatureBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataSet;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class AdHocTest {

    public static void main(String[] args) {

        String sample = "C:\\Users\\Stephan\\Workspace\\IntelliJProjects\\" +
                "MassCascade\\MassCascadeCore\\src\\test\\resources\\uk\\ac\\ebi\\masscascade\\data\\Sample.mzML";
        File file = new File(sample);

        ScanContainer container = MemoryContainerBuilder.getInstance().newInstance(ScanContainer.class, file.getName());

        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.SCAN_CONTAINER, container);

        CallableTask task1 = new PsiMzmlReader(params);

        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_FEATURE_INTENSITY, 1000d);
        params.put(Parameter.MIN_FEATURE_WIDTH, 4);
        params.put(Parameter.SCAN_CONTAINER, task1.call());

        CallableTask task2 = new SequentialFeatureBuilder(params);

        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 6);
        params.put(Parameter.FEATURE_CONTAINER, task2.call());

        SimpleSpectrum spectrum = new SimpleSpectrum();
        ChartPanel cp = new ChartPanel(spectrum);
        FeatureContainer fc1 = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
        DataSet baseline = new DataSet.Builder(fc1.getFeature(6).getTrace().getData(), "T").color(Color.RED).build();

        CallableTask task3 = new BaselineSubtraction(params);
        FeatureContainer fc2 = (FeatureContainer) task3.call();
        SimpleSpectrum spectrum1 = new SimpleSpectrum();
        ChartPanel cp1 = new ChartPanel(spectrum1);
        JFrame frame1 = new JFrame();
        frame1.add(cp1);
        frame1.setSize(640, 480);
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        spectrum1.addData(new DataSet.Builder(fc2.getFeature(6).getTrace().getData(), "T").color(Color.GREEN).build());
        spectrum1.addData(baseline);
        frame1.repaint();
    }
}
