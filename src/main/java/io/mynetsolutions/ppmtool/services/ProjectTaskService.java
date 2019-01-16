package io.mynetsolutions.ppmtool.services;

import io.mynetsolutions.ppmtool.domain.Backlog;
import io.mynetsolutions.ppmtool.domain.Project;
import io.mynetsolutions.ppmtool.domain.ProjectTask;
import io.mynetsolutions.ppmtool.exceptions.ProjectNotFoundException;
import io.mynetsolutions.ppmtool.repositories.BacklogRepository;
import io.mynetsolutions.ppmtool.repositories.ProjectTaskRepository;
import io.mynetsolutions.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService<service> {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String userName) {

        //all PTs to be added to a specific project, project != null, BL exists
        Backlog backlog = projectService.findByProjectIdentifier(projectIdentifier, userName).getBacklog();
        //set the BL to PT
        projectTask.setBacklog(backlog);
        // we want our project sequence to be like this: IDPRO-1 IDPRO-2  ...100 101
        Integer BacklogSequence = backlog.getPTSequence();

        // update the BL SEQUENCE
        BacklogSequence++;

        backlog.setPTSequence(BacklogSequence);

        //Add sequence to PT
        projectTask.setProjectSequence(backlog.getProjectIdentifier()+"-"+BacklogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        // INITIAL priority when priority null
        if(projectTask.getPriority() == null || projectTask.getPriority() == 0) { // in the future we need projectTask.getPriority() == 0 to handle the form
            projectTask.setPriority(3);
        }
        // INITIAL status when status is null
        if(projectTask.getStatus() == "" || projectTask.getStatus() == null) {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBacklogById(String id, String userName) {

       projectService.findByProjectIdentifier(id, userName);

       return projectTaskRepository.findProjectTaskByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username){

        //make sure we are searching on an existing backlog
        //projectService.findProjectByIdentifier(backlog_id, username);
        projectService.findByProjectIdentifier(backlog_id, username);

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findProjectTasksByProjectSequence(pt_id);
        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project: '"+backlog_id);
        }


        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

        //ProjectTask projectTask = projectTaskRepository.findProjectTasksByProjectSequence(pt_id);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

//        Backlog backlog = projectTask.getBacklog();
//        List<ProjectTask> pts = backlog.getProjectTasks();
//        pts.remove(projectTask);
//        backlogRepository.save(backlog);

        projectTaskRepository.delete(projectTask);
    }
}
