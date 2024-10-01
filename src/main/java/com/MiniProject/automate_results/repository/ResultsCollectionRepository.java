package com.MiniProject.automate_results.repository;

import com.MiniProject.automate_results.entity.results.SubjectResultSheet;
import com.MiniProject.automate_results.entity.resultsCollection.ResultsCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ResultsCollectionRepository extends MongoRepository<ResultsCollection,String> {

    // Custom query to check if a result sheet exists with the given parameters (ignoring case sensitivity)
    @Query("{ 'university' : { $regex: ?0, $options: 'i' }, "
            + "'faculty' : { $regex: ?1, $options: 'i' }, "
            + "'department' : { $regex: ?2, $options: 'i' }, "
            + "'degreeProgram' : { $regex: ?3, $options: 'i' }, "
            + "'semester' : { $regex: ?4, $options: 'i' }, "
            + "'batch' : { $regex: ?5, $options: 'i' } }")
    Optional<ResultsCollection> findByParameters(String university, String faculty, String department,
                                                  String degreeProgram, String semester, String batch);

}
