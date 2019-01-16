package io.mynetsolutions.ppmtool.repositories;

import io.mynetsolutions.ppmtool.domain.ProjectTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {

    List<ProjectTask> findProjectTaskByProjectIdentifierOrderByPriority(String id);

    ProjectTask findProjectTasksByProjectSequence(String sequence);
}
