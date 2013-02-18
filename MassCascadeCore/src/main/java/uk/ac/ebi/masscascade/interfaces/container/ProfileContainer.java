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

package uk.ac.ebi.masscascade.interfaces.container;

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.file.FileManager;
import uk.ac.ebi.masscascade.interfaces.Profile;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public interface ProfileContainer extends Container, Iterable<Profile> {

    void addProfile(Profile profile);

    void addProfileList(List<Profile> profileList);

    void finaliseFile();

    String getId();

    TreeMultimap<Double, Integer> getTimes();

    FileManager getFileManager();

    Profile getProfile(int i);

    String getWorkingDirectory();

    File getDataFile();

    boolean removeAll();

    @Override
    Iterator<Profile> iterator();
}
