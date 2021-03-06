MassCascade: Mass Spectrometry Library
-----
Copyright (C) 2013 EMBL - European Bioinformatics Institute
Contributors: Stephan Beisken
Tags: mass spectrometry, tandem mass spectrometry, analytical data processing
License: GPLv3 or later
License URI: http://www.gnu.org/licenses/gpl-3.0.html

The MassCascade library provides functionality for tandem mass spectrometry data processing and visualisation.
It provides a plug-in for the workflow platform KNIME and has been tested with metabolomics LC-MS data.

Description
-------
The following input formats are supported (centroid data):
* Thermo RAW file format
* PSI mzML file format

The core classes are representations of essential mass spectrometry objects. These core objects are manipulated by task
instances, each carrying out a single task on the core object. To reduce memory load and ensure persistence within a
workflow environment, every task can serialize the core instance to a specified directory if required.

Installation
-------
The library can be freely used as backend for mass spectrometry data processing. All task classes implement the
'Callable<?>' interface and can be configured via the 'setParameters()' method.

The library is used in the KNIME MassCascade feature. "KNIME is a user-friendly and comprehensive
open-source data integration, processing, analysis, and exploration platform." (http://knime.org/)

For the <a href="http://sbeisken.github.com/MassCascade/javadoc/index.html">API</a> see the JavaDoc pages.

Changelog
-------
1.0.0
* Initial release.
1.1.0
* Various updates and bug fixes.