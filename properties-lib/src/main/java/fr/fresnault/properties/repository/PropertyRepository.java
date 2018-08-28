package fr.fresnault.properties.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.fresnault.properties.domain.Property;
import fr.fresnault.properties.domain.enumeration.Source;

/**
 * Spring Data repository for the Property entity.
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

	boolean existsByRefSourceAndRefId(Source refSource, String refId);

	Property findByRefSourceAndRefId(Source refSource, String refId);

}
