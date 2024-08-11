package com.magmutual.microservice.userinfo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.magmutual.microservice.userinfo.controller.UserInformationController;

/**
 * @author Pranitha
 *
 */

@SpringBootTest
class UserInfoApplicationTests {

	@Autowired
	private UserInformationController userInfoController;
	
	@Test
	void contextLoads() {
		assertThat(userInfoController).isNotNull();
	}
}
