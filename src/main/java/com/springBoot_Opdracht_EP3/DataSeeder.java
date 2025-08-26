package com.springBoot_Opdracht_EP3;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import entity.Address;
import entity.Category;
import entity.Festival;
import entity.MyUser;
import entity.Registration;
import entity.Vendor;
import enums.Role;
import lombok.extern.slf4j.Slf4j;
import repository.AddressRepository;
import repository.CategoryRepository;
import repository.FestivalRepository;
import repository.RegistrationRepository;
import repository.UserRepository;
import repository.VendorRepository;

/**
 * Populates the database with initial data for addresses, categories, users,
 * vendors, festivals, and registrations using DAO or JPA EntityManager. This
 * class reads CSV files for data sources, creates entity objects, and persists
 * them into the database. It also contains a method for populating using JPA
 * directly.
 */
@Slf4j
@Component
public class DataSeeder implements CommandLineRunner {
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private FestivalRepository festivalRepository;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private VendorRepository vendorRepository;

	private String loremIpsum = "Proin et purus sed elit sodales elementum sit amet in lacus. Suspendisse quis lobortis quam. Aliquam sodales mollis diam,"
			+ "a vestibulum lorem congue et. Pellentesque et quam felis. Quisque quis ligula in libero scelerisque laoreet eget non purus."
			+ "Donec congue tincidunt egestas. Fusce at laoreet enim. Nam lobortis lectus eget massa posuere condimentum.";

	private PasswordEncoder encoder = new BCryptPasswordEncoder();

	/**
	 * Runs the population process using DAO classes. Reads CSV files and inserts
	 * addresses, categories, users, vendors, festivals, and registrations into the
	 * database within a transaction.
	 */
	@Transactional
	public void run(String... args) {
		try {
			log.info("!!! Started populating database");

			// Addresses, categories, users
			List<Address> addresses = createAddresses(Paths.get("src/main/resources/populate-data/address-data.csv"));
			List<Category> categories = createCategories(
					Paths.get("src/main/resources/populate-data/category-data.csv"));
			List<MyUser> myUsers = createUsers(Paths.get("src/main/resources/populate-data/user-data.csv"));

			addressRepository.saveAll(addresses);
			log.info("Addresses populated: {}", addresses.size());
			categoryRepository.saveAll(categories);
			log.info("Categories populated: {}", categories.size());
			userRepository.saveAll(myUsers);
			log.info("Users populated: {}", myUsers.size());

			// Vendors
			List<Vendor> vendors = createVendors(addresses, categories,
					Paths.get("src/main/resources/populate-data/vendor-data.csv"));
			vendorRepository.saveAll(vendors);
			log.info("Vendors populated: {}", vendors.size());

			// Festivals
			List<Festival> festivals = createFestivals(addresses, categories, vendors,
					Paths.get("src/main/resources/populate-data/festival-data.csv"), 3, 5);
			festivalRepository.saveAll(festivals);
			log.info("Festivals populated: {}", festivals.size());

			// Registrations (composite key handled by Registration entity/Repository)
			List<Registration> registrations = createRegistrations(myUsers, festivals, 200);
			registrationRepository.saveAll(registrations);
			log.info("Registrations populated: {}", registrations.size());

			log.info("!!! Finished populating database");
			// No explicit commit needed; @Transactional will commit if no exception is
			// thrown.
		} catch (Exception e) {
			// Transaction will roll back automatically on runtime exceptions
			log.error("Error while populating database", e);
		}
	}

	/**
	 * Reads a CSV file and creates a list of Address objects.
	 *
	 * @param csvFilePath the path to the CSV file containing address data
	 * @return a list of Address entities created from the CSV data
	 * @throws IOException if reading the file fails
	 */
	public List<Address> createAddresses(Path csvFilePath) throws IOException {
		List<Address> addresses = new ArrayList<>();

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 7)
					continue;

