package repository;

import java.util.List;

import entity.Vendor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VendorDaoJpa extends GenericDaoJpa<Vendor> implements VendorDao {

	public VendorDaoJpa() {
		super(Vendor.class);
		log.info("VendorDaoJpa initialized");
	}

	public List<Vendor> getAllVendors() {
		log.info("Started fetching all vendors");
		try {
			List<Vendor> vendors = em.createNamedQuery("Vendor.findAll", Vendor.class).getResultList();
			log.info("Fetched {} vendors", vendors.size());
			return vendors;
		} catch (NoResultException ex) {
			log.warn("No vendors found", ex);
			throw new EntityNotFoundException("No vendors found");
		}
	}

	@Override
	public Vendor getVendorById(Long id) {
		log.info("Fetching vendor with id {}", id);
		try {
			Vendor vendor = em.createNamedQuery("Vendor.findById", Vendor.class).setParameter("id", id)
					.getSingleResult();
			log.info("Fetched {} vendor", vendor.getId());
			return vendor;
		} catch (NoResultException ex) {
			log.warn("No vendor found");
			throw new EntityNotFoundException("No vendor found");
		}
	}

	@Override
	public void updateVendor(Vendor vendor) {
		log.info("Updating vendor with ID {}", vendor.getId());
		runInTransaction(() -> {
			super.update(vendor);
			log.info("Vendor with ID {} updated", vendor.getId());
		});
	}

	@Override
	public void addVendor(Vendor vendor) {
		log.info("Adding vendor: {}", vendor.getName());
		runInTransaction(() -> {
			super.insert(vendor);
			log.info("Vendor {} added", vendor.getName());
		});
	}

	@Override
	public List<Vendor> getVendorsByIds(List<Long> vendorIds) {
		log.info("Fetching vendors using named query with IDs: {}", vendorIds);
		if (vendorIds == null || vendorIds.isEmpty()) {
			log.warn("No vendor IDs provided for lookup");
			return List.of();
		}

		try {
			return em.createNamedQuery("Vendor.findByIds", Vendor.class).setParameter("ids", vendorIds).getResultList();
		} catch (NoResultException ex) {
			log.warn("No vendors found for provided IDs", ex);
			throw new EntityNotFoundException("No vendors found for the provided IDs");
		}
	}

}
