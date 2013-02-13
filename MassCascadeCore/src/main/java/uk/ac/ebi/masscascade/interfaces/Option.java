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

package uk.ac.ebi.masscascade.interfaces;

/**
 * Interface describing the required methods for a valid task option.
 */
public interface Option {

    /**
     * Gets the description of the option.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets the default value of the option.
     *
     * @return the default value
     */
    Object getDefaultValue();

    /**
     * Gets the class of the option's value.
     *
     * @return the option's value
     */
    Class<?> getType();
}
