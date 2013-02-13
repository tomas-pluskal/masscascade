/*
 * Copyright 2006-2011 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * ------------------------------------------------------------------------------
 *
 * This program binds to the XRawfile2.dll provided by the MSFileReader library 
 * (http://sjsupport.thermofinnigan.com/public/detail.asp?id=703) and dumps the 
 * contents of a given RAW file as text data. The code is partly based on ReAdW 
 * program (GPL). To compile this source, you can use Microsoft Visual C++ 
 * command line compiler:
 * 
 * 1) setup the compiler environment by running 'vcvars32.bat' in the Visual C++
 *    bin directory 
 *
 * 2) build RAWdump.exe by running 'cl.exe RAWdump.cpp'  
 *
 */
 
#include <comutil.h>
#include <stdio.h>
#include <io.h>
#include <fcntl.h>

// MSFileReader must be installed for this program to compile
#import "XRawfile2.dll"

typedef struct _datapeak
{
    double dMass;
    double dIntensity;
} DataPeak;

int main(int argc, char* argv[]) {

    // Disable output buffering and set output to binary mode
    setvbuf(stdout, 0, _IONBF, 0);
    _setmode(fileno(stdout), _O_BINARY);

	// Check the filename argument    
    if (argc != 2) {
        fprintf(stdout, "ERROR: This program accepts exactly 1 argument: a RAW file path\n");
        return 1;
    }

	// Initialize COM interface
    HRESULT hr = CoInitializeEx(NULL, COINIT_MULTITHREADED);    
    if (FAILED(hr)) {
        fprintf(stdout, "ERROR: Unable to initialize COM\n");
        return 1;
    }
    
    // Make an instance from XRawfile class defined in XRawfile2.dll.
    MSFileReaderLib::IXRawfilePtr rawFile(NULL);
    hr = rawFile.CreateInstance("MSFileReader.XRawfile.1");
    if (FAILED(hr)) {
        fprintf(stdout, "ERROR: Unable to initialize RAW file object, have you installed MSFileReader?\n");
        return 1;
    }

    // Get the filename parameter
    char *filename = argv[1];

	// Test whether the file exists, because the rawFile->Open() 
	// function does not check that
	FILE *fp = fopen(filename, "r");
	if (! fp) {
        fprintf(stdout, "ERROR: Unable to read file %s\n", filename);
        return 1;
	}
	fclose(fp);
	
    // Open the Thermo RAW file
    hr = rawFile->Open(filename);
    if (FAILED(hr)) {
        fprintf(stdout, "ERROR: Unable to open RAW file %s\n", filename);
        return 1;
    }

    // Look for data that belong to the first mass spectra device in the file
    rawFile->SetCurrentController(0, 1);
    
    long firstScanNumber = 0, lastScanNumber = 0;

    // Verifies if can get the first scan
    hr = rawFile->GetFirstSpectrumNumber(&firstScanNumber);
    if (FAILED(hr)) {
        fprintf(stdout, "ERROR: Unable to get first scan\n");
        return 1;
    }

	BSTR sampleId = NULL;
	long nSi = rawFile->GetSeqRowSampleID(&sampleId);
	if (nSi != 0) {
		fprintf(stdout, "ERROR: Unable to get sample id\n");
	} else {
		char *sampleIdChar = _com_util::ConvertBSTRToString(sampleId);
		fprintf(stdout, "SAMPLE ID: %s\n", sampleIdChar);
	}
	SysFreeString(sampleId);

    // Ask for the last scan number to prepare memory space, for cycle 
    // and final verification
    rawFile->GetLastSpectrumNumber(&lastScanNumber);
    long totalNumScans = (lastScanNumber - firstScanNumber) + 1;

    fprintf(stdout, "NUMBER OF SCANS: %ld\n", totalNumScans);

    // Prepare a wide character string to read the filter line
    BSTR bstrFilter;

    // Read totalnumber of scans
    for (long curScanNum = firstScanNumber; curScanNum <= lastScanNumber; curScanNum++) {

        bstrFilter = NULL;
        rawFile->GetFilterForScanNum(curScanNum, &bstrFilter);

        if (bstrFilter == NULL){
            fprintf(stdout, "ERROR: Could not extract scan filter line for scan #%d\n", curScanNum);
            return 1;
        }

        char *thermoFilterLine = _com_util::ConvertBSTRToString(bstrFilter);

        fprintf(stdout, "SCAN NUMBER: %ld\n", curScanNum);
        fprintf(stdout, "SCAN FILTER: %s\n", thermoFilterLine);
    
        // Cleanup memory
        SysFreeString(bstrFilter);
        delete[] thermoFilterLine;

        long numDataPoints = -1; // points in both the m/z and intensity arrays
        double retentionTimeInMinutes = -1;
        double minObservedMZ_ = -1;
        double maxObservedMZ_ = -1;
        double totalIonCurrent_ = -1;
        double basePeakMZ_ = -1;
        double basePeakIntensity_ = -1;
        long channel; // unused
        long uniformTime; // unused
        double frequency; // unused
        double precursorMz = 0;
        long precursorCharge = 0;

        rawFile->GetScanHeaderInfoForScanNum(
            curScanNum, 
            &numDataPoints, 
            &retentionTimeInMinutes, 
            &minObservedMZ_,
            &maxObservedMZ_,
            &totalIonCurrent_,
            &basePeakMZ_,
            &basePeakIntensity_,
            &channel, // unused
            &uniformTime, // unused
            &frequency // unused
        );
        
        DATE creationDate = NULL;
		rawFile->GetCreationDate(&creationDate);
        fprintf(stdout, "CREATION DATE: %f\n", creationDate);
        fprintf(stdout, "RETENTION TIME: %f\n", retentionTimeInMinutes);
        fprintf(stdout, "TOTAL ION CURRENT: %f\n", totalIonCurrent_);
        fprintf(stdout, "BASE PEAK MZ: %f\n", basePeakMZ_);
		fprintf(stdout, "BASE PEAK INT: %f\n", basePeakIntensity_);

        // Check if the scan is MS/MS scan
        if (strstr(thermoFilterLine, "ms ") == NULL) {

                // precursorMz
                VARIANT varValue;
                VariantInit(&varValue);
                rawFile->GetTrailerExtraValueForScanNum(curScanNum, "Monoisotopic M/Z:" , &varValue);

                if( varValue.vt == VT_R4 ){ 
                    precursorMz = (double) varValue.fltVal;
                }else if( varValue.vt == VT_R8 ) {
                    precursorMz = varValue.dblVal;
                }else if ( varValue.vt != VT_ERROR ) {
                    precursorMz = 0;
                }
                
                // precursorCharge
                VariantClear(&varValue);
                rawFile->GetTrailerExtraValueForScanNum(curScanNum, "Charge State:" , &varValue);

                if( varValue.vt == VT_I2 ) 
                    precursorCharge = varValue.iVal;

                VariantClear(&varValue);
                fprintf(stdout, "PRECURSOR: %f %d\n", precursorMz, precursorCharge);
        
        }

        VARIANT varMassList;
        // initiallize variant to VT_EMPTY
        VariantInit(&varMassList);

        VARIANT varPeakFlags; // unused
        // initiallize variant to VT_EMPTY
        VariantInit(&varPeakFlags);

        // set up the parameters to read the scan
        long dataPoints = 0;
        long scanNum = curScanNum;
        LPCTSTR szFilter = NULL;        // No filter
        long intensityCutoffType = 0;        // No cutoff
        long intensityCutoffValue = 0;    // No cutoff
        long maxNumberOfPeaks = 0;        // 0 : return all data peaks
        double centroidPeakWidth = 0;        // No centroiding
        bool centroidThisScan = true;

        rawFile->GetMassListFromScanNum(
            &scanNum,
            szFilter,             // filter
            intensityCutoffType, // intensityCutoffType
            intensityCutoffValue, // intensityCutoffValue
            maxNumberOfPeaks,     // maxNumberOfPeaks
            centroidThisScan,        // centroid result?
            &centroidPeakWidth,    // centroidingPeakWidth
            &varMassList,        // massList
            &varPeakFlags,        // peakFlags
            &dataPoints);        // array size

        // Get a pointer to the SafeArray
        SAFEARRAY FAR* psa = varMassList.parray;
        DataPeak* pDataPeaks = NULL;
        SafeArrayAccessData(psa, (void**)(&pDataPeaks));
        
        // Print data points
        fprintf(stdout, "DATA POINTS: %d\n", dataPoints);
        
        // Dump the binary data
        fwrite(pDataPeaks, 16, dataPoints, stdout);

        // Cleanup
        SafeArrayUnaccessData(psa); // Release the data handle
        VariantClear(&varMassList); // Delete all memory associated with the variant
        VariantClear(&varPeakFlags); // and reinitialize to VT_EMPTY

        if( varMassList.vt != VT_EMPTY ) {
            SAFEARRAY FAR* psa = varMassList.parray;
            varMassList.parray = NULL;
            SafeArrayDestroy( psa ); // Delete the SafeArray
        }

        if(varPeakFlags.vt != VT_EMPTY ) {
            SAFEARRAY FAR* psa = varPeakFlags.parray;
            varPeakFlags.parray = NULL;
            SafeArrayDestroy( psa ); // Delete the SafeArray
        }
        
    }

    // Finalize link to XRawfile2.dll library
    hr = rawFile->Close();
    if (FAILED(hr)) {
        fprintf(stdout, "ERROR: Error trying to close the RAW file\n");
        return 1;
    }
    
    CoUninitialize();    
    
    return 0;
}
