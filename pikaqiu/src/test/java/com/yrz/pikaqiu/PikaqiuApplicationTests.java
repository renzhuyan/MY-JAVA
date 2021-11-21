package com.yrz.pikaqiu;

import com.yrz.pikaqiu.service.PikaqiuTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PikaqiuApplicationTests {

	@Autowired
	private PikaqiuTestService pikaqiuTestService;

	@Test
	void test() {
		pikaqiuTestService.insert();
	}

}
