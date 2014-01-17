package uk.ac.ebi.masscascade.io;

import uk.ac.ebi.pride.jmztab.model.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.SortedMap;
import java.util.TreeMap;

public class MzTabTemplate {

    public MzTabTemplate(File file) {

        MZTabFile mzTabFile = new MZTabFile(convertMetadata());

        mzTabFile.setSmallMoleculeColumnFactory(MZTabColumnFactory.getInstance(Section.Small_Molecule));
        mzTabFile.addSmallMolecule(smallMolecule(mzTabFile.getMetadata()));

        try {
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
            mzTabFile.printMZTab(bout);
            bout.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MzTabTemplate(new File("C:/Users/stephan/Desktop/out.mzTab"));
    }

    protected Metadata convertMetadata() {

        Metadata metadata = new Metadata();
        metadata.setMZTabID("Cytidine");
        metadata.setDescription("LC-MS/MS Reference Standard");

        metadata.addSampleProcessingParam(1, new CVParam("MS", "MS:1000544", "Conversion to mzML", ""));
        metadata.addSampleProcessingParam(1,  new CVParam("MS", "MS:1000035", "Peak picking", ""));
        metadata.addSampleProcessingParam(1,  new CVParam("MS", "MS:1001994", "Top Hat baseline reduction", ""));
        metadata.addSampleProcessingParam(1,  new CVParam("MS", "MS:1000782", "Savitzky-Golay smoothing", ""));
        metadata.addSampleProcessingParam(1,  new CVParam("MS", "MS:1000594", "Low intensity data point removal", ""));

        SortedMap<Integer, Software> softwareMap = new TreeMap<>();
        Software software1 = new Software(1);
        software1.setParam(new CVParam("MS", "MS:1002205", "ProteoWizard msconvert", ""));
        software1.addSetting("Peak Picking MS1");
        Software software2 = new Software(2);
        software2.setParam(new CVParam("MS", "MS:1001457", "data processing software", "MassCascade-KNIME"));
        softwareMap.put(1, software1);
        softwareMap.put(2, software2);
        metadata.setSoftwareMap(softwareMap);

        SortedMap<Integer, Instrument> instrumentMap = new TreeMap<>();
        Instrument instrument = new Instrument(1);
        instrument.setName(new CVParam("MS", "MS:1000483", "Thermo Fisher Scientific instrument model", "LTQ Orbitrap Velos"));
        instrument.setSource(new CVParam("MS", "MS:1000008", "Ionization Type", "ESI"));
        instrument.setAnalyzer(new CVParam("MS", "MS:1000443", "Mass Analyzer Type", "Orbitrap"));
        instrument.setDetector(new CVParam("MS", "MS:1000453", "Detector", "Dynode Detector"));
        instrumentMap.put(1, instrument);
        metadata.setInstrumentMap(instrumentMap);

        Contact contact = new Contact(1);
        contact.setName("Stephan Beisken");
        contact.setEmail("beiken@ebi.ac.uk");
        contact.setAffiliation("European Bioinformatics Institute (EMBL-EBI)");
        metadata.addContact(contact);
        metadata.addUri(uri("http://www.ebi.ac.uk/metabolights/MTBLS38"));

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "Proteomics Standards Inititative mzML file format", "mzML file"));
        metadata.addMsRunLocation(1, url("ftp://ftp.ebi.ac.uk/pub/databases/metabolights/studies/public/MTBLS38/cytidine.mzML"));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1000767", "Native spectrum identifier format", ""));
        metadata.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "Collision-induced dissociation", ""));

        return metadata;
    }

    protected SmallMolecule smallMolecule(Metadata metadata) {

        SmallMolecule smallMolecule = new SmallMolecule(metadata);

        smallMolecule.setIdentifier("CHEBI:17562");
        smallMolecule.setChemicalFormula("C9H13N3O5");
        smallMolecule.setSmiles("Nc1ccn([C@@H]2O[C@H](CO)[C@@H](O)[C@H]2O)c(=O)n1");
        smallMolecule.setInchiKey("UHDGCWIWMRVCDJ-XVFCMESISA-N");
        smallMolecule.setDescription("Cytidine");

        smallMolecule.setExpMassToCharge(244.0928);
        smallMolecule.addRetentionTime(193.25);
        smallMolecule.setCharge(+1);

        smallMolecule.setDatabase("ChEBI");
        smallMolecule.setDatabaseVersion("109");
        smallMolecule.setReliability(Reliability.High);
        smallMolecule.setURI(uri("http://www.ebi.ac.uk/metabolights/MTBLS38"));

        smallMolecule.addSearchEngineParam(new CVParam("MS", "MS:1001083", "ms-ms search", "MassBank"));
        smallMolecule.addBestSearchEngineScoreParam(new CVParam("MS", "MS:1001153", "search engine specific score", "977"));

        smallMolecule.addModification(new Modification(Section.Small_Molecule, Modification.Type.CHEMMOD, "2M+H"));
        smallMolecule.addModification(new Modification(Section.Small_Molecule, Modification.Type.CHEMMOD, "M-C5H8O4"));

        return smallMolecule;
    }

    private URI uri(String identifier) {
        URI uri = null;
        try {
            uri = new URI(identifier);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        return uri;
    }

    private URL url(String identifier) {
        URL url = null;
        try {
            url = new URL(identifier);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        return url;
    }
}
