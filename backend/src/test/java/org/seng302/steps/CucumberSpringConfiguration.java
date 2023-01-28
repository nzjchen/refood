package org.seng302.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.seng302.Main;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@CucumberContextConfiguration
@PropertySource("classpath:application.properties")
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CucumberSpringConfiguration {

}