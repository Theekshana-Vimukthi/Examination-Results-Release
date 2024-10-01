package com.MiniProject.automate_results.service.resultsCollection;

import com.MiniProject.automate_results.entity.resultsCollection.ResultsCollection;
import com.MiniProject.automate_results.service.exception.RecordNotFoundException;

import java.util.List;
import java.util.Map;

public interface ResultsCollectionService {
    public void saveResultsWithPdf(String id) throws Exception;
}
