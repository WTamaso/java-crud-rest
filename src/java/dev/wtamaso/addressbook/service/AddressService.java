package dev.wtamaso.addressbook.service;

import dev.wtamaso.addressbook.dto.AddressDTO;
import dev.wtamaso.addressbook.entities.Address;
import dev.wtamaso.addressbook.util.SessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class AddressService {
    public static Long create(AddressDTO dto) {
        try {
            if (dto.getLatitude() == null || dto.getLongitude() == null) {
                //TODO: Update latitude and longitude
            }

            Address address = new Address();
            AddressDTO.updateEntity(dto, address);

            Session s = SessionFactoryUtil.session();

            s.beginTransaction();
            s.save(address);
            s.getTransaction().commit();

            return address.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Address", e);
        }
    }

    public static Address get(Long id) {
        try {
            Session s = SessionFactoryUtil.session();

            CriteriaBuilder criteriaBuilder = s.getCriteriaBuilder();
            CriteriaQuery<Address> criteriaQuery = criteriaBuilder.createQuery(Address.class);
            Root<Address> root = criteriaQuery.from(Address.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));
            Query<Address> query = s.createQuery(criteriaQuery)
                    .setMaxResults(1);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Address", e);
        }
    }

    public static List<Address> getList() {
        try {
            Session s = SessionFactoryUtil.session();

            CriteriaBuilder criteriaBuilder = s.getCriteriaBuilder();
            CriteriaQuery<Address> criteriaQuery = criteriaBuilder.createQuery(Address.class);
            Root<Address> root = criteriaQuery.from(Address.class);
            criteriaQuery.select(root);

            Query<Address> query;
            query = s.createQuery(criteriaQuery);

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Addresses List", e);
        }
    }

    public static Long update(Address address, AddressDTO dto) {
        try {
            if ((dto.getLatitude() == null || dto.getLongitude() == null) ||
                    (!dto.getStreetname().equals(address.getStreetname()) ||
                            !dto.getNumber().equals(address.getNumber()) ||
                            !dto.getNeighbourhood().equals(address.getNeighbourhood()) ||
                            !dto.getCity().equals(address.getCity()) ||
                            !dto.getState().equals(address.getState()) ||
                            !dto.getCountry().equals(address.getCountry()) ||
                            !dto.getZipCode().equals(address.getZipCode())
                    )) {
                //TODO: Update latitude and longitude
            }

            Session s = SessionFactoryUtil.session();

            s.beginTransaction();
            AddressDTO.updateEntity(dto, address);
            s.update(address);
            s.getTransaction().commit();

            return address.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Address", e);
        }
    }

    public static void delete(Address address) {
        try {
            Session s = SessionFactoryUtil.session();

            s.beginTransaction();
            s.delete(address);
            s.getTransaction().commit();

            return;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Address", e);
        }
    }

    public static boolean validate(AddressDTO dto) {
        return
                dto.getStreetname() != null && !dto.getStreetname().trim().isEmpty() &&
                        dto.getNumber() != null &&
                        dto.getNeighbourhood() != null && !dto.getNeighbourhood().trim().isEmpty() &&
                        dto.getCity() != null && !dto.getCity().trim().isEmpty() &&
                        dto.getState() != null && !dto.getState().trim().isEmpty() &&
                        dto.getCountry() != null && !dto.getCountry().trim().isEmpty() &&
                        dto.getZipCode() != null && !dto.getZipCode().trim().isEmpty();
    }
}
