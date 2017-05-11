package cz.muni.fi.pb138.backend;

import cz.muni.fi.pb138.exceptions.ConverterException;
import cz.muni.fi.pb138.exceptions.InvalidInputFormatException;

public interface ODSToXMLConverter {

    /**
     * Converts ODS input file to XML format.
     * The xml file is created automatically, when such file exists it is replaced.
     *
     * @param odsFileName filename to import
     * @param xmlFileName filename to export
     * @throws IllegalArgumentException    when the input filename is null, does not exist or cannot be accessed
     * @throws IllegalArgumentException    when the output filename is null, or cannot be accessed
     * @throws InvalidInputFormatException when there was a problem parsing input file
     * @throws ConverterException          when there was a problem converting between files
     */
    void convertToXml(String odsFileName, String xmlFileName);

    /**
     * Converts XML input file to ODS format.
     * The ods file is created automatically, when such file exists it is replaced.
     *
     * @param odsFileName filename to import
     * @param xmlFileName filename to export
     * @throws IllegalArgumentException    when the input filename is null, does not exist or cannot be accessed
     * @throws IllegalArgumentException    when the output filename is null, or cannot be accessed
     * @throws InvalidInputFormatException when there was a problem parsing input file
     * @throws ConverterException          when there was a problem converting between files
     */
    void convertToODS(String xmlFileName, String odsFileName);
}
