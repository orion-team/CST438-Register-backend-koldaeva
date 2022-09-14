package com.cst438;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {StudentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
    public static final int TEST_STUDENT_ID = 22;
    public static final int TEST_STUDENT_ID_DNE = 33;
    public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
    public static final String TEST_STUDENT_NAME = "test";
    public static final int TEST_STATUS_HOLD = 1;
    public static final String TEST_STATUS_HOLD_STR = "Student has a hold";

    @MockBean
    StudentRepository studentRepository;

    @Autowired
    private MockMvc mvc;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            System.out.println("STRING " + str);
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addStudent() throws Exception {
        MockHttpServletResponse response;

        Student student = new Student();
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setName(TEST_STUDENT_NAME);
        student.setStatusCode(0);
        student.setStudent_id(TEST_STUDENT_ID);

        when(studentRepository.save(any())).thenReturn(student);

        // create the DTO (data transfer object) for the student to add.
        StudentDTO studentDTO = new StudentDTO();

        // do http request with missing data
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/student")
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // verify that return status = 400
        assertEquals(400, response.getStatus());

        // add missing data
        studentDTO.studentEmail = TEST_STUDENT_EMAIL;
        studentDTO.studentName = TEST_STUDENT_NAME;

        // do http post request with body of studentDTO as JSON
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/student")
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // verify that return status = OK (value 200)
        assertEquals(200, response.getStatus());

        // verify that returned data has non zero primary key
        Student result = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(TEST_STUDENT_ID, result.getStudent_id());

        // verify that resulting student equals to expected
        assertTrue(student.equals(result));

        // verify that repository save method was called.
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    public void updateStudent() throws Exception {
        MockHttpServletResponse response;

        Student studentInitial = new Student();
        studentInitial.setEmail(TEST_STUDENT_EMAIL);
        studentInitial.setName(TEST_STUDENT_NAME);
        studentInitial.setStatusCode(0);
        studentInitial.setStatus(null);
        studentInitial.setStudent_id(TEST_STUDENT_ID);

        Student student = new Student();
        student.setEmail(TEST_STUDENT_EMAIL);
        student.setName(TEST_STUDENT_NAME);
        student.setStatusCode(TEST_STATUS_HOLD);
        student.setStatus(TEST_STATUS_HOLD_STR);
        student.setStudent_id(TEST_STUDENT_ID);

        when(studentRepository.save(any())).thenReturn(student);
        given(studentRepository.findById(TEST_STUDENT_ID_DNE)).willReturn(Optional.empty());
        given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.of(studentInitial));

        // create the DTO (data transfer object) for the student to add.
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.status = TEST_STATUS_HOLD_STR;
        studentDTO.statusCode = TEST_STATUS_HOLD;

        // make a request with a student that does not exist
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STUDENT_ID_DNE)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // verify that return status = 400
        assertEquals(400, response.getStatus());

        // make a request to update a student with status
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STUDENT_ID)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // verify that return status = 200
        assertEquals(200, response.getStatus());

        Student result = fromJsonString(response.getContentAsString(), Student.class);
        // verify that resulting student equals to expected
        assertTrue(student.equals(result));

        // verify that repository save method was called.
        verify(studentRepository).save(any(Student.class));

        // mock service when resetting the hold
        student.setStatusCode(0);
        student.setStatus(null);
        when(studentRepository.save(any())).thenReturn(student);

        studentDTO.status = null;
        studentDTO.statusCode = 0;

        // make a request to reset a student hold
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/student/" + TEST_STUDENT_ID)
                                .content(asJsonString(studentDTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // verify that return status = 200
        assertEquals(200, response.getStatus());
        result = fromJsonString(response.getContentAsString(), Student.class);
        // verify that resulting student equals to expected
        assertTrue(student.equals(result));
    }
}
