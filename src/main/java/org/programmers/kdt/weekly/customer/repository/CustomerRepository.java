package org.programmers.kdt.weekly.customer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.programmers.kdt.weekly.customer.model.Customer;

public interface CustomerRepository {

	Customer insert(Customer customer);

	List<Customer> findAll();

	Optional<Customer> findById(UUID customerId);

	Optional<Customer> findByEmail(String customerEmail);

	List<Customer> findByType(String customerType);

	Customer update(Customer customer);

	void deleteAll();
}