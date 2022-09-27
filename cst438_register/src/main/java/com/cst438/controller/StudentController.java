package com.cst438.controller;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/student")
    @Transactional
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    public Student addStudent (@RequestBody StudentDTO studentDTO) {
        // As an administrator, I can add a student to the system.
        Student existingStudent = studentRepository.findByEmail(studentDTO.studentEmail);

        if (existingStudent != null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student with provided email already exists.");
        } else if (studentDTO.studentName == null || studentDTO.studentEmail == null) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student name and email are required for registration.");
        } else {
            Student student = new Student();
            student.setName(studentDTO.studentName);
            student.setEmail(studentDTO.studentEmail);
            student.setStatusCode(studentDTO.statusCode);
            student.setStatus(studentDTO.status);
            return studentRepository.save(student);
        }
    }

    @PutMapping(value = "/student/{student_id}")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    @Transactional
    public Student updateStudent(@PathVariable("student_id") int id, @RequestBody StudentDTO studentDTO) {
        Optional<Student> existingStudent = studentRepository.findById(id);

        if (existingStudent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student with provided id does not exist.");
        } else {
            Student student = existingStudent.get();

            // As an administrator, I can put student registration on HOLD.
            // As an administrator, I can release the HOLD on student registration.
            // To release the hold - issue a put request with the statusCode == 0
            student.setStatusCode(studentDTO.statusCode);
            student.setStatus(studentDTO.status);
            return studentRepository.save(student);
        }
    }

    @GetMapping(value = "/student/{student_id}")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    public Student getStudent(@PathVariable("student_id") int id) {
        Optional<Student> existingStudent = studentRepository.findById(id);

        if (existingStudent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with provided id does not exist");
        } else {
            return existingStudent.get();
        }
    }

    @GetMapping(value = "/student")
    @CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        studentRepository.findAll().forEach(students::add);

        return students;
    }
}


