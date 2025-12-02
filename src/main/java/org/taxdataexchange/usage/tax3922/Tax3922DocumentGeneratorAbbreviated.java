package org.taxdataexchange.usage.tax3922;

import irs.fdxtax641.jsonserializers.Tax3922Serializer;
import irs.fdxtax641.map2objects.Map2Tax3922;
import org.apache.commons.lang3.time.StopWatch;
import org.openapitools.client.model.Tax3922;
import org.taxdataexchange.core.csv.GenericCsvMapReader;
import org.taxdataexchange.core.pdfbuilders.Tax3922PdfBuilder;
import org.taxdataexchange.core.utils.FileUtils;
import org.taxdataexchange.core.utils.Pdf2PngConverter;
import org.taxdataexchange.core.utils.PdfWatermarker;
import returnagnosticutils.BytesToFile;
import returnagnosticutils.Jsonizer;
import returnagnosticutils.StringToFile;

import java.util.List;
import java.util.Map;

// Read CSV file for one company and generate 1099-MISC PDFs

public class Tax3922DocumentGeneratorAbbreviated {

    public static final String INPUT_DIRECTORY = "input";

    public static final String OUTPUT_DIRECTORY = "output";

    private void processOneObject(
        int rowNumber,
        String outputDir,
        Tax3922 taxObject
    ) {

        // Generate PDFs
        Tax3922PdfBuilder pdfBuilder = new Tax3922PdfBuilder( );
        StringBuilder stringBuilder = new StringBuilder();
        StopWatch stopWatch = new StopWatch( );
        stopWatch.start( );
        pdfBuilder.buildBasicPdf( stringBuilder, stopWatch, taxObject );
        {
            // Issuer copy
            String fileName = String.format( "%06d.issuer.pdf", rowNumber );
            byte[] pdfBytes = pdfBuilder.getIssuerPdfBytes( );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );
        }

        {
            // Print and mail copy
            byte[] pdfBytes = pdfBuilder.getPrintPdfBytes1( );
            pdfBytes = PdfWatermarker.addWatermarkToPdf( pdfBytes, "Sample" );
            String fileName = String.format( "%06d.print.pdf", rowNumber );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );

            byte[] pngBytes = Pdf2PngConverter.convertBytes( pdfBytes );
            String pngFileName = String.format( "%06d.print.png", rowNumber );
            BytesToFile.writeToDirFile(
                pngBytes,
                outputDir,
                pngFileName
            );
        }

        {
            // Email or download copy
            byte[] pdfBytes = pdfBuilder.getDownloadPdfBytes1( );
            pdfBytes = PdfWatermarker.addWatermarkToPdf( pdfBytes, "Sample" );
            String fileName = String.format( "%06d.download.pdf", rowNumber );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );

            byte[] pngBytes = Pdf2PngConverter.convertBytes( pdfBytes );
            String pngFileName = String.format( "%06d.download.png", rowNumber );
            BytesToFile.writeToDirFile(
                pngBytes,
                outputDir,
                pngFileName
            );

        }

    }

    private void processOneRow(
        String companyName,
        int rowNumber,
        Map<String, String> row
    ) {

        String outputDir = String.format( "%s/%s/%06d", OUTPUT_DIRECTORY, companyName, rowNumber );

        // Save input
        {
            String asJson = Jsonizer.toJson( row );
            String outputFile = String.format( "%06d.map.json", rowNumber );
            StringToFile.writeToDirFile( asJson, outputDir, outputFile );
        }

        // Convert to object and save
        Tax3922 obj = Map2Tax3922.generate( row );
        {
            String asJson = Tax3922Serializer.serialize( obj );
            String outputFile = String.format( "%06d.obj.json", rowNumber );
            StringToFile.writeToDirFile( asJson, outputDir, outputFile );
        }

        this.processOneObject( rowNumber, outputDir,obj );

    }

    private void processCsvForCompany(
        String companyName
    ) {


        // CSV file
        String csvFileName  = "Tax3922.csv";

        // CSV dir
        String csvDirName = String.format( "%s/%s", INPUT_DIRECTORY, companyName );


        // Save
//        String outputDirName = String.format( "%s/%s", OUTPUT_DIRECTORY, companyName );
//        String outputFileName = "Tax3922.rows.json";
//        String asJson = Jsonizer.toJson( rows );
//        StringToFile.writeToDirFile( asJson, outputDirName, outputFileName );


    }

    public static void main(String[] args) {

//        System.out.println( "Tax3922DocumentGenerator Begin" );
//
//        String companyName = "company1";
//
//        StringBuilder stringBuilder = new StringBuilder();
//        StopWatch stopWatch = new StopWatch( );
//        stopWatch.start( );
//
//        String csvContent = "";
//
//        // Read CSV content
//        csvContent = FileUtils.readDirFile(
//            csvDirName,
//            csvFileName
//        );
//
//        // Convert to list of maps
//        GenericCsvMapReader csvReader = new GenericCsvMapReader( );
//        List<Map<String, String>> rows = csvReader.readStringWithCsvMapReader(
//            csvContent
//        );
//
//        // Process row
//        Map<String, String> row = rows.get( 0 );
//
//        // Convert to object
//        Tax3922 taxObject = Map2Tax3922.generate( row );
//
//        // Generate PDF
//        Tax3922PdfBuilder pdfBuilder = new Tax3922PdfBuilder( );
//        pdfBuilder.buildBasicPdf( stringBuilder, stopWatch, taxObject );
//        byte[] pdfBytes = pdfBuilder.getPrintPdfBytes1( );
//
//        // Save
//        BytesToFile.writeToDirFile( pdfBytes,
//            outputDir,
//            fileName
//        );
//
//        System.out.println( "Tax3922DocumentGenerator Done" );

    }

}
