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

package uk.ac.ebi.masscascade.utilities;

/**
 * Class providing label constants for MassCascade
 */
public class Labels {

    /**
     * Enumerated standard labels for MassCascade.
     */
    public enum LABELS {

        MZ("m/z"),
        INTENSITY("intensity"),
        ABUNDANCE("abundance"),
        RT("rt [s]"),
        MZ_FULL("mass-to-charge"),
        RT_FULL("retention time [s]"),
        SAMPLE("sample #");

        private final String label;

        /**
         * Constructs a new label enum.
         *
         * @param label the enum label's string label
         */
        private LABELS(String label) {

            this.label = label;
        }

        /**
         * Gets the label of the enum label constant.
         *
         * @return the string label
         */
        public String getLabel() {

            return label;
        }
    }
}
