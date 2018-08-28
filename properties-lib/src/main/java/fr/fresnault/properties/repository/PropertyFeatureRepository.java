package fr.fresnault.properties.repository;

import fr.fresnault.properties.domain.PropertyFeature;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PropertyFeature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyFeatureRepository extends JpaRepository<PropertyFeature, Long> {

}
