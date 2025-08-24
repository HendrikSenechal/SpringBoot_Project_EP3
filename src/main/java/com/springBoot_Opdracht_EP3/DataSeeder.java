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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import entity.Address;
import entity.Category;
import entity.Festival;
import entity.Registration;
import entity.User;
import entity.UserFestivalKey;
import entity.Vendor;
import enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
			List<User> users = createUsers(Paths.get("src/main/resources/populate-data/user-data.csv"));

			addressRepository.saveAll(addresses);
			log.info("Addresses populated: {}", addresses.size());
			categoryRepository.saveAll(categories);
			log.info("Categories populated: {}", categories.size());
			userRepository.saveAll(users);
			log.info("Users populated: {}", users.size());

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
			List<Registration> registrations = createRegistrations(users, festivals, 200);
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
	public List<User> createUsers(Path csvFilePath) throws IOException {
		List<User> users = new ArrayList<>();
		Random random = new Random();

		// Add a fixed user and admin for testing
		users.add(new User("Hendrik", "Senechal", "h.s@gmail.com", "32478683011", Role.ADMIN, "123"));
		users.add(new User("Ana", "Castelijn", "a.c@gmail.com", "32476513846", Role.ADMIN, "123"));

		// Read CSV file with object data inside
		try (BufferedReader br = Files.newBufferedReader(csvFilePath)) {
			String line = br.readLine(); // skip header
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",", -1);
				if (fields.length != 5)
					continue;

				// add random role. 20% chance admin, 80% chance user
				Role role = (random.nextDouble() < 0.2) ? Role.ADMIN : Role.USER;

				// Constructor: name, firstName, email, phoneNumber, role, password)
				users.add(new User(fields[0], fields[1], fields[2], fields[3], role, fields[4]));
			}
		}

		return users;
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
	 * @param users     the list of users to create registrations for (admins
	 *                  excluded)
	 * @param festivals the list of festivals to register users to
	 * @param amount    the total number of registrations to create
	 * @return a list of Registration objects
	 */
	private List<Registration> createRegistrations(List<User> users, List<Festival> festivals, int amount) {
		Random random = new Random();
		List<Registration> registrations = new ArrayList<>();
		List<Map.Entry<User, Festival>> allPairs = new ArrayList<>();
		List<User> nonAdminUsers = users.stream().filter(user -> user.getRole() != Role.ADMIN)
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
		for (User user : nonAdminUsers)
			for (Festival festival : festivals)
				allPairs.add(new AbstractMap.SimpleEntry<>(user, festival));

		// Shuffle all possible festival_user pairings
		Collections.shuffle(allPairs, random);

		// Add 'argument: amount' of registrations to the list by iterating over the
		// shuffled list untill the max amount has been added. Adds randomly selected
		// comment, rating between 3 and 5, random dateTime
		for (int i = 0; i < Math.min(amount, allPairs.size()); i++) {
			String comment = comments[random.nextInt(comments.length)];
			int rating = 3 + random.nextInt(3); // 3 to 5
			LocalDateTime regDate = LocalDateTime.now().minusDays(random.nextInt(60) + 1);

			registrations.add(
					new Registration(rating, comment, regDate, allPairs.get(i).getKey(), allPairs.get(i).getValue()));
		}

		return registrations;
	}

	/**
	 * Populates the database using JPA EntityManager and EntityManagerFactory
	 * directly. Creates and persists sample categories, addresses, users,
	 * festivals, vendors, and registrations. Demonstrates a manual population
	 * approach independent of DAOs.
	 */
	public void runEMF() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("festivalPU");
		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();

			// --- Categories ---
			Category foodCategory = new Category("Food", "Food and beverages");
			Category musicCategory = new Category("Music", "Live concerts and DJs");
			em.persist(foodCategory);
			em.persist(musicCategory);

			// --- Addresses ---
			Address venue1 = new Address("Main Square", "USA", "New York", 10001, "Broadway", 1, "A");
			Address venue2 = new Address("City Park", "USA", "Chicago", 60601, "Michigan Ave", 200, null);
			em.persist(venue1);
			em.persist(venue2);

			// --- Users ---
			User user1 = new User("Smith", "John", "john@example.com", "123456789", Role.USER, "password123");
			User user2 = new User("Admin", "Alice", "alice@example.com", "987654321", Role.ADMIN, "adminPass");
			em.persist(user1);
			em.persist(user2);

			// --- Festivals ---
			Festival fest1 = new Festival("Summer Beats", "Music fest", 100, 2000);
			fest1.setStart(LocalDateTime.now().plusDays(30));
			fest1.setEnd(LocalDateTime.now().plusDays(32));
			fest1.setAddress(venue1);
			fest1.setCategory(musicCategory);

			Festival fest2 = new Festival("Food Carnival", "Culinary delights", 50, 1000);
			fest2.setStart(LocalDateTime.now().plusDays(15));
			fest2.setEnd(LocalDateTime.now().plusDays(16));
			fest2.setAddress(venue2);
			fest2.setCategory(foodCategory);

			em.persist(fest1);
			em.persist(fest2);

			// --- Vendors ---
			Vendor vendor1 = new Vendor("Tasty Burgers", "Delicious grilled burgers", "5551234", "burger@example.com",
					"www.burgers.com", "logo1.png", 5);
			vendor1.setCategory(foodCategory);
			vendor1.setAddress(venue1);
			vendor1.getFestivals().add(fest2);
			fest2.getVendors().add(vendor1);

			Vendor vendor2 = new Vendor("DJ Soundz", "Live DJ sets", "5555678", "dj@example.com", "www.djz.com",
					"logo2.png", 4);
			vendor1.setCategory(musicCategory);
			vendor1.setAddress(venue2);
			vendor2.getFestivals().add(fest1);
			fest1.getVendors().add(vendor2);

			em.persist(vendor1);
			em.persist(vendor2);

			// --- Registrations ---
			Registration reg1 = new Registration(new UserFestivalKey(user1.getId(), 1L), 5, "Amazing festival!",
					LocalDateTime.now(), user1, fest1);

			Registration reg2 = new Registration(new UserFestivalKey(2L, 2L), 4, "Great food!", LocalDateTime.now(),
					user2, fest2);

			em.persist(reg1);
			em.persist(reg2);

			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			em.close();
			emf.close();
		}
	}

}
