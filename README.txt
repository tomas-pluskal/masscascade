MassCascade: Mass Spectrometry Library
-----
Contributors: Stephan Beisken
Tags: mass spectrometry, tandem mass spectrometry, analytical data processing
License: GPLv3 or later
License URI: http://www.gnu.org/licenses/gpl-3.0.html

The MassCascade library provides diverse functionality for tandem mass spectrometry data processing.
Currently, the library is integrated in in the workflow tool KNIME as community feature.
The library has primarily been tested with metabolomics LC-MS data.

Description
-------
The following input formats are supported:
* Thermo RAW file format
* PSI mzML file format
* CML file format ('MSnMetabolomics' convention)

Functionality includes:
* input (mzML, cml, raw) / output (cml)
* scan-based sample to reference alignment using fast dynamic time warping
* background subtraction and noise reduction
* data binning in the m/z and time domain
* sample / profile filters in the m/z and time domain
* profile-based gap filling using natural cubic splines
* charge-based isotope and list-based adduct detection
* mass-trace extraction and manipulation
* data smoothing in the m/z and time domain
* visualisation of spectra and chromatograms

The two core categories are 'raw-' and 'profile-files'. The 'raw-files' contain raw data and the 'profile-files'
extracted mass traces and annotated peaks. Both core categories are always serialized to the set working directory to
reduce memory load and ensure persistence.

Installation
-------
The library can be freely used as backend for mass spectrometry data processing. All task classes implement the
'Callable<?>' interface and can be configured via the 'setParameters()' method or constructed fully parameterized.

Currently, the library is used in the KNIME MassCascade feature. "KNIME is a user-friendly and comprehensive
open-source data integration, processing, analysis, and exploration platform." (http://knime.org/)

For the <a href="http://sbeisken.github.com/MassCascade/javadoc/index.html">API</a> see the JavaDoc pages.

Changelog
-------
1.0.0
* Initial release.