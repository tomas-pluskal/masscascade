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

package uk.ac.ebi.masscascade.parameters;

import uk.ac.ebi.masscascade.exception.MassCascadeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class holding valid task parameters and their respective values.
 */
public class ParameterMap {

    private final Map<Parameter, Object> parameters;

    /**
     * Creates an empty parameter map.
     */
    public ParameterMap() {

        parameters = new HashMap<Parameter, Object>();
    }

    /**
     * Creates a new parameter map from the given parameter hash map.
     *
     * @param parameters the parameter hash map
     */
    public ParameterMap(Map<Parameter, Object> parameters) {

        this.parameters = parameters;
    }

    /**
     * Adds a parameter value pair to the map.
     *
     * @param parameter the parameter
     * @param instance  the value instance
     * @param <T>       any object that complies with the parameter value type
     */
    public <T> void put(Parameter parameter, T instance) {

        if (parameter == null) throw new MassCascadeException("Parameter is null");

        if (instance.getClass().isInstance(parameter.getType())) {
            if (!(parameter.getType().equals(Double.class) && instance.getClass().equals(Integer.class)))
                throw new MassCascadeException("Parameter value is not of type " + parameter.getType());
        }

        parameters.put(parameter, instance);
    }

    /**
     * Gets a parameter value of the given type.
     *
     * @param parameter the parameter
     * @param type      the value type
     * @param <T>       any object that complies with the parameter value type
     * @return the parameter's value
     */
    public <T> T get(Parameter parameter, Class<T> type) {

        if (parameter.getType() != type) {
            if (!(type.equals(Double.class) && parameter.getType().equals(Integer.class)))
                throw new MassCascadeException("Parameter value is not of type " + type);
        }

        if (!parameters.containsKey(parameter)) throw new MassCascadeException(parameter.name() + " is not in the map");

        return type.cast(parameters.get(parameter));
    }

    /**
     * Returns true if the parameter map contains the parameter key.
     *
     * @param parameter the parameter
     * @return if the parameter key is in the map
     */
    public boolean containsKey(Parameter parameter) {

        return parameters.containsKey(parameter);
    }

    /**
     * Creates and returns a copy of this object.
     */
    public ParameterMap clone() {

        return new ParameterMap(new HashMap(parameters));
    }
}
