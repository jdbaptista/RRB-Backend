package com.jdbaptista.Office;

import com.mongodb.client.MongoClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@SpringBootApplication
public class OfficeApplication {

//	private static final Log log = LogFactory.getLog(OfficeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OfficeApplication.class, args);
	}

//		MongoOperations mongo = new MongoTemplate(MongoClients.create(), "database");
////		mongoOps.insert(new ProjectCell("2924 San Marcos Pass Road", "Jaden", "Drywall", 8));
////		mongoOps.insert(new ProjectCell("2924 San Marcos Pass Road", "Daniel", "Framing", 8));
////
////
////		log.info(mongo.findOne(new Query(where("name").is("Jaden")), ProjectCell.class));
//
//		for (String s : mongo.getCollectionNames()) {
//			System.out.println(s);
//		}
//		List<ProjectCell> cells = mongo.findAll(ProjectCell.class, "projectCell");
//		for (ProjectCell c : cells) {
//			System.out.println(c);
//		}
//		mongo.dropCollection("projectCell");
//		SpringApplication.run(OfficeApplication.class, args);
//	}
}
