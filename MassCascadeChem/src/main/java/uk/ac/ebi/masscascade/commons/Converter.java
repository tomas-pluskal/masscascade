package uk.ac.ebi.masscascade.commons;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

public class Converter {

    private static final Logger LOGGER = Logger.getLogger(Converter.class);
    private final static String INCHI_PREFIX = "inchi";

    public static IAtomContainer lineNotationToCDK(String lineNotation) {

        IAtomContainer molecule = null;
        try {
            if (lineNotation.toLowerCase().startsWith(INCHI_PREFIX)) {
                InChIToStructure iTs = InChIGeneratorFactory.getInstance().getInChIToStructure(lineNotation,
                        SilentChemObjectBuilder.getInstance());
                molecule = iTs.getAtomContainer();
            } else {
                SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
                molecule = parser.parseSmiles(lineNotation);
            }

        } catch (Exception exception) {
            LOGGER.log(Level.DEBUG, "Error converting line notation: " + lineNotation);
        }

        return molecule;
    }
}
