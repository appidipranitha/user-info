package com.magmutual.microservice.userinfo.service;

import java.sql.Date;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.magmutual.microservice.userinfo.dao.UserInformationRepository;
import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.model.UserInfoResponse;

/**
 * @author Pranitha
 *
 */

@Service
public class UserinformationServiceImpl implements IUserInformationService {

	private static final Logger logger = LoggerFactory.getLogger(UserinformationServiceImpl.class);

	@Autowired
	private UserInformationRepository userInfoRepository;

	@Override
	public UserInfoResponse getSpecificUserByID(Integer id) {
		UserInfoResponse userInfoResponse = new UserInfoResponse();
		Optional<User> optionalUser = userInfoRepository.findById(id);

		List<User> specificUser = optionalUser.map(Collections::singletonList).orElse(Collections.emptyList());

		if (specificUser.isEmpty()) {
			logger.info("getSpecificUserByID() : No Matching users found");
		}

		userInfoResponse.setMetadata("Users Count: " + specificUser.size());
		userInfoResponse.setUsers(specificUser);
		return userInfoResponse;
	}

	@Override
	public UserInfoResponse getUsersByDateRange(Date startdate, Date endDate) throws ParseException {
		UserInfoResponse userInfoResponse = new UserInfoResponse();
		
		List<User> usersByDate = userInfoRepository.findByDateCreatedBetween(startdate, endDate);

		if (usersByDate.isEmpty()) {
			logger.info("getUsersByDateRange() : No Users found matching the given date range");
		}

		userInfoResponse.setMetadata("Users Count: " + usersByDate.size());
		userInfoResponse.setUsers(usersByDate);
		return userInfoResponse;
	}

	@Override
	public UserInfoResponse getUsersByProfession(String profession) {
		UserInfoResponse userInfoResponse = new UserInfoResponse();
		List<User> usersByProfession = userInfoRepository.findByProfession(profession);

		if (usersByProfession.isEmpty()) {
			logger.info("getUsersByProfession() : No users found matching the given Profession");
		}

		userInfoResponse.setMetadata("Users Count: " + usersByProfession.size());
		userInfoResponse.setUsers(usersByProfession);
		return userInfoResponse;
	}

	@Override
	public void createUser(User user) {
		UserInfoResponse userInfoResponse = getSpecificUserByID(user.getId());
		
		if(userInfoResponse.getUsers().isEmpty()) {
			userInfoRepository.save(user);
			logger.info("createUser() : created new User with id: " +user.getId());
		}
		else {
			logger.info("createUser() : User already exists with this id");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User already exists with this id");
		}
	}

	@Override
	public Long getNextUserId() {
		return userInfoRepository.getNextUserId();
	}
}