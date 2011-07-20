package com.pintu.dao.impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.pintu.dao.DBAccessInterface;

public class DBAccessImplement implements DBAccessInterface {

	private JdbcTemplate jdbcTemplate;
	
	//Constructor
	public DBAccessImplement () {
		
	}
	
	
	//ÓÉSpring×¢Èë
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
	}
	
	
}
