package com.cst438.controller;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CourseController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    /*
     * endpoint used by gradebook service to transfer final course grades
     */
    @PutMapping("/course/{course_id}")
    @Transactional
    public void updateCourseGrades(@RequestBody CourseDTOG courseDTO, @PathVariable("course_id") int course_id) {
        System.out.println("Receive course DTO data :" + courseDTO);

        // update student grades
        for (CourseDTOG.GradeDTO gradeDTO : courseDTO.grades) {
            Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(gradeDTO.student_email, course_id);

            if (enrollment == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No enrollment found for student with provided email and course id");
            }
            enrollment.setCourseGrade(gradeDTO.grade);
            enrollmentRepository.save(enrollment);
            System.out.println("Final grade updated: " + gradeDTO.student_email + " " + course_id + " " + gradeDTO.grade);
        }
    }

}
