package com.magmutual.microservice.userinfo.service;

import java.sql.Date;
import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.model.UserInfoResponse;

/**
 * @author Pranitha
 *
 */

@Service
public interface IUserInformationService {

	public UserInfoResponse getSpecificUserByID(Integer id);

	public UserInfoResponse getUsersByDateRange(Date startDate, Date endDate) throws ParseException;

	public UserInfoResponse getUsersByProfession(String profession);

	public void createUser(User user);
	
	public Long getNextUserId();

}