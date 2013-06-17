package uk.ac.ebi.masscascade.bless;

import org.junit.Test;
import uk.ac.ebi.masscascade.bless.commons.FileLoader;
import uk.ac.ebi.masscascade.bless.table.BlessPane;
import uk.ac.ebi.masscascade.bless.table.BlessTable;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.compound.CompoundSpectrumAdapter;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.properties.Isotope;

import javax.swing.*;
import java.util.List;

public class TableTest {

    @Test
    public void testTable() throws Exception {

        SpectrumContainer container = FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC);

        container.getSpectrum(3).getProfile(245).setProperty(new Identity("123", "Ga", "CCCCC", 200, "F", "MS1", ""));
        container.getSpectrum(3).getProfile(245).setProperty(new Isotope("M+1", 1, 245, 246));
        container.getSpectrum(3).getProfile(245).setProperty(new Isotope("M+2", 2, 245, 247));

        CompoundSpectrumAdapter adapter = new CompoundSpectrumAdapter();
        List<CompoundSpectrum> cs = adapter.getSpectra(container);

        JFrame frame = new JFrame();
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable table = new BlessTable(cs);
        JScrollPane pane = new BlessPane(table);
        frame.add(pane);
        frame.setVisible(true);

        Thread.sleep(50000);
    }
}
