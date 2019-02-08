package com.cryptoregistry.mockumatrix;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.cryptoregistry.db.DatasourceUtil;

public class DatasourceUtilTest {

	@Test
	public void test0() {
		try (
			Connection con = DatasourceUtil.ds.getConnection();
		){
			
			Assert.assertTrue(con.isValid(1000));
			
		

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
