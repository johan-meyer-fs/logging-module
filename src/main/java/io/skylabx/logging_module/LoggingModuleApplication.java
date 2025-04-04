package io.skylabx.logging_module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.MDC;
import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@Slf4j
public class LoggingModuleApplication {

	public static void main(String[] args) {

		MDC.put("orderNumber", "1234567890");

		log.info("Order placed.");

		MDC.put("buyerName", "jack");
		MDC.put("destination", "xxxxxxxxxx");

		log.info("Order shipped successfully.");

		// Remove items
		MDC.remove("buyerName");
		MDC.remove("destination");

		log.warn("Order shipment failed.");

		// Clear all items
		MDC.clear();
		SpringApplication.run(LoggingModuleApplication.class, args);
	}

}
