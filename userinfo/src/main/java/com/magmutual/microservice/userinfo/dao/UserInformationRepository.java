package com.magmutual.microservice.userinfo.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.magmutual.microservice.userinfo.model.User;

/**
 * @author Pranitha
 *
 */
@Repository
public interface UserInformationRepository extends JpaRepository<User, Integer> {
	
	List<User> findByProfession(String profession);
	
	List<User> findByDateCreatedBetween(Date startdate, Date endDate);

	@Query(value = "SELECT NEXT VALUE FOR user_id_seq", nativeQuery = true)
	Long getNextUserId();
}
