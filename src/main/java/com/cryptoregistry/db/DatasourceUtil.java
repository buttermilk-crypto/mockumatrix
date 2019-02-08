package com.cryptoregistry.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatasourceUtil {
	
	public static final ComboPooledDataSource ds = initDataSource();

	private static ComboPooledDataSource initDataSource() {
		
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setJdbcUrl("jdbc:mysql://localhost/twitter_data");
		cpds.setUser("dev");
		cpds.setPassword("Simran99$");

		// Optional Settings
		cpds.setInitialPoolSize(5);
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
		cpds.setMaxStatements(100);
		
		

		return cpds;
	}
	
	
}
