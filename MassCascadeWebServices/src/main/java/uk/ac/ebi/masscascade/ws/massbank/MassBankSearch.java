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

package uk.ac.ebi.masscascade.ws.massbank;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableWebservice;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
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
 * Web task to run a featureset search against MassBank using the MassBank web service.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value in ppm.</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> RESULTS </code>- The max. number of retrieved results.</li>
 * <li>Parameter <code> MS LEVEL </code>- The MSn level to be queried.</li>
 * <li>Parameter <code> INSTRUMENTS </code>- The instruments to be included in the query.</li>
 * <li>Parameter <code> MIN PROFILE INTENSITY </code>- The min. valid feature intensity.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class MassBankSearch extends CallableWebservice {

    private double ppm;
    private int cutoff;
    private int maxNumOfResults;
    private List<String> instruments;
    private Constants.MSN msn;
    private Constants.ION_MODE ionMode;

    private MassBankAPIStub stub;

    private static final String IUPAC = "CH$IUPAC: ";

    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs a web task for the MassBank web service.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public MassBankSearch(ParameterMap params) throws MassCascadeException {

        super(MassBankSearch.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        cutoff = params.get(Parameter.MIN_FEATURE_INTENSITY, Integer.class);
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        instruments = params.get(Parameter.INSTRUMENTS, (new ArrayList<String>()).getClass());
        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        msn = params.get(Parameter.MS_LEVEL, Constants.MSN.class);
        maxNumOfResults = params.get(Parameter.RESULTS, Integer.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        try {
            stub = new MassBankAPIStub();

            ExecutorService executor = Executors.newFixedThreadPool(Constants.NTHREADS);
            List<Future<FeatureSet>> futureList = new ArrayList<>();

            for (FeatureSet ps : featureSetContainer) {
                Callable<FeatureSet> searcher = new SpectrumSearcher(ps);
                futureList.add(executor.submit(searcher));
            }

            for (Future<FeatureSet> search : futureList) {
                try {
                    outContainer.addFeatureSet(search.get());
                } catch (InterruptedException e) {
                    LOGGER.log(Level.ERROR, e);
                } catch (ExecutionException e) {
                    LOGGER.log(Level.ERROR, e);
                }
            }

            executor.shutdown();
        } catch (AxisFault exception) {
            LOGGER.log(Level.ERROR, exception.getMessage());
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    /**
     * Runs MassBank's <code> SearchSpectrum </code> web service on the query featureset. The returned profiles in the
     * featureset are annotated with the retrieved results.
     */
    class SpectrumSearcher implements Callable<FeatureSet> {

        private FeatureSet featureSet;

        /**
         * Constructs a MassBank search helper.
         *
         * @param featureSet the featureset containing the profiles for the query.
         */
        public SpectrumSearcher(FeatureSet featureSet) {
            this.featureSet = featureSet;
        }

        /**
         * Converts the featureset into a MassBank compatible format and queries MassBank for compounds matching the
         * featureset.
         *
         * @return the annotated featureset
         * @throws Exception if unable to run the web service
         */
        @Override
        public FeatureSet call() throws Exception {

            if (msn == Constants.MSN.MS1) {
                queryMSn(featureSet, null);
            } else {
                for (Feature feature : featureSet) {
                    if (feature.hasMsnSpectra(msn)) {
                        for (FeatureSet msnFeatureSetX : feature.getMsnSpectra(msn)) {
                            int parentId = msnFeatureSetX.getParentScan();
                            if (feature.getId() == parentId) queryMSn(msnFeatureSetX, feature);
                            else queryMSn(msnFeatureSetX, feature.getMsnSpectra(msn.up()).get(0).getFeature(parentId));
                        }
                    }
                }
            }
            return featureSet;
        }

        private void queryMSn(FeatureSet featureSet, Feature parent) throws Exception {

            String[] mzs = new String[featureSet.getFeaturesMap().size()];
            String[] ints = new String[featureSet.getFeaturesMap().size()];

            double[] intsVal = new double[featureSet.getFeaturesMap().size()];
            TreeSet<Double> mzsOrder = new TreeSet<>();

            double max = Double.MIN_VALUE;

            int i = 0;
            for (Feature feature : featureSet) {
                double mz = feature.getMz();
                double intensity = feature.getIntensity();

                mzs[i] = "" + mz;
                intsVal[i] = intensity;

                if (intensity > max) max = intensity;

                mzsOrder.add(mz);

                i++;
            }

            i = 0;
            LinearEquation lq = new LinearEquation(new XYPoint(0, 0), new XYPoint(max, 1000));
            for (double inten : intsVal) ints[++i] = "" + lq.getY(inten);

            MassBankAPIStub.SearchSpectrum sp = new MassBankAPIStub.SearchSpectrum();
            sp.setCutoff("" + cutoff);
            sp.setInstrumentTypes(instruments.toArray(new String[instruments.size()]));
            sp.setIntensities(ints);
            sp.setMzs(mzs);
            sp.setIonMode(ionMode.name().toLowerCase());
            sp.setTolerance("" + ppm);
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

                    if (ionMode == Constants.ION_MODE.POSITIVE) mass = mass + Constants.PARTICLES.PROTON.getMass();
                    else if (ionMode.equals(Constants.ION_MODE.NEGATIVE))
                        mass = mass - Constants.PARTICLES.PROTON.getMass();

                    if (parent == null) {
                        Double closestValue = DataUtils.getClosestValue(mass, mzsOrder);
                        if (closestValue != null && new ToleranceRange(mass, ppm).contains(closestValue)) {
                            String iupacString = getIUPACNotation(id);
                            Identity identity =
                                    new Identity(id, title.split(";")[0], iupacString, score, "MassBank", msn.name(),
                                            title);
                            for (Feature feature : featureSet) {
                                if (feature.getMz() == closestValue) feature.setProperty(identity);
                            }
                        }
                    } else {
                        if (new ToleranceRange(parent.getMz(), ppm).contains(mass)) {
                            String iupacString = getIUPACNotation(id);
                            Identity identity =
                                    new Identity(id, title.split(";")[0], iupacString, score, "MassBank", msn.name(),
                                            title);
                            parent.setProperty(identity);
                        }
                    }
                }
            }
        }

        /**
         * Retrieves the full record info and extracts the InChI.
         *
         * @param id the record id
         * @return the inchi string
         * @throws Exception if SOAP retrieval fails
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
}
