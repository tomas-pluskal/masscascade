package uk.ac.ebi.masscascade.brush.judge;

import uk.ac.ebi.masscascade.compound.CompoundSpectrum;

import java.util.List;

public interface Judge {

    List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra);

    int removed();
}
