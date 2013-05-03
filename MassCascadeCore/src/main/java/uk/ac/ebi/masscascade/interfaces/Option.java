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

package uk.ac.ebi.masscascade.interfaces;

/**
 * This is an option describing required methods for a valid task option.
 */
public interface Option {

    /**
     * Returns the description of the option.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Returns the default value of the option.
     *
     * @return the default value
     */
    Object getDefaultValue();

    /**
     * Returns the class of the option's value.
     *
     * @return the option's value
     */
    Class<?> getType();

    /**
     * Returns the option's name.
     *
     * @return the name
     */
    String name();
}
