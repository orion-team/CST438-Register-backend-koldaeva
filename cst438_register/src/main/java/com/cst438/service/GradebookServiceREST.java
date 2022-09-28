package com.cst438.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.EnrollmentDTO;


public class GradebookServiceREST extends GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	String gradebook_url;
	
	public GradebookServiceREST() {
		System.out.println("REST grade book service");
	}

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
		enrollmentDTO.studentEmail = student_email;
		enrollmentDTO.studentName = student_name;
		enrollmentDTO.course_id = course_id;

		System.out.println("Post to the gradebook service: " + enrollmentDTO);
		EnrollmentDTO response = restTemplate.postForObject(gradebook_url + "/enrollment", enrollmentDTO, EnrollmentDTO.class);
		System.out.println("Response: " + response);
	}

}
