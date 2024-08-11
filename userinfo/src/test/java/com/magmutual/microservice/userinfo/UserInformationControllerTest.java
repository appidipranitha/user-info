package com.magmutual.microservice.userinfo;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.magmutual.microservice.userinfo.Util.DateUtils;
import com.magmutual.microservice.userinfo.controller.UserInformationController;
import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.model.UserInfoResponse;
import com.magmutual.microservice.userinfo.model.UserRequest;
import com.magmutual.microservice.userinfo.service.IUserInformationService;

/**
 * @author Pranitha
 *
 */

@WebMvcTest(UserInformationController.class)
public class UserInformationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IUserInformationService userInformationService;

	private User mockUser;
	private List<User> mockUsers;
	
	@BeforeEach
	public void setup() throws ParseException {
		MockitoAnnotations.openMocks(this);
		mockUsers = new ArrayList<>();

		User mockUser = new User();
		mockUser.setId(100);
		mockUser.setFirstName("Rock");
		mockUser.setLastName("Feller");
		mockUser.setEmail("Rock.Feller@gmail.com");
		mockUser.setProfession("doctor");
		mockUser.setDateCreated(DateUtils.convertStringToDate("2022-07-18"));
		mockUser.setCountry("Greenland");
		mockUser.setCity("Banjul");
		mockUsers.add(mockUser);

		mockUser = new User();
		mockUser.setId(101);
		mockUser.setFirstName("Amalie");
		mockUser.setLastName("Emerson");
		mockUser.setEmail("Amalie.Emerson@gmail.com");
		mockUser.setProfession("doctor");
		mockUser.setDateCreated(DateUtils.convertStringToDate("2022-04-26"));
		mockUser.setCountry("SwitzerLand");
		mockUser.setCity("Zermatt");

		mockUsers.add(mockUser);
	}

	@Test
	void testGetSpecificUser() throws Exception {
		List<User> users = new ArrayList<User>();
		users.add(mockUser);

		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(users);

		when(userInformationService.getSpecificUserByID(anyInt())).thenReturn(response);

		mockMvc.perform(get("/v1/api/users/101").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.users", hasSize(1)));
	}

	@Test
	void testGetSpecificUserNotFound() throws Exception {
		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(new ArrayList<User>());

		when(userInformationService.getSpecificUserByID(anyInt())).thenReturn(response);

		mockMvc.perform(get("/v1/api/users/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No Users found matching the given id"));
	}

	@Test
	void testGetUsersByDateRange() throws Exception {
		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(mockUsers);

		when(userInformationService.getUsersByDateRange(any(Date.class), any(Date.class))).thenReturn(response);

		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "2023-01-01")
				.param("endDate", "2023-12-31")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.users", hasSize(2)));
	}

	@Test
	void testGetUsersByDateRangeEmptyDatesBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "")
				.param("endDate", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("startDate  and endDate cannot be empty"));
	}

	@Test
	void testGetUsersByDateRangeInvalidDatesBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "10-20-2020")
				.param("endDate", "11-11-2022")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("startDate and endDate are in invalid Format. Please enter valid startDate and endDate in 'yyyy-MM-dd' format"));
	}

	@Test
	void testGetUsersByDateRangeEmptyStartDateBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "")
				.param("endDate", "2022-10-20")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("startDate cannot be empty"));
	}

	@Test
	void testGetUsersByDateRangeStartDateInvalidBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "10-20-2020")
				.param("endDate", "2022-10-20")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("startDate is in invalid Format. Please enter valid start date in 'yyyy-MM-dd' format"));
	}

	@Test
	void testGetUsersByDateRangeEmptyEndDateBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "2022-10-20")
				.param("endDate", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("endDate cannot be empty"));
	}

	@Test
	void testGetUsersByDateRangeEndDateInvalidBadRequest() throws Exception {
		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "2022-10-20")
				.param("endDate", "20-20-2020")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("endDate is in invalid Format.Please enter valid endDate in 'yyyy-MM-dd' format"));
	}

	@Test
	void testGetUsersByDateRangeUserNotFound() throws Exception {
		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(new ArrayList<User>());
		when(userInformationService.getUsersByDateRange(any(Date.class), any(Date.class))).thenReturn(response);

		mockMvc.perform(get("/v1/api/usersByDateRange")
				.param("startDate", "2020-10-20")
				.param("endDate", "2022-10-20")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("No Users found matching the given date range"));
	}

	@Test
	void testGetUsersByProfession() throws Exception {
		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(mockUsers);

		when(userInformationService.getUsersByProfession(anyString())).thenReturn(response);

		mockMvc.perform(
				get("/v1/api/usersByProfession").param("profession", "doctor")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.users", hasSize(2)));
	}

	@Test
	void testGetUsersByProfessionBadRequest() throws Exception {
		mockMvc.perform(
				get("/v1/api/usersByProfession").param("profession", "").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("profession cannot be blank. Please enter a valid profession value"));
	}

	@Test
	void testGetUsersByProfessionUserNotFound() throws Exception {
		UserInfoResponse response = new UserInfoResponse();
		response.setUsers(new ArrayList<User>());

		when(userInformationService.getUsersByProfession(anyString())).thenReturn(response);

		mockMvc.perform(
				get("/v1/api/usersByProfession").param("profession", "doctor")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message")
				.value("No Users found matching the given Profession"));
	}
	
	@Test
    public void testCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("Abraham");
        userRequest.setLastName("Lincoln");
        userRequest.setEmail("Abraham.Lincoln@example.com");
        userRequest.setProfession("Developer");
        userRequest.setDateCreated(DateUtils.convertStringToDate("2024-01-01"));
        userRequest.setCountry("USA");
        userRequest.setCity("NewYork");

        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
                .andExpect(status().isOk());

        // Verify interactions with the service layer
        verify(userInformationService).createUser(any(User.class));
    }

    @Test
    public void testCreateUserEmptyFirstName() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
        		.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("firstName cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyLastName() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("lastName cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyEmail() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("email cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyProfession() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("profession cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyDate() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"\",\"country\":\"USA\",\"city\":\"NewYork\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("dateCreated cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyCountry() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"\",\"city\":\"NewYork\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("country cannot be empty"));
    }
    
    @Test
    public void testCreateUserEmptyCity() throws Exception {
        mockMvc.perform(post("/v1/api/createUser")
                .contentType("application/json")
                .content("{\"firstName\":\"Abraham\",\"lastName\":\"Lincoln\",\"email\":\"Abraham.Lincoln@example.com\",\"profession\":\"Developer\",\"dateCreated\":\"2024-01-01\",\"country\":\"USA\",\"city\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
        				.value("city cannot be empty"));
    }
}

