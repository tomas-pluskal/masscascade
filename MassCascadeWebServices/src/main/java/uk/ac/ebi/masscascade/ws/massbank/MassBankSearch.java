/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.ws.massbank;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class to execute a spectrum query against MassBank.
 */
public class MassBankSearch extends CallableTask {

    private int cutoff;
    private int maxNumOfResults;
    private double tolerance;
    private List<String> instruments;
    private Constants.ION_MODE ionMode;

    private MassBankAPIStub stub;

    private static final String IUPAC = "CH$IUPAC: ";

    private SpectrumContainer spectrumContainer;

    /**
     * Constructor for a MassBankSearch search task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public MassBankSearch(ParameterMap params) throws MassCascadeException {

        super(MassBankSearch.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the MassBankSearch search.
     *
     * @param params the parameter objects
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          description of the Exception
     */
    @SuppressWarnings("unchecked")
    private void setParameters(ParameterMap params) throws MassCascadeException {

        cutoff = params.get(Parameter.MIN_PROFILE_INTENSITY, Integer.class);
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        instruments = params.get(Parameter.INSTRUMENTS, (new ArrayList<String>()).getClass());
        tolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        maxNumOfResults = params.get(Parameter.RESULTS, Integer.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, FileSpectrumContainer.class);
    }

    /**
     * Executes the task.
     *
     * @return the annotated spectrum data
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outContainer = new FileSpectrumContainer(id, spectrumContainer.getWorkingDirectory());

        try {

            stub = new MassBankAPIStub();

            ExecutorService executor = Executors.newFixedThreadPool(Constants.NTHREADS);
            List<Future<PseudoSpectrum>> futureList = new ArrayList<Future<PseudoSpectrum>>();

            for (PseudoSpectrum ps : spectrumContainer) {

                Callable<PseudoSpectrum> mbS = new MbSearch(ps);
                futureList.add(executor.submit(mbS));
            }

            for (Future<PseudoSpectrum> mbS : futureList) {

                try {
                    outContainer.addSpectrum(mbS.get());
                } catch (InterruptedException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                } catch (ExecutionException e) {
                    LOGGER.log(Level.ERROR, e.getMessage());
                }
            }

            executor.shutdown();
        } catch (AxisFault exception) {
            LOGGER.log(Level.ERROR, exception.getMessage());
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    class MbSearch implements Callable<PseudoSpectrum> {

        private PseudoSpectrum mbSpectrum;

        public MbSearch(PseudoSpectrum mbSpectrum) {

            this.mbSpectrum = mbSpectrum;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public PseudoSpectrum call() throws Exception {

            String[] mzs = new String[mbSpectrum.getProfileMap().size()];
            String[] ints = new String[mbSpectrum.getProfileMap().size()];

            double[] intsVal = new double[mbSpectrum.getProfileMap().size()];
            TreeSet<Double> mzsOrder = new TreeSet<Double>();

            double max = Double.MIN_VALUE;

            int i = 0;
            for (Profile profile : mbSpectrum) {

                double mz = profile.getMz();
                double intensity = profile.getIntensity();

                mzs[i] = "" + mz;
                intsVal[i] = intensity;

                if (intensity > max) max = intensity;

                mzsOrder.add(mz);

                i++;
            }

            i = 0;
            LinearEquation lq = new LinearEquation(new XYPoint(0, 0), new XYPoint(max, 1000));
            for (double inten : intsVal) {
                ints[i] = "" + lq.getY(inten);
                i++;
            }

            MassBankAPIStub.SearchSpectrum sp = new MassBankAPIStub.SearchSpectrum();
            sp.setCutoff("" + cutoff);
            sp.setInstrumentTypes(instruments.toArray(new String[]{}));
            sp.setIntensities(ints);
            sp.setMzs(mzs);
            sp.setIonMode(ionMode.name().toLowerCase());
            sp.setTolerance("" + tolerance);
            sp.setMaxNumResults(maxNumOfResults);
            sp.setUnit("ppm");

            MassBankAPIStub.SearchSpectrumResponse ssr = stub.searchSpectrum(sp);
            MassBankAPIStub.Result[] results = ssr.get_return().getResults();

            if (results != null) {
                for (MassBankAPIStub.Result result : results) {
                    String id = result.getId();
                    String title = result.getTitle();
                    double mass = Double.parseDouble(result.getExactMass());
                    double score = Double.parseDouble(result.getScore());

                    if (ionMode == Constants.ION_MODE.POSITIVE) {
                        mass = mass + Constants.PARTICLES.PROTON.getMass();
                    } else if (ionMode.equals(Constants.ION_MODE.NEGATIVE)) {
                        mass = mass - Constants.PARTICLES.PROTON.getMass();
                    }

                    double res = DataUtils.getNearestIndexRel(mass, tolerance, mzsOrder);
                    if (res != -1d) {

                        String iupacString = getIUPACNotation(id);

                        Identity identity = new Identity(id, title, iupacString, score);

                        for (Profile profile : mbSpectrum) {
                            if (profile.getMz() == res) {
                                profile.setProperty(identity);
                            }
                        }
                    }
                }
            }

            return mbSpectrum;
        }
    }

    /**
     * Retrieves the full record info and extracts the InChI.
     *
     * @param id the record id
     * @return the inchi string
     * @throws Exception SOAP retrieval exception
     */
    private String getIUPACNotation(String id) throws Exception {

        String iupacString = "";

        MassBankAPIStub.GetRecordInfo rci = new MassBankAPIStub.GetRecordInfo();
        rci.addIds(id);
        MassBankAPIStub.GetRecordInfoResponse rciR = stub.getRecordInfo(rci);
        String resString = rciR.get_return()[0].getInfo();

        int iupacIndex = resString.indexOf(IUPAC);
        if (iupacIndex != -1) {
            iupacIndex = iupacIndex + IUPAC.length();
            int iupacStop = resString.indexOf('\n', iupacIndex);
            if (iupacStop == -1) iupacStop = resString.indexOf('\r', iupacIndex);
            if (iupacStop != -1) iupacString = resString.substring(iupacIndex, iupacStop);
        }

        return iupacString;
    }
}
