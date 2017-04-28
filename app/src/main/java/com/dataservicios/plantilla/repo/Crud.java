package com.dataservicios.plantilla.repo;

import java.util.List;

interface Crud
{
	public int create(Object item);
	public int update(Object item);
	public int delete(Object item);
	public int deleteAll();
	public Object findById(int id);
	public List<?> findAll();
}
