package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController

public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/student")
    @Transactional
    public Student addStudent (@RequestBody StudentDTO studentDTO) {
        Student existingStudent = studentRepository.findByEmail(studentDTO.studentEmail);

        if (existingStudent != null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student with provided email already exists.");
        } else if (studentDTO.studentName == null || studentDTO.studentEmail == null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student name and email are required for registration.");
        } else {
            Student student = new Student();
            student.setName(studentDTO.studentName);
            student.setEmail(studentDTO.studentEmail);
            return studentRepository.save(student);
        }
    }

    @PutMapping(value = "/student/{student_id}")
    @Transactional
    public Student updateStudentHold(@PathVariable("student_id") int id, @RequestBody StudentDTO studentDTO) {
        Optional<Student> existingStudent = studentRepository.findById(id);

        if (existingStudent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student with provided id does not exist.");
        } else {
            Student student = existingStudent.get();
            student.setStatusCode(studentDTO.statusCode);
            student.setStatus(studentDTO.status);
            return studentRepository.save(student);
        }
    }
}


