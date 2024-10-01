package com.MiniProject.automate_results.repository;

import com.MiniProject.automate_results.entity.results.SubjectResultSheet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResultSheetRepository extends MongoRepository<SubjectResultSheet, String> {
    // Custom query to find all SubjectResultSheets where depSecretaryStatus is APPROVED
    @Query("{ 'depSecretaryStatus' : 'APPROVED', 'depSecretaryID' : ?0 }")
    List<SubjectResultSheet> findByDepSecretaryStatusApprovedAndDepSecretaryId(String depSecretaryID);

    @Query("{ 'lecturerApprovalStatus' : 'APPROVED', 'lectureID' : ?0 }")
    List<SubjectResultSheet> findByLecturerStatusApprovedAndLecturerId(String lectureID);

    @Query("{ 'hodApprovalStatus' : 'APPROVED', 'hodID' : ?0 }")
    List<SubjectResultSheet> findByHodStatusApprovedAndHodId(String hodID);

    @Query("{ 'deanApproved' : 'APPROVED', 'deanID' : ?0 }")
    List<SubjectResultSheet> findByDeanStatusApprovedAndDeanId(String deanID);

    // Custom query to find all SubjectResultSheets where depSecretaryStatus is not APPROVED
    @Query("{ 'depSecretaryStatus' : { $ne: 'APPROVED' }, 'lectureID' : ?0 }")
    List<SubjectResultSheet> findByDepSecretaryStatusNotApprovedAndLecId(String lectureID);

    @Query("{ 'depSecretaryStatus' : { $ne: 'APPROVED' } }")
    List<SubjectResultSheet> findByDepSecretaryStatusNotApproved();

    @Query("{ 'depSecretaryStatus' : 'APPROVED', 'lecturerApprovalStatus' : { $ne: 'APPROVED' }, 'lectureID' : ?0 }")
    List<SubjectResultSheet> findByDepSecretaryStatusApprovedAndLectureNotApprovedAndLecId(String lectureID);


    @Query("{ 'lecturerApprovalStatus' : 'APPROVED', 'hodApprovalStatus' : { $ne: 'APPROVED' } }")
    List<SubjectResultSheet> findByLecturerApprovalStatusApprovedAndHodNotApproved();

    @Query("{ 'hodApprovalStatus' : 'APPROVED', 'deanApproved' : { $ne: 'APPROVED' } }")
    List<SubjectResultSheet> findByHodApprovalStatusApprovedAndDeanNotApproved();


    // Custom query to check if a result sheet exists with the given parameters (ignoring case sensitivity)
    @Query("{ 'university' : { $regex: ?0, $options: 'i' }, "
            + "'faculty' : { $regex: ?1, $options: 'i' }, "
            + "'department' : { $regex: ?2, $options: 'i' }, "
            + "'degreeProgram' : { $regex: ?3, $options: 'i' }, "
            + "'semester' : { $regex: ?4, $options: 'i' }, "
            + "'examinationHeldMonth' : { $regex: ?5, $options: 'i' }, "
            + "'subject' : { $regex: ?6, $options: 'i' }, "
            + "'subjectCode' : { $regex: ?7, $options: 'i' }, "
            + "'batch' : { $regex: ?8, $options: 'i' } }")
    Optional<SubjectResultSheet> findByParameters(String university, String faculty, String department,
                                                  String degreeProgram, String semester, String examinationHeldMonth,
                                                  String subject, String subjectCode, String batch);

}
