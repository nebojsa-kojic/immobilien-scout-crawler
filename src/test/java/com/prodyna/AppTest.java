package com.prodyna;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

	/**
	 * Rigourous Test :-)
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testApp() throws IOException, URISyntaxException {
		Set<Apartment> apartments = new HashSet<>();

		Apartment apartment1 = new Apartment("87620534");
		Apartment apartment2 = new Apartment("87619734");

		apartments.add(apartment1);
		apartments.add(apartment2);

		GmailSender.send(apartments);
	}

	@Test
	@Ignore
	public void testDuration(){
		System.out.println(TravelTimeService.getTravelTime(" Sendlinger Tor, München ", "place_id:ChIJAZZ5YDV8nkcRJ29QgCLx0eQ"));
	}
}
