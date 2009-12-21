package net.danieljurado.jAZConector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JAzConectorTest {

	private static JAzConector jAzConector;

	@Before
	public void setUp() {
		jAzConector.logOut(station, agentId, agentGroup);
	}

	@After
	public void tearDown() {
		jAzConector.logOut(station, agentId, agentGroup);
	}

	@BeforeClass
	public static void classSetUp() {
		jAzConector = new JAzConector();
		jAzConector.init();
		assertTrue(jAzConector.openStream("localhost", 24000));
		while (!jAzConector.isConnected())
			espera();
	}

	private static void espera() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void classTearDown() {
		assertTrue(jAzConector.closeStream());
	}

	@Test(timeout = 1000)
	public void testAlternateCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testAnswerCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testClearCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testConferenceCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testConsultationCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetAgentState() {
		assertEquals(JAzConector.AS_LOGGEDOUT, jAzConector.getAgentState());
		assertTrue(jAzConector.logIn(station, agentId, agentGroup,
				agentPassword));
		assertEquals(JAzConector.AS_NOTREADY, jAzConector.getAgentState());
		assertTrue(jAzConector.ready(station, agentId, agentGroup));
		assertEquals(JAzConector.AS_READY, jAzConector.getAgentState());
		assertTrue(jAzConector.notReady(station, agentId, agentGroup,
				reasonCode));
		assertEquals(JAzConector.AS_NOTREADY, jAzConector.getAgentState());
	}

	@Test(timeout = 1000)
	public void testGetEventObserver() {
		assertNotNull(jAzConector.getEventObserver());
	}

	@Test(timeout = 1000)
	public void testGetLastANI() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastCallId() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastCause() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastDNIS() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastEvent() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastUserDetails() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testGetLastUserInfo() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testHoldCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testIsConnected() {
		assertTrue(jAzConector.isConnected());
	}

	private static final String station = "3333";
	private static final String agentId = "3333";
	private static final String agentGroup = "101";
	private static final String agentPassword = "3333";

	@Test(timeout = 1000)
	public void testLogIn() {
		assertTrue(jAzConector.logIn(station, agentId, agentGroup,
				agentPassword));
	}

	@Test(timeout = 1000)
	public void testLogOut() {
		jAzConector.logIn(station, agentId, agentGroup, agentPassword);
		assertTrue(jAzConector.logOut(station, agentId, agentGroup));
	}

	@Test(timeout = 1000)
	public void testMakeCall() {
		fail("Not yet implemented");
	}

	private static final int reasonCode = 0;

	@Test(timeout = 1000)
	public void testNotReady() {
		jAzConector.logIn(station, agentId, agentGroup, agentPassword);
		jAzConector.ready(station, agentId, agentGroup);
		assertTrue(jAzConector.notReady(station, agentId, agentGroup,
				reasonCode));
	}

	@Test(timeout = 1000)
	public void testReady() {
		jAzConector.logIn(station, agentId, agentGroup, agentPassword);
		assertTrue(jAzConector.ready(station, agentId, agentGroup));
	}

	@Test(timeout = 1000)
	public void testAcw() {
		jAzConector.logIn(station, agentId, agentGroup, agentPassword);
		jAzConector.ready(station, agentId, agentGroup);
		assertTrue(jAzConector.acw(station, agentId, agentGroup));
	}

	@Test(timeout = 1000)
	public void testRetrieveCall() {
		fail("Not yet implemented");
	}

	@Test(timeout = 1000)
	public void testTransferCall() {
		fail("Not yet implemented");
	}

}
