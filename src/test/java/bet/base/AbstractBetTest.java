package bet.base;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

public abstract class AbstractBetTest extends Assert {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

}
