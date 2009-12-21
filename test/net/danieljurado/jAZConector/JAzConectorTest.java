package net.danieljurado.jAZConector;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JAzConectorTest {

	JAzConector jAzConector;

	@Before
	public void setUp() throws Exception {
		jAzConector = new JAzConector();
		jAzConector.init();
		assertTrue(jAzConector.openStream("localhost", 25000));
	}

	@After
	public void tearDown() {
		assertTrue(jAzConector.closeStream());
	}

	@Test
	public void testAlternateCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testAnswerCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testConferenceCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testConsultationCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAgentState() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetEventObserver() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastANI() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastCallId() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastCause() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastDNIS() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastUserDetails() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastUserInfo() {
		fail("Not yet implemented");
	}

	@Test
	public void testHoldCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsConnected() {
		fail("Not yet implemented");
	}

	@Test
	public void testLogIn() {
		fail("Not yet implemented");
	}

	@Test
	public void testLogOut() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotReady() {
		fail("Not yet implemented");
	}

	@Test
	public void testReady() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcw() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetrieveCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPort() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetServer() {
		fail("Not yet implemented");
	}

	@Test
	public void testTransferCall() {
		fail("Not yet implemented");
	}

}
