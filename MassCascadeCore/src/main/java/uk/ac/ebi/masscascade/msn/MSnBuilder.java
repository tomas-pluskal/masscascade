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

package uk.ac.ebi.masscascade.msn;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZTrace;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class implementing an MSn builder method. The method compiles a representative MSn spectra for each profile that has
 * MSn references. The MSn references point to the RAW container. For each MSn level, one MSn spectrum is generated,
 * only taking into account signals that are present in all MSn scans of a particular level and that are above the
 * intensity threshold.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass tolerance in ppm.</li>
 * <li>Parameter <code> MIN PROFILE INTENSITY </code>- The minimum profile intensity.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class MSnBuilder extends CallableTask {

    private double ppm;
    private double minIntensity;
    private RawContainer rawContainer;
    private SpectrumContainer spectrumContainer;

    private final TreeMap<Trace, Boolean> traceToExtended = new TreeMap<>();

    /**
     * Constructor for a MSn builder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MSnBuilder(ParameterMap params) throws MassCascadeException {

        super(MSnBuilder.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the MSn builder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        minIntensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
    }

    /**
     * Executes the MSn builder task.
     *
     * @return the spectrum container
     */
    @Override
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        for (Spectrum spectrum : spectrumContainer) {
            for (Profile profile : spectrum) {
                if (profile.hasMsnScans()) compileMSnSpectra(profile);
            }
            outSpectrumContainer.addSpectrum(spectrum);
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
    }

    private void compileMSnSpectra(Profile profile) {

        for (Map.Entry<Constants.MSN, Set<Integer>> entry : profile.getMsnScans().entrySet()) {

            if (entry.getKey().getLvl() < 2 || entry.getKey().getLvl() > 5) continue;

            traceToExtended.clear();

            for (int scanId : entry.getValue()) {
                Scan scan = rawContainer.getScan(scanId);
                if (scan == null) continue;

                double rt = scan.getRetentionTime();
                XYList scanDps = scan.getData();
                for (int i = 0; i < scanDps.size(); i++) {

                    XYPoint currentDp = scanDps.get(i);
                    double nextDpMz = (currentDp == scanDps.getLast()) ? Double.MAX_VALUE : scanDps.get(i + 1).x;

                    XYZTrace signalTrace = new XYZTrace(currentDp, rt);
                    XYZTrace closestTrace = (XYZTrace) DataUtils.getClosestKey(signalTrace, traceToExtended);

                    if (closestTrace == null) addTrace(signalTrace);
                    else if (traceToExtended.containsKey(signalTrace)) {
                        if (!traceToExtended.get(signalTrace)) appendTrace(signalTrace, closestTrace);
                    } else if (isCloserThanNext(closestTrace.getAvg(), currentDp.x, nextDpMz)) {
                        if (isWithinParameter(closestTrace.getAvg(), currentDp.x, traceToExtended.get(closestTrace)))
                            appendTrace(signalTrace, closestTrace);
                        else addTrace(signalTrace);
                    } else addTrace(signalTrace);
                }

                Iterator<Map.Entry<Trace, Boolean>> iter = traceToExtended.entrySet().iterator();
                while (iter.hasNext()) iter.next().setValue(false);
            }

            annotateProfile(profile, entry.getKey(), traceToExtended);
        }
    }

    private boolean isCloserThanNext(double avg, double curMz, double nextMz) {
        return FastMath.abs(avg - curMz) <= FastMath.abs(avg - nextMz);
    }

    private boolean isWithinParameter(double avg, double mz, boolean extended) {
        return (new ToleranceRange(avg, ppm).contains(mz) && !extended);
    }

    private void addTrace(XYZTrace trace) {

        XYZPoint dp = trace.getData().get(0);
        trace.push(new XYZPoint(dp.x - 1, dp.y, Constants.MIN_ABUNDANCE));
        traceToExtended.put(trace, true);
    }

    private void appendTrace(XYZTrace signalTrace, XYZTrace closestTrace) {

        closestTrace.add(signalTrace.get(0));
        traceToExtended.put(closestTrace, true);
    }

    private void annotateProfile(Profile profile, Constants.MSN msn, TreeMap<Trace, Boolean> traceToExtended) {

        Set<Profile> spectrumProfiles = new HashSet<>();
        XYList spectrumData = new XYList();

        int id = 1;
        int totalMsnScans = profile.getMsnScans().get(msn).size();
        for (Trace trace : traceToExtended.keySet()) {
            if (trace.size() - 1 != totalMsnScans) continue;

            XYZTrace xyzTrace = (XYZTrace) trace;

            Range mzRange = new ExtendableRange(xyzTrace.get(0).y);
            Profile msnProfile = new ProfileImpl(id++, xyzTrace.get(0), mzRange, profile.getPropertyManager());
            for (int i = 1; i < trace.size(); i++) msnProfile.addProfilePoint(xyzTrace.get(i));
            msnProfile.closeProfile(((XYZTrace) trace).get(trace.size() - 1).x + 1);

            if (msnProfile.getIntensity() < minIntensity) continue;

            spectrumProfiles.add(msnProfile);
            spectrumData.add(msnProfile.getMzIntDp());
        }

        if (spectrumProfiles.size() == 0) return;

        Range rtRange = new ExtendableRange(spectrumProfiles.iterator().next().getRetentionTime());
        double rt = 0;
        for (Profile msnProfile : spectrumProfiles) rt += msnProfile.getRetentionTime();
        rt /= spectrumProfiles.size();

        Spectrum spectrum = new PseudoSpectrum(1, spectrumData, rtRange, rt, spectrumProfiles);
        spectrum.setParent(profile.getId(), profile.getMz(), 0);
        profile.addMsnSpectrum(msn, spectrum);
    }
}
