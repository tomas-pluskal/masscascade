package uk.ac.ebi.masscascade.bless;

import org.junit.Test;
import uk.ac.ebi.masscascade.bless.table.BlessPane;
import uk.ac.ebi.masscascade.bless.table.BlessTable;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TableTest {

    @Test
    public void testTable() throws Exception {

        //
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CompoundSpectrum spectrum1 = new CompoundSpectrum(1);
        CompoundSpectrum spectrum2 = new CompoundSpectrum(2);
        CompoundSpectrum spectrum3 = new CompoundSpectrum(3);

        spectrum1.setMajorPeak(1);
        spectrum2.setMajorPeak(2);
        spectrum3.setMajorPeak(1);

        List<XYPoint> peakList = new ArrayList<>();
        peakList.add(new XYPoint(10, 500));
        peakList.add(new XYPoint(40, 900));
        peakList.add(new XYPoint(50, 2500));
        peakList.add(new XYPoint(25, 100));
        peakList.add(new XYPoint(70, 5000));

        spectrum1.setPeakList(peakList);
        spectrum2.setPeakList(peakList);
        spectrum3.setPeakList(peakList);

        CompoundEntity e1 = new CompoundEntity(1, 500, "Entity1", Status.INTERMEDIATE, Evidence.MSI_2, null, null);
        CompoundEntity e2 = new CompoundEntity(2, 800, "Entity2", Status.STRONG, Evidence.MSI_1, null, null);

        CompoundEntity e5 = new CompoundEntity(5, 900, "Entity6", Status.INTERMEDIATE, Evidence.MSI_3, null, null);
        CompoundEntity e6 = new CompoundEntity(6, 400, "Entity7", Status.STRONG, Evidence.MSI_2, null, null);

        CompoundEntity e9 = new CompoundEntity(9, 200, "Entity9", Status.WEAK, Evidence.MSI_4, null, null);
        CompoundEntity e10 = new CompoundEntity(10, 500, "Entity10", Status.INTERMEDIATE, Evidence.MSI_3, null, null);

        List<CompoundEntity> es = new ArrayList<>();
        es.add(e1);
        es.add(e2);

        List<CompoundEntity> es2 = new ArrayList<>();
        es2.add(e5);
        es2.add(e6);

        List<CompoundEntity> es3 = new ArrayList<>();
        es3.add(e9);
        es3.add(e10);

        spectrum1.setCompounds(es);
        spectrum2.setCompounds(es3);
        spectrum3.setCompounds(es2);

        List<CompoundSpectrum> spectra = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            spectra.add(spectrum1);
            spectra.add(spectrum2);
            spectra.add(spectrum3);
        }

        JTable table = new BlessTable(spectra);
        JScrollPane pane = new BlessPane(table);
        frame.add(pane);
        frame.setVisible(true);
    }
}
