package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ShareItApp.class)
class ShareItTests {

	@Test
	void contextLoads() {
		log.info("Context loaded successfully");
		assertTrue(true);
	}

}
