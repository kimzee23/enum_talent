package org.example.enumtalentapi.repository;

import org.example.enumtalentapi.entity.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByIsActiveTrue();
    List<Job> findByIsActiveTrueAndLocationContainingIgnoreCase(String location);
    List<Job> findByIsActiveTrueAndWorkMode(String workMode);
    List<Job> findByIsActiveTrueAndEmploymentType(String employmentType);

    @Query("{'isActive': true, 'requiredSkills': { $in: ?0 }}")
    List<Job> findBySkills(List<String> skills);

    @Query("{'isActive': true, '$or': [{'title': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}, {'company': {$regex: ?0, $options: 'i'}}]}")
    List<Job> searchJobs(String searchTerm);

    List<Job> findByPostedBy(String postedBy);
}