				// Constructor: name, country, city, postalCode, street, number, addition
				addresses.add(new Address(fields[0], fields[1], fields[2], Integer.parseInt(fields[3]), fields[4],
						Integer.parseInt(fields[5]), fields[6]));
			}
		}

		return addresses;
	}

	/**
	 * Reads a CSV file and creates a list of Category objects.
	 *
	 * @param csvFilePath the path to the CSV file containing category data
	 * @return a list of Category entities created from the CSV data
	 * @throws IOException if reading the file fails
	 */
	public List<Category> createCategories(Path csvFilePath) throws IOException {
		List<Category> categories = new ArrayList<>();

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 2)
					continue;

				// Constructor: name, description
				categories.add(new Category(fields[0], fields[1]));
			}
		}

		return categories;
	}

	/**
	 * Reads a CSV file and creates a list of User objects. Also adds two hardcoded
	 * admin users. Assigns roles randomly with 20% chance of ADMIN and 80% chance
	 * of USER.
	 *
	 * @param csvFilePath the path to the CSV file containing user data
	 * @return a list of User entities created from the CSV data
	 * @throws IOException if reading the file fails
	 */
	public List<MyUser> createUsers(Path csvFilePath) throws IOException {
		List<MyUser> myUsers = new ArrayList<>();
		Random random = new Random();

		// Add a fixed user and admin for testing
		myUsers.add(
				new MyUser("Hendrik", "Senechal", "h.s@gmail.com", "32478683011", Role.ADMIN, encoder.encode("123")));
		myUsers.add(
				new MyUser("Ana-Laura", "Castelijn", "a.c@gmail.com", "32476513846", Role.USER, encoder.encode("123")));

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 5)
					continue;

				// Constructor: name, firstName, email, phoneNumber, role, password)
				myUsers.add(
						new MyUser(fields[0], fields[1], fields[2], fields[3], Role.USER, encoder.encode(fields[4])));
			}
		}

		return myUsers;
	}

	/**
	 * Reads a CSV file and creates a list of Vendor objects. Assigns random
	 * categories and addresses to each vendor.
	 *
	 * @param addresses   the list of available addresses
	 * @param categories  the list of available categories
	 * @param csvFilePath the path to the CSV file containing vendor data
	 * @return a list of Vendor entities created from the CSV data
	 * @throws IOException if reading the file fails
	 */
	public List<Vendor> createVendors(List<Address> addresses, List<Category> categories, Path csvFilePath)
			throws IOException {
		List<Vendor> vendors = new ArrayList<>();
		Random random = new Random();

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 7)
					continue;

				// Constructor:name, description, phoneNumber, email, website, logoUrl,
				// vendorRating
				Vendor vendor = new Vendor(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5],
						Integer.parseInt(fields[6]));

				// add random category and random address
				vendor.setCategory(categories.get(random.nextInt(categories.size())));
				vendor.setAddress(addresses.get(random.nextInt(addresses.size())));

				vendors.add(vendor);
			}
		}

		return vendors;
	}

	/**
	 * Creates a list of Festival objects based on the provided CSV data file. Each
	 * Festival is assigned a random start and end date within the current year, a
	 * random address, category, and a random set of vendors.
	 *
	 * @param addresses   the list of possible addresses to assign to festivals
	 * @param categories  the list of possible categories to assign to festivals
	 * @param vendors     the list of possible vendors to assign to festivals
	 * @param csvFilePath the path to the CSV file containing festival data
	 * @param minVendors  the minimum number of vendors to assign to each festival
	 * @param maxVendors  the maximum number of vendors to assign to each festival
	 * @return a list of populated Festival objects
	 * @throws IOException if an error occurs reading the CSV file
	 */
	private List<Festival> createFestivals(List<Address> addresses, List<Category> categories, List<Vendor> vendors,
			Path csvFilePath, int minVendors, int maxVendors) throws IOException {
		List<Festival> festivals = new ArrayList<>();
		LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
		Random random = new Random();

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 4)
					continue;

				// Constructor: name, descritpion, price, amount
				Festival festival = new Festival(fields[0], fields[1], Integer.parseInt(fields[2]),
						Integer.parseInt(fields[3]));

				// Set start and end date
				LocalDateTime date = startOfYear.plusDays(random.nextInt(365 - 5)).atTime(random.nextInt(8) + 10, 0);
				festival.setStart(date);
				festival.setEnd(date.plusDays(3 + random.nextInt(3)));

				// Set random Address and Category
				if (!addresses.isEmpty())
					festival.setAddress(addresses.get(random.nextInt(addresses.size())));
				int categoryIndex = Math.max(categories.size() - 1, 1);
				festival.setCategory(categories.get(random.nextInt(categoryIndex)));

				// Set random vendors between minVendors and maxVendors
				int vendorCount = 3 + random.nextInt(maxVendors - minVendors - 1);
				Set<Integer> usedIndexes = new HashSet<>();
				while (festival.getVendors().size() < vendorCount) {
					int index = random.nextInt(vendors.size());
					if (usedIndexes.add(index)) {
						Vendor vendor = vendors.get(index);
						festival.getVendors().add(vendor);
						vendor.getFestivals().add(festival);
					}
				}

				festivals.add(festival);
			}
		}
		return festivals;
	}

	/**
	 * Creates a list of Registration objects associating users with festivals. Only
	 * non-admin users are registered. Each registration includes a random rating,
	 * comment, and registration date.
	 *
	 * @param myUsers   the list of users to create registrations for (admins
	 *                  excluded)
	 * @param festivals the list of festivals to register users to
	 * @param amount    the total number of registrations to create
	 * @return a list of Registration objects
	 */
	private List<Registration> createRegistrations(List<MyUser> myUsers, List<Festival> festivals, int amount) {
		Random random = new Random();
		List<Registration> registrations = new ArrayList<>();
		List<Map.Entry<MyUser, Festival>> allPairs = new ArrayList<>();
		List<MyUser> nonAdminUsers = myUsers.stream().filter(user -> user.getRole() != Role.ADMIN)
				.collect(Collectors.toList());

		// List with comments to randomly select from
		String[] comments = { "Amazing festival!", "Great food!", "Loved the music!", "Will come again!",
				"Too crowded but fun", "Perfect day!", "Not bad at all", "Met cool people!", "Danced all night",
				"The vibe was unreal!", "Nice vendors", "Could use more toilets", "Loved the setting",
				"Really enjoyed it", "Fantastic atmosphere", "It was okay", "Superb!", "Unforgettable!",
				"Great artists!", "Chill event!", "So relaxing", "Food was fire!", "Unexpectedly awesome", "Super fun",
				"Best day ever!", "Lovely ambiance", "Music too loud", "Tasty treats", "Clean and safe",
				"Top-notch lineup" };

		// Create all possible festival_user pairings
		for (MyUser myUser : nonAdminUsers)
			for (Festival festival : festivals)
				allPairs.add(new AbstractMap.SimpleEntry<>(myUser, festival));

		// Shuffle all possible festival_user pairings
		Collections.shuffle(allPairs, random);

		// Add 'argument: amount' of registrations to the list by iterating over the
		// shuffled list untill the max amount has been added. Adds randomly selected
		// comment, rating between 3 and 5, random dateTime
		for (int i = 0; i < Math.min(amount, allPairs.size()); i++) {
			String comment = comments[random.nextInt(comments.length)];
			int rating = 3 + random.nextInt(3); // 3 to 5
			LocalDateTime regDate = LocalDateTime.now().minusDays(random.nextInt(60) + 1);

			registrations.add(new Registration(rating, comment, loremIpsum, regDate, allPairs.get(i).getKey(),
					allPairs.get(i).getValue()));
		}

		return registrations;
	}
}
