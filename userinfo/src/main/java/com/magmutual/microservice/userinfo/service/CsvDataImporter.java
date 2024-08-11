package com.magmutual.microservice.userinfo.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.magmutual.microservice.userinfo.Util.DateUtils;
import com.magmutual.microservice.userinfo.dao.UserInformationRepository;
import com.magmutual.microservice.userinfo.model.User;

/**
 * @author Pranitha
 *
 */

@Component
public class CsvDataImporter implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(CsvDataImporter.class);

	@Autowired
	private UserInformationRepository userRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/UserInformation.csv"),
				StandardCharsets.UTF_8);
			 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			loadCSVData(csvParser);
		} catch (IOException e) {
			throw new RuntimeException("CSV data failed to parse: " + e.getMessage(), e);
		}
	}

	public void loadCSVData(CSVParser csvParser) {
		int recordCount = 0;
		for (CSVRecord csvRecord : csvParser) {
			try {
				User user = new User();
				user.setId(Integer.parseInt(csvRecord.get("id")));
				user.setFirstName(csvRecord.get("firstname"));
				user.setLastName(csvRecord.get("lastname"));
				user.setEmail(csvRecord.get("email"));
				user.setProfession(csvRecord.get("profession"));
				String dateCreatedStr = csvRecord.get("dateCreated");
				Date dateCreated = DateUtils.convertStringToDate(dateCreatedStr);
				user.setDateCreated(dateCreated);
				user.setCountry(csvRecord.get("country"));
				user.setCity(csvRecord.get("city"));

				userRepository.save(user);
				recordCount++;
			} catch (Exception e) {
				logger.warn("Failed to process record: " + csvRecord.toString(), e);
			}
		}
		logger.info("Processed {} records from CSV file", recordCount);
	}
}
