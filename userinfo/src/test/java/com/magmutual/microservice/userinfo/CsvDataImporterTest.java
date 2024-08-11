package com.magmutual.microservice.userinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.magmutual.microservice.userinfo.Util.DateUtils;
import com.magmutual.microservice.userinfo.dao.UserInformationRepository;
import com.magmutual.microservice.userinfo.model.User;
import com.magmutual.microservice.userinfo.service.CsvDataImporter;

/**
 * @author Pranitha
 *
 */
@ExtendWith(MockitoExtension.class)
public class CsvDataImporterTest {

	@InjectMocks
	private CsvDataImporter csvDataImporter;

	@Mock
	private UserInformationRepository userRepository;

	@Test
	void testLoadCSVDatasuccess() throws Exception {
		String csvData = "id,firstname,lastname,email,profession,dateCreated,country,city\n"
				+ "1,John,Daniel,john.daniel@example.com,Developer,2024-08-09,USA,New York\n";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

		// Mock the DateUtils.convertStringToDate method
		Date mockDate = Date.valueOf("2024-08-09");
		try (var mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.convertStringToDate("2024-08-09")).thenReturn(mockDate);

			csvDataImporter.loadCSVData(csvParser);

			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userCaptor.capture());

			User capturedUser = userCaptor.getValue();

			assertEquals(1, capturedUser.getId());
			assertEquals("John", capturedUser.getFirstName());
			assertEquals("Daniel", capturedUser.getLastName());
			assertEquals("john.daniel@example.com", capturedUser.getEmail());
			assertEquals("Developer", capturedUser.getProfession());
			assertEquals(mockDate, capturedUser.getDateCreated());
			assertEquals("USA", capturedUser.getCountry());
			assertEquals("New York", capturedUser.getCity());
		}
	}
}
