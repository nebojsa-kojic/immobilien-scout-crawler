package com.prodyna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Crawler for web site www.immobilienscout24.de.
 *
 * @author Boris Gligorijevic, PRODYNA AG
 */
public class App {

	private static Set<Apartment> oldApartments = new HashSet<>();

	public static String WEBSITE_ROOT = "https://www.immobilienscout24.de";

	private static String ROOMS_MIN = "1,5";

	private static String ROOMS_MAX = "2,5";

	private static String SIZE_MIN = "40";

	private static String SIZE_MAX = "60";

	private static String PRICE_MIN = "600";

	private static String PRICE_MAX = "900";

	private static String FREQUENCY = "10";

	private static boolean TRAVEL_TIME = true;

	private static String DESTINATION = "place_id:ChIJAZZ5YDV8nkcRJ29QgCLx0eQ";

	public static void main(String[] args) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		try {
			System.out.print("Use defaults? Y/n: ");
			String def = reader.readLine();

			if (def.equalsIgnoreCase("n")) {
				System.out.print("Rooms min: ");
				ROOMS_MIN = reader.readLine();
				System.out.print("Rooms max: ");
				ROOMS_MAX = reader.readLine();

				System.out.print("Size min: ");
				SIZE_MIN = reader.readLine();
				System.out.print("Size max: ");
				SIZE_MAX = reader.readLine();

				System.out.print("Price min: ");
				PRICE_MIN = reader.readLine();
				System.out.print("Price max: ");
				PRICE_MAX = reader.readLine();

				System.out.print("How often do I run (minutes): ");
				FREQUENCY = reader.readLine();

				System.out.print("Enable travel time calculations? Y/n: ");
				String distance = reader.readLine();
				if (distance.equalsIgnoreCase("n")) {
					TRAVEL_TIME = false;
				}
			}

			if(TRAVEL_TIME){
				System.out.print("Calculate travel time to : ");
				DESTINATION = reader.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Min rooms: " + ROOMS_MIN);
		System.out.println("Max rooms: " + ROOMS_MAX);

		System.out.println("Min size: " + SIZE_MIN);
		System.out.println("Max size: " + SIZE_MAX);

		System.out.println("Min price: " + PRICE_MIN);
		System.out.println("Max price: " + PRICE_MAX);

		System.out.println("Travel time enabled: " + TRAVEL_TIME);

		if(TRAVEL_TIME){
			System.out.println("Calculate travel time to: " + DESTINATION);
		}

		System.out.println("I will run every " + FREQUENCY + " minutes.");

		startRunner();
	}

	private static void startRunner() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						System.out.println(new Date().toString() + " Working...");

						String rootUrl = constructUrl();
						Document doc = Jsoup.connect(rootUrl).get();
						int noPages = getNoPages(doc);

						Set<Apartment> newApartments = new HashSet<>();
						for (int i = 1; i <= noPages; i++) {
							if (i == 1) {
								Set<Apartment> firstPageResults = processPage(doc);
								newApartments.addAll(firstPageResults);
							} else {
								String pageUrl = rootUrl.replace("S-T/", "S-T/P-" + i + "/");
								Set<Apartment> nthPageResults = processPage(pageUrl);
								newApartments.addAll(nthPageResults);
							}
						}

						if (!oldApartments.isEmpty()) {
							Set<Apartment> diff = new HashSet<>();

							for (Apartment newApartment : newApartments) {
								if (!oldApartments.contains(newApartment)) {
									newApartment.setTravelTime(TravelTimeService.getTravelTime(newApartment.getAddress(), DESTINATION));
									diff.add(newApartment);
								}
							}

							if (!diff.isEmpty()) {
								GmailSender.send(diff);
							}
						}

						oldApartments = newApartments;


						int milis = 1000 * 60 * Integer.valueOf(FREQUENCY);
						Thread.sleep(milis);
					} catch (InterruptedException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		t.start();
	}

	private static String constructUrl() {
		String url = WEBSITE_ROOT + "/Suche/S-T/Wohnung-Miete/Bayern/Muenchen/-/%s-%s/%s-%s/EURO-%s-%s/-/-/-/true";

		return String.format(url, ROOMS_MIN, ROOMS_MAX, SIZE_MIN, SIZE_MAX, PRICE_MIN, PRICE_MAX);
	}

	private static int getNoPages(Document doc) {
		Element ul = doc.getElementById("pageSelection");
		Elements selects = ul.getElementsByTag("select");

		return selects.get(0).getElementsByTag("option").size();
	}

	public static Set<Apartment> processPage(Document doc) throws IOException {
		Set<Apartment> results = new HashSet<>();

		Element ul = doc.getElementById("resultListItems");

		for (Element li : ul.getElementsByTag("li")) {

			// skip non-private apartments
			Elements privately = li.getElementsByAttributeValueContaining("class", "private block");
			if (privately.isEmpty()) {
				continue;
			}

			String apartmentId = "";

			Elements articleElements = li.getElementsByAttributeStarting("data-obid");
			for (Element article : articleElements) {
				apartmentId = article.attr("data-obid");
			}

			Apartment  apartment = new Apartment(apartmentId);

			if (TRAVEL_TIME){
				Elements addressElem = li.getElementsByAttributeValueContaining("class", "result-list-entry__address");
				String startLocation = addressElem.first().child(0).ownText();
				apartment.setAddress(startLocation);
			}

			results.add(apartment);

		}

		return results;
	}

	public static Set<Apartment> processPage(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		return processPage(doc);
	}
}
