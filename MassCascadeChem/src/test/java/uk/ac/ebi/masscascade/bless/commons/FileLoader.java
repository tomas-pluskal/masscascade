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

package uk.ac.ebi.masscascade.bless.commons;

import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.distance.CosineSimilarityDistance;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.tracebuilder.ProfileBuilder;

import java.io.File;
import java.net.URL;

public class FileLoader {

    public enum TESTFILE {

        QC("/uk/ac/ebi/masscascade/data/QC.mzML");

        private final String path;

        private TESTFILE(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    public static RawContainer getRawContainer(TESTFILE testFile) {

        URL url = FileLoader.class.getResource(testFile.getPath());
        File file = new File(url.getFile());

        RawContainer container = MemoryContainerBuilder.getInstance().newInstance(RawContainer.class, file.getName());

        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.RAW_CONTAINER, container);

        CallableTask task = new PsiMzmlReader(params);
        return (RawContainer) task.call();
    }

    public static ProfileContainer getProfileContainer(TESTFILE testFile) {

        ParameterMap params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_PROFILE_INTENSITY, 1000d);
        params.put(Parameter.MIN_PROFILE_WIDTH, 4);
        params.put(Parameter.RAW_CONTAINER, getRawContainer(testFile));

        CallableTask task = new ProfileBuilder(params);
        return (ProfileContainer) task.call();
    }

    public static SpectrumContainer getSpectrumContainer(TESTFILE testFile) {

        ParameterMap params = new ParameterMap();
        params.put(Parameter.BINS, 10);
        params.put(Parameter.CORRELATION_THRESHOLD, 0.99);
        params.put(Parameter.PROFILE_CONTAINER, getProfileContainer(testFile));

        CallableTask task = new CosineSimilarityDistance(params);
        return (SpectrumContainer) task.call();
    }
}
