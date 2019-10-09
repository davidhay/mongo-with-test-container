package com.ealanta.repo;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ealanta.domain.Customer;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataMongoTest
public class CustomerRepositoryWithContainerTest extends BaseMongoInTestContainerTest {

	private String ONE_FIRST_NAME = "test1first";
	private String ONE_LAST_NAME = "test1last";

	private String TWO_FIRST_NAME = "test2first";
	private String TWO_LAST_NAME = "test2last";
	
	@Value("${customer.collection.name}")
	private String customerCollectionName;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Test
	public void testCustomerCollectionName() {
		Assert.assertEquals("customerz", customerCollectionName);
	}

	@Test
	public void testWriteThenRead() {
		Customer cust1 = new Customer(ONE_FIRST_NAME, ONE_LAST_NAME);
		Assert.assertNull(cust1.getId());
		
		Customer cust2 = new Customer(TWO_FIRST_NAME, TWO_LAST_NAME);
		Assert.assertNull(cust2.getId());

		Customer cust1saved = customerRepo.save(cust1);
		String cust1Id = cust1saved.getId();
		Assert.assertNotNull(cust1Id);
		
		Customer cust2saved = customerRepo.save(cust2);
		String cust2Id = cust2saved.getId();
		Assert.assertNotNull(cust2Id);
		
		checkCustomer(cust1Id, cust1);
		checkCustomer(cust2Id, cust2);
	}
	
	public void checkCustomer(String id, Customer expected) {
		Optional<Customer> res = customerRepo.findById(id);
		Assert.assertTrue(res.isPresent());
		res.ifPresent(cust -> {
			Assert.assertEquals(expected.getFirstName(), cust.getFirstName());
			Assert.assertEquals(expected.getLastName(), cust.getLastName());
		});		
	}
}
