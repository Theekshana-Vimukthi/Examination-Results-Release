package com.MiniProject.automate_results.service.resultsCollection.impl;

import com.MiniProject.automate_results.entity.results.StudentResult;
import com.MiniProject.automate_results.entity.results.SubjectResultSheet;
import com.MiniProject.automate_results.entity.resultsCollection.ResultsCollection;
import com.MiniProject.automate_results.repository.ResultsCollectionRepository;
import com.MiniProject.automate_results.service.exception.RecordNotFoundException;
import com.MiniProject.automate_results.service.results.resultsImpl.ManageResultsServiceImpl;
import com.MiniProject.automate_results.service.resultsCollection.ResultsCollectionService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
public class ResultsCollectionServiceImpl implements ResultsCollectionService {

    private final ResultsCollectionRepository resultsCollectionRepository;
    private static final Logger logger = LoggerFactory.getLogger(ManageResultsServiceImpl.class);

    public ResultsCollectionServiceImpl(ResultsCollectionRepository resultsCollectionRepository) {
        this.resultsCollectionRepository = resultsCollectionRepository;
    }

    public Map<String, Map<String, String>> generateTable(ResultsCollection resultCollection) {
        Map<String, Map<String, String>> studentResultsTable = new HashMap<>();

        // Iterate over the subject result sheets to extract unique indexes
        for (SubjectResultSheet subjectResultSheet : resultCollection.getSubjectResultSheets()) {

            String subjectCode = subjectResultSheet.getSubjectCode();

            // Iterate over each student's result
            for (StudentResult studentResult : subjectResultSheet.getStudentResults()) {
                String studentIndex = studentResult.getIndex();
                String grade = studentResult.getGrade();

                // If the index doesn't exist in the map, create a new map for the subject results
                studentResultsTable.computeIfAbsent(studentIndex, k -> new HashMap<>());

                // Fill in the grade for the current subject
                studentResultsTable.get(studentIndex).put(subjectCode, grade);
            }
        }

        // Fill missing subjects with "N/A"
        for (String index : studentResultsTable.keySet()) {
            for (SubjectResultSheet subjectResultSheet : resultCollection.getSubjectResultSheets()) {
                String subjectCode = subjectResultSheet.getSubjectCode();
                studentResultsTable.get(index).putIfAbsent(subjectCode, "N/A");
            }
        }

        return studentResultsTable;
    }

    public Binary createPdf(Map<String, Map<String, String>> studentResultsTable, ResultsCollection resultCollection) throws Exception {
        // Create a byte array output stream to hold the PDF data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Initialize the PdfWriter with the byte array output stream
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document with custom margins for better space management
        Document document = new Document(pdf);
        document.setMargins(10, 10, 10, 10);  // Reduced margins

        // Add university details (centered, reduced font size)
        Paragraph title = new Paragraph(resultCollection.getUniversity()).setTextAlignment(TextAlignment.CENTER).setBold();
        title.setFontSize(10);
        document.add(title);

        document.add(new Paragraph(resultCollection.getFaculty()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph(resultCollection.getDepartment()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph(resultCollection.getDegreeProgram()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph("Batch: " + resultCollection.getBatch()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph("Semester: " + resultCollection.getSemester()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph("Examination Held in: " + resultCollection.getSubjectResultSheets().get(0).getExaminationHeldMonth()).setTextAlignment(TextAlignment.CENTER).setFontSize(8));

        // Add a small space before the table
        document.add(new Paragraph("\n"));

        // Number of columns = subjects + 1 (for the index column)
        int numColumns = studentResultsTable.values().iterator().next().size() + 1;
        Table table = new Table(numColumns);

        // Set table to fit the page width and reduce font size
        table.setWidth(UnitValue.createPercentValue(100)); // Table width set to 100% of the page
        table.setFontSize(8);  // Reduced font size for table content

        // Add headers (Index + Subject Codes)
        table.addCell(new Cell().add(new Paragraph("Index")).setFontSize(8));
        for (String subjectCode : studentResultsTable.values().iterator().next().keySet()) {
            table.addCell(new Cell().add(new Paragraph(subjectCode)).setFontSize(8));
        }

        // Add student results
        for (String index : studentResultsTable.keySet()) {
            table.addCell(new Cell().add(new Paragraph(index)).setFontSize(8));
            for (String grade : studentResultsTable.get(index).values()) {
                table.addCell(new Cell().add(new Paragraph(grade)).setFontSize(8));
            }
        }

        // Add table to document
        document.add(table);

        // Avoid excessive spacing and add signatures with reduced font
        document.add(new Paragraph("\n"));

        // Signature table
        Table signatureTable = new Table(4); // 4 columns for the 4 signatures
        signatureTable.setWidth(UnitValue.createPercentValue(100)); // Set table width to 100%

        signatureTable.addCell(new Cell().add(new Paragraph("Prepared By")).setFontSize(8));
        signatureTable.addCell(new Cell().add(new Paragraph("Checked By")).setFontSize(8));
        signatureTable.addCell(new Cell().add(new Paragraph("Senior Assistant Registrar/Examinations")).setFontSize(8));
        signatureTable.addCell(new Cell().add(new Paragraph("Vice Chancellor")).setFontSize(8));

        // Add empty cells for signatures
        for (int i = 0; i < 4; i++) {
            signatureTable.addCell(new Cell().add(new Paragraph("\n\n\n\n"))); // Space for signatures
        }

        // Add signature table to the document
        document.add(signatureTable);

        // Add space before subjects section and reduce spacing between lines
        document.add(new Paragraph("\n"));

        // Add each unique subject to the PDF document with reduced font size
        Set<String> subjectsNameAndCode = getUniqueSubjectDetails(resultCollection);
        for (String subject : subjectsNameAndCode) {
            document.add(new Paragraph(subject).setFontSize(8));
        }

        // Close the document
        document.close();

        // Convert the byte array output stream to a Binary object
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return new Binary(pdfBytes);
    }

    public void saveResultsWithPdf(String id) throws Exception {

        Optional<ResultsCollection> resultCollections = resultsCollectionRepository.findById(id);
        if(resultCollections.isPresent()){
            ResultsCollection resultsCollections = resultCollections.get();
        // Generate the result table
        Map <String, Map<String, String>> resultTable = generateTable(resultsCollections);

        // Generate the PDF and get it as a Binary object
        Binary pdfContent = createPdf(resultTable,resultsCollections);

        // Save the PDF content in the resultsCollection entity
        resultsCollections.setPdfContent(pdfContent);

        // Save the updated resultsCollection back to the database
        resultsCollectionRepository.save(resultsCollections);

        }else{
            throw new RecordNotFoundException("The collection is not found");
        }
    }

    public Set<String> getUniqueSubjectDetails(ResultsCollection resultsCollection) {
        // Use a Set to ensure uniqueness
        Set<String> uniqueSubjects = new HashSet<>();

        // Loop through the subjectResultSheets list
        for (SubjectResultSheet subjectResultSheet : resultsCollection.getSubjectResultSheets()) {
            String subjectCode = subjectResultSheet.getSubjectCode();
            String subject = subjectResultSheet.getSubject();

            // Concatenate subjectCode and subject
            String subjectDetail = subjectCode + " - " + subject;

            // Add the combination to the Set (ensures uniqueness)
            uniqueSubjects.add(subjectDetail);
        }

        return uniqueSubjects; // Return the set of unique subject details
    }
}
