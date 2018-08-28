package fr.fresnault.properties.repository;

import fr.fresnault.properties.domain.PropertyPhoto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the PropertyPhoto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {

}
