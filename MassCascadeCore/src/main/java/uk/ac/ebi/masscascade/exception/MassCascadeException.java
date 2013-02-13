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

package uk.ac.ebi.masscascade.exception;

/**
 * Custom exception that is thrown by MassCascade classes.
 */
public class MassCascadeException extends RuntimeException {

    /**
     * Constructs a new MassCascadeException with the given message.
     *
     * @param message for the constructed exception
     */
    public MassCascadeException(String message) {

        super(message);
    }

    /**
     * Constructs a new MassCascadeException with the given message and the initial Exception.
     *
     * @param message for the constructed exception
     * @param cause   the throwable that triggered this MassCascadeException
     */
    public MassCascadeException(String message, Throwable cause) {

        super(message, cause);
    }
}
