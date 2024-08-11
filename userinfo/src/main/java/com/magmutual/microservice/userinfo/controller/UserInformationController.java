package com.magmutual.microservice.userinfo.controller;

import java.sql.Date;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.magmutual.microservice.userinfo.Util.DateUtils;
import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.model.UserInfoResponse;
import com.magmutual.microservice.userinfo.model.UserRequest;
import com.magmutual.microservice.userinfo.service.IUserInformationService;

import io.swagger.v3.oas.annotations.Operation;

/**
 * @author Pranitha
 *
 */

@RestController
@RequestMapping("/v1/api")
public class UserInformationController {

	@Autowired
	private IUserInformationService userInformationService;
	public static final Pattern dateRegexPattern = Pattern
			.compile("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))");

	/**
	 * @param id
	 * @return Returns the specific user based on the email.
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	@Operation(description = "API to return a specific user and associated data by id")
	public UserInfoResponse getSpecificUser(@PathVariable Integer id) {

		UserInfoResponse userInfoResponse = userInformationService.getSpecificUserByID(id);

		if (userInfoResponse.getUsers().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Users found matching the given id");
		}

		return userInfoResponse;

	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return Returns the users within a date range inclusive of the dates.
	 * @throws ParseException
	 */
	@RequestMapping(value = "/usersByDateRange", method = RequestMethod.GET)
	@Operation(description = "API to return a list of users created between a date range")
	public UserInfoResponse getUsersByDateRange(@RequestParam(value = "startDate", required = true) String startDateStr,
			@RequestParam(value = "endDate", required = true) String endDateStr) throws ParseException {

		checkValidDates(startDateStr, endDateStr);

		Date startDate = DateUtils.convertStringToDate(startDateStr);
		Date endDate = DateUtils.convertStringToDate(endDateStr);

		UserInfoResponse userInfoResponse = userInformationService.getUsersByDateRange(startDate, endDate);

		if (userInfoResponse.getUsers().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Users found matching the given date range");
		}

		return userInfoResponse;
	}

	/**
	 * Checks the date validations
	 * 
	 * @param startDateStr
	 * @param endDateStr
	 */
	private void checkValidDates(String startDateStr, String endDateStr) {
		boolean validStartDate = dateRegexPattern.matcher(startDateStr).find();
		boolean validEndDate = dateRegexPattern.matcher(endDateStr).find();

		if (StringUtils.isBlank(startDateStr) && StringUtils.isBlank(endDateStr)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate  and endDate cannot be empty");
		} else if (!validStartDate && !validEndDate) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"startDate and endDate are in invalid Format. Please enter valid startDate and endDate in 'yyyy-MM-dd' format");
		} else if (StringUtils.isBlank(startDateStr)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate cannot be empty");
		} else if (!validStartDate) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"startDate is in invalid Format. Please enter valid start date in 'yyyy-MM-dd' format");
		} else if (StringUtils.isBlank(endDateStr)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate cannot be empty");
		} else if (!validEndDate) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"endDate is in invalid Format.Please enter valid endDate in 'yyyy-MM-dd' format");
		}
	}

	/**
	 * @param profession
	 * @return Returns the list of users with the given profession.
	 */
	@GetMapping(value = "/usersByProfession")
	@Operation(description = "API to return a list of users based on a specific profession")
	public UserInfoResponse getUsersByProfession(
			@RequestParam(value = "profession", required = true) String profession) {

		if (StringUtils.isBlank(profession)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"profession cannot be blank. Please enter a valid profession value");
		}

		UserInfoResponse userInfoResponse = userInformationService.getUsersByProfession(profession);
		if (userInfoResponse.getUsers().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Users found matching the given Profession");
		}

		return userInfoResponse;
	}

	@PostMapping(value = "/createUser")
	@Operation(description = "API to create a User")
	public void createUser(@RequestBody UserRequest userRequest) {
		
		checkValidFields(userRequest);
	    
		User user = new User();
		Long nextUserId = userInformationService.getNextUserId();
        user.setId(nextUserId.intValue());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setEmail(userRequest.getEmail());
		user.setProfession(userRequest.getProfession());
		user.setDateCreated(userRequest.getDateCreated());
		user.setCountry(userRequest.getCountry());
		user.setCity(userRequest.getCity());
        userInformationService.createUser(user);
	}

	private void checkValidFields(UserRequest userRequest) {
		if (StringUtils.isBlank(userRequest.getFirstName())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "firstName cannot be empty");
	    }
	    if (StringUtils.isBlank(userRequest.getLastName())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lastName cannot be empty");
	    }
	    if (StringUtils.isBlank(userRequest.getEmail())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email cannot be empty");
	    }
	    if (StringUtils.isBlank(userRequest.getProfession())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "profession cannot be empty");
	    }
	    if (userRequest.getDateCreated() == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateCreated cannot be empty");
	    }
	    if (StringUtils.isBlank(userRequest.getCountry())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "country cannot be empty");
	    }
	    if (StringUtils.isBlank(userRequest.getCity())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "city cannot be empty");
	    }
	}

}
