package com.azkivam.bank.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

	T save(T dto);
	
	boolean delete(T dto);
	
	Optional<T> findById(Long id);

}
