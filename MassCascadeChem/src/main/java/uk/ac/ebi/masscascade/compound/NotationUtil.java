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

package uk.ac.ebi.masscascade.compound;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Utilities class for chemistry format conversion.
 */
public class NotationUtil {

    // CDK's coordinates generator for molecules
    private static StructureDiagramGenerator sdg = new StructureDiagramGenerator();

    /**
     * Converts a SMILES or InChI line notation into a fully configured, aromaticity-detected CDK molecule with 2D
     * layout. Unconnected fragments are removed, keeping the biggest fragment only.
     *
     * @param notation the SMILES or InChI
     * @return the layout CDK molecule
     */
    public static IAtomContainer getMoleculeTyped(String notation) {

        IAtomContainer molecule = getMolecule(notation);

        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            CDKHydrogenAdder hydra = CDKHydrogenAdder.getInstance(molecule.getBuilder());
            hydra.addImplicitHydrogens(molecule);
            hydra = null;
            if (ConnectivityChecker.isConnected(molecule)) {
                CDKHueckelAromaticityDetector.detectAromaticity(molecule);
            } else {
                IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(molecule);
                molecule.removeAllElements();
                for (IAtomContainer mol : moleculeSet.atomContainers()) {
                    CDKHueckelAromaticityDetector.detectAromaticity(mol);
                    molecule.add(mol);
                }
                moleculeSet = null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return molecule;
    }

    /**
     * Converts a SMILES or InChI line notation into a raw CDK molecule with 2D layout.
     *
     * @param notation the SMILES or InChI
     * @return the layout CDK molecule
     */
    public static IAtomContainer getMolecule(String notation) {

        IAtomContainer molecule = null;

        try {
            if (notation == null) return molecule;
            else if (notation.toLowerCase().startsWith("inchi")) molecule = convertInChI(notation);
            else molecule = convertSmiles(notation);

            if (ConnectivityChecker.isConnected(molecule)) {
                sdg.setMolecule(molecule);
                sdg.generateCoordinates();
                molecule = sdg.getMolecule();
            } else {
                return null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return molecule;
    }

    /**
     * Converts a SMILES or InChI line notation into a raw CDK molecule.
     *
     * @param notation the SMILES or InChI
     * @return the CDK molecule
     */
    public static IAtomContainer getMoleculePlain(String notation) {

        IAtomContainer molecule = null;

        try {
            if (notation == null) return molecule;
            else if (notation.toLowerCase().startsWith("inchi")) molecule = convertInChI(notation);
            else molecule = convertSmiles(notation);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return molecule;
    }

    /**
     * Converts SMILES into a CDK molecule.
     *
     * @param notation the SMILES string
     * @return the CDK molecule
     * @throws InvalidSmilesException if a parsing error has occurred
     */
    private static IAtomContainer convertSmiles(String notation) throws InvalidSmilesException {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        sp.setPreservingAromaticity(true);
        return sp.parseSmiles(notation);
    }

    /**
     * Converts InChI into a CDK molecule.
     *
     * @param notation the InChI string
     * @return the CDK molecule
     * @throws CDKException if a parsing error has occurred
     */
    private static IAtomContainer convertInChI(String notation) throws CDKException {

        return InChIGeneratorFactory.getInstance().getInChIToStructure(notation,
                SilentChemObjectBuilder.getInstance()).getAtomContainer();
    }
}
