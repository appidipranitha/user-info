package com.magmutual.microservice.userinfo.model;

import java.util.List;

/**
 * @author Pranitha
 *
 */

public class UserInfoResponse {
	
	private String metadata;
	
	private List<User> users;

	/**
	 * @return the metadata
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

}
