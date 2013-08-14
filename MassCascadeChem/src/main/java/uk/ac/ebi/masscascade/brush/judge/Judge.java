package uk.ac.ebi.masscascade.brush.judge;

import uk.ac.ebi.masscascade.compound.CompoundSpectrum;

import java.util.List;

/**
 * Interface of a judge defining basic methods.
 */
public interface Judge {

    /**
     * The core method of the judge executing the filtering process.
     *
     * @param compoundSpectra the input list of compound spectra
     * @return the filtered input list
     */
    List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra);

    /**
     * Returns the number of removed or filtered compound spectra.
     *
     * @return the number of removed or filtered compound spectra
     */
    int removed();
}
