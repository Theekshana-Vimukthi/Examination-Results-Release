package com.MiniProject.automate_results.service.results;

import com.MiniProject.automate_results.dto.Roles;
import com.MiniProject.automate_results.dto.resluts.DepSecretarySubjectResultSheetDto;
import com.MiniProject.automate_results.dto.resluts.LectureSubjectResultSheetDto;
import com.MiniProject.automate_results.entity.results.SubjectResultSheet;
import com.MiniProject.automate_results.service.exception.DuplicateRecordException;
import com.MiniProject.automate_results.service.exception.RecordNotFoundException;
import com.MiniProject.automate_results.user.Role;

import java.util.List;

public interface ManageResultsService {
    public SubjectResultSheet createAndUpdateResults(String id, LectureSubjectResultSheetDto lectureSubjectResultSheetDto) throws RecordNotFoundException, DuplicateRecordException;
    public SubjectResultSheet UpdateStudentsResultsByDepSecretary(String sheetId, DepSecretarySubjectResultSheetDto depSecretarySubjectResultSheetDto) throws RecordNotFoundException;

    List<SubjectResultSheet> getPendingSheet(String id) throws RecordNotFoundException;

    List<SubjectResultSheet> getApprovedSheet(Roles person,String id) throws RecordNotFoundException;

    SubjectResultSheet addApproval(String personId, String sheetId, Roles person) throws RecordNotFoundException;

    SubjectResultSheet saveResultSheetAndCollection(SubjectResultSheet subjectResultSheet, String personId) throws RecordNotFoundException;

    List<SubjectResultSheet> getAllApprovedSheets(Roles role, String id) throws RecordNotFoundException;
}
