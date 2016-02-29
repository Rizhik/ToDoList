package org.gradle;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Component
public class Trash{

	@Bean
	public DataSource dataSource(){
	  
    	MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setURL("jdbc:mysql://localhost:3306/javabase");
        mysqlDS.setUser("java");
        mysqlDS.setPassword("password");
        return mysqlDS;

	}
}