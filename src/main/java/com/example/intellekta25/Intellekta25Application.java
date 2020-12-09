package com.example.intellekta25;

import com.example.intellekta25.entity.Product;
import com.example.intellekta25.entity.Sales;
import com.example.intellekta25.jdbc.ProductJdbcRepository;
import com.example.intellekta25.jdbc.SalesJdbcRepository;
import com.example.intellekta25.jpa.ProductJpa;
import com.example.intellekta25.jpa.SalesJPA;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootApplication
public class Intellekta25Application {
	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext = new SpringApplicationBuilder(Intellekta25Application.class)
				.web(WebApplicationType.NONE)
				.run(args);

		// JPA
			SalesJPA salesJPA = configurableApplicationContext.getBean(SalesJPA.class);
			System.out.println("JPA sales count: "+salesJPA.count());
			System.out.println("JPA sales get by id: "+salesJPA.findById(1l).get());

			// Save
				Sales salesForSaveJPA = new Sales();
				salesForSaveJPA.setId(5);
				salesForSaveJPA.setPrice(30000);
				salesForSaveJPA.setDateGoodIncoming(java.sql.Date.valueOf("2018-07-04"));
				salesForSaveJPA.setDateSale(java.sql.Date.valueOf("2019-07-04"));
				ProductJpa productJpa = configurableApplicationContext.getBean(ProductJpa.class);
				salesForSaveJPA.setProduct(productJpa.findById(1l).get());
				System.out.println("JPA save new item: "+salesJPA.saveAndFlush(salesForSaveJPA));

		// JDBC
			SalesJdbcRepository salesJdbcRepository = configurableApplicationContext.getBean(SalesJdbcRepository.class);
			System.out.println("JDBC sales count: "+salesJdbcRepository.count());
			System.out.println("JDBC sales get by id: "+salesJdbcRepository.findById(1l).get());

			// Save
				Sales salesForSaveJDBC = new Sales();
				salesForSaveJDBC.setId(6);
				salesForSaveJDBC.setPrice(55000);
				salesForSaveJDBC.setDateGoodIncoming(java.sql.Date.valueOf("2018-07-04"));
				salesForSaveJDBC.setDateSale(java.sql.Date.valueOf("2019-07-04"));
				ProductJdbcRepository productJdbcRepository = configurableApplicationContext.getBean(ProductJdbcRepository.class);
				salesForSaveJDBC.setProduct(productJdbcRepository.findById(1l).get());
				System.out.println("JDBC save new item: "+salesJPA.saveAndFlush(salesForSaveJDBC));

	}
}
