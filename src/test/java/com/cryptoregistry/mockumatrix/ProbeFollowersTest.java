package com.cryptoregistry.mockumatrix;

import org.junit.Assert;
import org.junit.Test;

public class ProbeFollowersTest {

	@Test
	public void probe0() {
		ProbeFollowers pf = new ProbeFollowers("MikeEnochTRS");
		pf.run();
		Assert.assertTrue(true);
		
	}
}
