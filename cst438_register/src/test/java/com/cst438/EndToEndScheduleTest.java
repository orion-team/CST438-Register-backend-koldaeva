package com.cst438;

import com.cst438.domain.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * This example shows how to use selenium testing using the web driver
 * with Chrome browser.
 *
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *
 *    Make sure that TEST_COURSE_ID is a valid course for TEST_SEMESTER.
 *
 *    URL is the server on which Node.js is running.
 */

@SpringBootTest
public class EndToEndScheduleTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/kseniakoldaeva/chromedriver";

    public static final String URL = "http://localhost:3000";

    public static final String TEST_USER_EMAIL = "test@csumb.edu";

    public static final String TEST_USER_NAME = "Test Student";

    public static final String TEST_STATUS = "No Holds";

    public static final int TEST_STATUS_CODE = 0;

    public static final int TEST_COURSE_ID = 40443;

    public static final String TEST_SEMESTER = "2021 Fall";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    /*
     * When running in @SpringBootTest environment, database repositories can be used
     * with the actual database.
     */

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    /*
     * Student add course TEST_COURSE_ID to schedule for 2021 Fall semester.
     */

    @Test
    public void addCourseTest() throws Exception {


        /*
         * if student does not exist - create a student
         */
        Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
        if (student == null) {
            student = new Student();
            student.setEmail(TEST_USER_EMAIL);
            student.setName(TEST_USER_NAME);
            studentRepository.save(student);
        }

        /*
         * if student is already enrolled, then delete the enrollment.
         */

        Enrollment x = null;
        do {
            x = enrollmentRepository.findByEmailAndCourseId(TEST_USER_EMAIL, TEST_COURSE_ID);
            if (x != null)
                enrollmentRepository.delete(x);
        } while (x != null);

        // set the driver location and start driver
        //@formatter:off
        // browser	property name 				Java Driver Class
        // edge 	webdriver.edge.driver 		EdgeDriver
        // FireFox 	webdriver.firefox.driver 	FirefoxDriver
        // IE 		webdriver.ie.driver 		InternetExplorerDriver
        //@formatter:on

        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // select the last of the radio buttons on the list of semesters page.

            WebElement we = driver.findElement(By.xpath("(//input[@type='radio'])[last()]"));
            we.click();

            // Locate and click "Get Schedule" button

            driver.findElement(By.xpath("//a[@href='/schedule']")).click();
            Thread.sleep(SLEEP_DURATION);

            // Locate and click "Add Course" button which is the first and only button on the page.
            driver.findElement(By.xpath("//button")).click();
            Thread.sleep(SLEEP_DURATION);

            // enter course no and click Add button

            driver.findElement(By.xpath("//input[@name='course_id']")).sendKeys(Integer.toString(TEST_COURSE_ID));
            driver.findElement(By.xpath("//button[@id='Add']")).click();
            Thread.sleep(SLEEP_DURATION);

            /*
             * verify that new course shows in schedule.
             * get the title of all courses listed in schedule
             */

            Course course = courseRepository.findById(TEST_COURSE_ID).get();

            List<WebElement> elements = driver.findElements(By.xpath("//div[@data-field='title']/div[@class='MuiDataGrid-cellContent']"));
            boolean found = false;
            for (WebElement e : elements) {
                System.out.println(e.getText()); // for debug
                if (e.getText().equals(course.getTitle())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Course added but not listed in schedule.");

            // verify that enrollment row has been inserted to database.

            Enrollment e = enrollmentRepository.findByEmailAndCourseId(TEST_USER_EMAIL, TEST_COURSE_ID);
            assertNotNull(e, "Course enrollment not found in database.");

        } catch (Exception ex) {
            throw ex;
        } finally {

            // clean up database.

            Enrollment e = enrollmentRepository.findByEmailAndCourseId(TEST_USER_EMAIL, TEST_COURSE_ID);
            if (e != null)
                enrollmentRepository.delete(e);

            Student studentToDelete = studentRepository.findByEmail(TEST_USER_EMAIL);
            if (studentToDelete != null)
                studentRepository.delete(studentToDelete);

            driver.quit();
        }

    }


    @Test
    public void addStudentTest() throws Exception {


		/*
		 * if student is already enrolled, then delete the enrollment.
		 */
		Enrollment x = null;
		do {
			x = enrollmentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				enrollmentRepository.delete(x);
		} while (x != null);


		/*
		 * if student is already exists, then delete the student.
		 */
		Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
        if (student != null)
            studentRepository.delete(student);

        // set the driver location and start driver
        //@formatter:off
        // browser	property name 				Java Driver Class
        // edge 	webdriver.edge.driver 		EdgeDriver
        // FireFox 	webdriver.firefox.driver 	FirefoxDriver
        // IE 		webdriver.ie.driver 		InternetExplorerDriver
        //@formatter:on

        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // Locate and click "Add Student" button

            driver.findElement(By.xpath("//a[@href='student/add']")).click();
            Thread.sleep(SLEEP_DURATION);

            // Locate and click "Add Student" button which is the first and only button on the page.
            driver.findElement(By.xpath("//button")).click();
            Thread.sleep(SLEEP_DURATION);

            // enter student name, email, status code = 0 and status note - "No Holds"

            driver.findElement(By.xpath("//input[@name='studentName']")).sendKeys(TEST_USER_NAME);
            driver.findElement(By.xpath("//input[@name='studentEmail']")).sendKeys(TEST_USER_EMAIL);
            WebElement statusCodeElement = driver.findElement(By.xpath("//input[@name='statusCode']"));

            // there is a default value that is set to 0 - clear it
            statusCodeElement.sendKeys(Keys.BACK_SPACE);
            statusCodeElement.sendKeys(Integer.toString(TEST_STATUS_CODE));
            driver.findElement(By.xpath("//input[@name='status']")).sendKeys(TEST_STATUS);
            driver.findElement(By.xpath("//button[@id='Add']")).click();
            Thread.sleep(SLEEP_DURATION);

            Student studentCreated = studentRepository.findByEmail(TEST_USER_EMAIL);

            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='MuiDataGrid-cellContent']"));
            boolean found = false;
            for (WebElement e : elements) {
                System.out.println(e.getText()); // for debug
                if (e.getText().equals(studentCreated.getEmail())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Student created but not visible in the students list");

            // verify that student row has been inserted to database.
            assertNotNull(studentCreated, "Student has not been added to the database");

        } catch (Exception ex) {
            throw ex;
        } finally {

            // clean up database.

            Student studentToDelete = studentRepository.findByEmail(TEST_USER_EMAIL);
            if (studentToDelete != null)
                studentRepository.delete(studentToDelete);
            driver.quit();
        }

    }
}
