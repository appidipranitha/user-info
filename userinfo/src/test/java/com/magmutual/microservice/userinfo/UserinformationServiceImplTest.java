package com.magmutual.microservice.userinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import com.magmutual.microservice.userinfo.dao.UserInformationRepository;
import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.model.UserInfoResponse;
import com.magmutual.microservice.userinfo.service.UserinformationServiceImpl;

/**
 * @author Pranitha
 *
 */

public class UserinformationServiceImplTest {

    @Mock
    private UserInformationRepository userInfoRepository;

    @InjectMocks
    private UserinformationServiceImpl userinformationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSpecificUserByID_UserFound() {
        User user = new User();
        user.setId(1);
        Optional<User> optionalUser = Optional.of(user);
        when(userInfoRepository.findById(anyInt())).thenReturn(optionalUser);

        UserInfoResponse response = userinformationService.getSpecificUserByID(1);

        assertEquals(1, response.getUsers().size());
        assertEquals(user, response.getUsers().get(0));
        assertEquals("Users Count: 1", response.getMetadata());
    }

    @Test
    public void testGetSpecificUserByID_UserNotFound() {
        when(userInfoRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserInfoResponse response = userinformationService.getSpecificUserByID(1);

        assertTrue(response.getUsers().isEmpty());
        assertEquals("Users Count: 0", response.getMetadata());
    }

    @Test
    public void testGetUsersByDateRange() throws Exception {
        Date startDate = Date.valueOf("2024-01-01");
        Date endDate = Date.valueOf("2024-01-31");
        User user = new User();
        user.setDateCreated(startDate);
        List<User> users = Collections.singletonList(user);
        when(userInfoRepository.findByDateCreatedBetween(any(Date.class), any(Date.class))).thenReturn(users);

        UserInfoResponse response = userinformationService.getUsersByDateRange(startDate, endDate);

        assertEquals(1, response.getUsers().size());
        assertEquals(user, response.getUsers().get(0));
        assertEquals("Users Count: 1", response.getMetadata());
    }

    @Test
    public void testGetUsersByDateRange_NoUsersFound() throws Exception {
        Date startDate = Date.valueOf("2024-01-01");
        Date endDate = Date.valueOf("2024-01-31");
        when(userInfoRepository.findByDateCreatedBetween(any(Date.class), any(Date.class))).thenReturn(Collections.emptyList());

        UserInfoResponse response = userinformationService.getUsersByDateRange(startDate, endDate);

        assertTrue(response.getUsers().isEmpty());
        assertEquals("Users Count: 0", response.getMetadata());
    }

    @Test
    public void testGetUsersByProfession() {
        User user = new User();
        user.setProfession("Developer");
        List<User> users = Collections.singletonList(user);
        when(userInfoRepository.findByProfession(anyString())).thenReturn(users);

        UserInfoResponse response = userinformationService.getUsersByProfession("Developer");

        assertEquals(1, response.getUsers().size());
        assertEquals(user, response.getUsers().get(0));
        assertEquals("Users Count: 1", response.getMetadata());
    }

    @Test
    public void testGetUsersByProfession_NoUsersFound() {
        when(userInfoRepository.findByProfession(anyString())).thenReturn(Collections.emptyList());

        UserInfoResponse response = userinformationService.getUsersByProfession("Developer");

        assertTrue(response.getUsers().isEmpty());
        assertEquals("Users Count: 0", response.getMetadata());
    }

    @Test
    public void testCreateUser_UserDoesNotExist() {
        User user = new User();
        user.setId(1);
        when(userInfoRepository.findById(anyInt())).thenReturn(Optional.empty());

        userinformationService.createUser(user);

        verify(userInfoRepository).save(user);
    }

    @Test
    public void testCreateUser_UserAlreadyExists() {
        User user = new User();
        user.setId(1);
        when(userInfoRepository.findById(anyInt())).thenReturn(Optional.of(user));

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            userinformationService.createUser(user);
        });

        assertEquals("User already exists with this id", thrown.getReason());
    }

    @Test
    public void testGetNextUserId() {
        when(userInfoRepository.getNextUserId()).thenReturn(1L);

        Long nextId = userinformationService.getNextUserId();

        assertEquals(1L, nextId);
    }
}

