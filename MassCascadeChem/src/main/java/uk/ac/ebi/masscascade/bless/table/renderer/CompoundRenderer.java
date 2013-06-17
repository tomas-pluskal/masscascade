package uk.ac.ebi.masscascade.bless.table.renderer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import uk.ac.ebi.masscascade.bless.table.BlessTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompoundRenderer extends DefaultTableCellRenderer {

    private final AtomContainerRenderer renderer;

    public CompoundRenderer() {

        super();

        setBackground(Color.WHITE);

        List<IGenerator<IAtomContainer>> generators = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new RingGenerator());
        generators.add(new ExtendedAtomGenerator());
        generators.add(new AtomNumberGenerator());
        renderer = new AtomContainerRenderer(generators, new AWTFontManager());

        RendererModel renderer2dModel = renderer.getRenderer2DModel();
        renderer2dModel.set(RingGenerator.ShowAromaticity.class, true);
        renderer2dModel.set(RingGenerator.MaxDrawableAromaticRing.class, 9);
        renderer2dModel.set(BasicSceneGenerator.UseAntiAliasing.class, true);
        renderer2dModel.set(BasicAtomGenerator.ShowExplicitHydrogens.class, true);
        renderer2dModel.set(BasicAtomGenerator.ShowEndCarbons.class, true);
        renderer2dModel.set(ExtendedAtomGenerator.ShowImplicitHydrogens.class, true);
        renderer2dModel.set(AtomNumberGenerator.WillDrawAtomNumbers.class, false);
    }

    private boolean drawn;
    private IAtomContainer molecule;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == BlessTableModel.MOLECULE_COLUMN) {
            molecule = (IAtomContainer) value;
            drawn = false;
            label.setText("");
        }

        return label;
    }

    public void paint(Graphics g) {

        if (drawn) return;

        if (molecule == null)
            g.drawString("n/a", this.getWidth() / 2, this.getHeight() / 2);
        else {
            Rectangle bounds = new Rectangle(15, 15, this.getWidth() - 15, this.getHeight() - 15);
            renderer.paint(molecule, new AWTDrawVisitor((Graphics2D) g), bounds, true);
            drawn = true;
        }
    }
}
