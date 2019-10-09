package com.ealanta.repo;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;

@ContextConfiguration(initializers = { BaseMongoInTestContainerTest.Initializer.class })
public abstract class BaseMongoInTestContainerTest {

	public static final int STANDARD_MONGO_PORT = 27017;
	
	private final Logger log = LoggerFactory.getLogger(BaseMongoInTestContainerTest.class);
	
	@ClassRule
	public static GenericContainer mongo = new GenericContainer("mongo:4.0.12");

	//helps check that Spring is using overridden property value from mongo test container
	@Value("${spring.data.mongodb.host}")
	private String springPropHostname;
	
	//helps check that Spring is using overridden property value from mongo test container
	@Value("${spring.data.mongodb.port}")
	private int springPropPort;
		
	//helps check that Spring is using overridden property value from rabbit test container
	@Value("${spring.data.mongodb.database}")
	private String springPropDatabase;
	
	@Test
	public void baseTestCheckSpringRabbitPropertiesFromTestContainer() {
		
		int mongoPort = mongo.getMappedPort(STANDARD_MONGO_PORT);
		String mongoHost = mongo.getContainerIpAddress();
		
		log.info("Mongo Host [{}]",  mongoHost);
		log.info("Mongo Port [{}]",  mongoPort);
		
		log.info("Spring Host [{}]",  springPropHostname);
		log.info("Spring Port [{}]",  springPropPort);
		log.info("Spring Database [{}]",  springPropDatabase);

		Assert.assertEquals(springPropHostname, mongoHost);
		Assert.assertEquals(springPropPort, mongoPort);
		Assert.assertEquals("testdb", springPropDatabase);
	}
	
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongo.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongo.getMappedPort(STANDARD_MONGO_PORT)
            );
            values.applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
