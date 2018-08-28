package fr.fresnault.properties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.fresnault.properties.domain.City;

/**
 * Spring Data repository for the City entity.
 */
@Repository
public interface CityRepository extends JpaRepository<City, Long> {

	City findByNameAndZipCode(String name, String zipCode);

}
