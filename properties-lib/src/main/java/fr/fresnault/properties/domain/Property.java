package fr.fresnault.properties.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.fresnault.properties.domain.enumeration.Source;
import fr.fresnault.properties.domain.enumeration.Transaction;
import fr.fresnault.properties.domain.enumeration.Type;

/**
 * A Property.
 */
@Entity
@Table(name = "property")
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;

	public Property() {
		// default constructor
	}

	public Property(Source refSource, String refId, String link) {
		this.refSource = refSource;
		this.refId = refId;
		this.url = link;
		this.createdDate = Instant.now();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "ref_source", nullable = false)
	private Source refSource;

	@NotNull
	@Column(name = "ref_id", nullable = false)
	private String refId;

	@Column(name = "name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "jhi_transaction", nullable = false)
	private Transaction transaction;

	@Enumerated(EnumType.STRING)
	@Column(name = "jhi_type", nullable = false)
	private Type type;

	@Lob
	@Column(name = "description")
	private String description;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private Instant createdDate;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price;

	@Column(name = "room_count")
	private Integer roomCount;

	@Column(name = "bedroom_count")
	private Integer bedroomCount;

	@Column(name = "living_area", precision = 10, scale = 2)
	private BigDecimal livingArea;

	@Column(name = "surface_area", precision = 10, scale = 2)
	private BigDecimal surfaceArea;

	@NotNull
	@Column(name = "url", nullable = false)
	private String url;

	@OneToMany(mappedBy = "property")
	private Set<PropertyFeature> features = new HashSet<>();

	@OneToMany(mappedBy = "property")
	private Set<PropertyPhoto> photos = new HashSet<>();

	@ManyToOne
	@JsonIgnoreProperties("properties")
	private City city;

	// jhipster-needle-entity-add-field - JHipster will add fields here, do not
	// remove
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Source getRefSource() {
		return refSource;
	}

	public Property refSource(Source refSource) {
		this.refSource = refSource;
		return this;
	}

	public void setRefSource(Source refSource) {
		this.refSource = refSource;
	}

	public String getRefId() {
		return refId;
	}

	public Property refId(String refId) {
		this.refId = refId;
		return this;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getName() {
		return name;
	}

	public Property name(String name) {
		this.name = name;
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public Property transaction(Transaction transaction) {
		this.transaction = transaction;
		return this;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Type getType() {
		return type;
	}

	public Property type(Type type) {
		this.type = type;
		return this;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public Property description(String description) {
		this.description = description;
		return this;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public Property createdDate(Instant createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Property price(BigDecimal price) {
		this.price = price;
		return this;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public Property roomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public void setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
	}

	public Integer getBedroomCount() {
		return bedroomCount;
	}

	public Property bedroomCount(Integer bedroomCount) {
		this.bedroomCount = bedroomCount;
		return this;
	}

	public void setBedroomCount(Integer bedroomCount) {
		this.bedroomCount = bedroomCount;
	}

	public BigDecimal getLivingArea() {
		return livingArea;
	}

	public Property livingArea(BigDecimal livingArea) {
		this.livingArea = livingArea;
		return this;
	}

	public void setLivingArea(BigDecimal livingArea) {
		this.livingArea = livingArea;
	}

	public BigDecimal getSurfaceArea() {
		return surfaceArea;
	}

	public Property surfaceArea(BigDecimal surfaceArea) {
		this.surfaceArea = surfaceArea;
		return this;
	}

	public void setSurfaceArea(BigDecimal surfaceArea) {
		this.surfaceArea = surfaceArea;
	}

	public String getUrl() {
		return url;
	}

	public Property url(String url) {
		this.url = url;
		return this;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<PropertyFeature> getFeatures() {
		return features;
	}

	public Property features(Set<PropertyFeature> propertyFeatures) {
		this.features = propertyFeatures;
		return this;
	}

	public Property addFeature(PropertyFeature propertyFeature) {
		this.features.add(propertyFeature);
		propertyFeature.setProperty(this);
		return this;
	}

	public Property removeFeature(PropertyFeature propertyFeature) {
		this.features.remove(propertyFeature);
		propertyFeature.setProperty(null);
		return this;
	}

	public void setFeatures(Set<PropertyFeature> propertyFeatures) {
		this.features = propertyFeatures;
	}

	public Set<PropertyPhoto> getPhotos() {
		return photos;
	}

	public Property photos(Set<PropertyPhoto> propertyPhotos) {
		this.photos = propertyPhotos;
		return this;
	}

	public Property addPhoto(PropertyPhoto propertyPhoto) {
		this.photos.add(propertyPhoto);
		propertyPhoto.setProperty(this);
		return this;
	}

	public Property removePhoto(PropertyPhoto propertyPhoto) {
		this.photos.remove(propertyPhoto);
		propertyPhoto.setProperty(null);
		return this;
	}

	public void setPhotos(Set<PropertyPhoto> propertyPhotos) {
		this.photos = propertyPhotos;
	}

	public City getCity() {
		return city;
	}

	public Property city(City city) {
		this.city = city;
		return this;
	}

	public void setCity(City city) {
		this.city = city;
	}
	// jhipster-needle-entity-add-getters-setters - JHipster will add getters
	// and setters here, do not remove

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Property property = (Property) o;
		if (property.getId() == null || getId() == null) {
			return false;
		}
		return Objects.equals(getId(), property.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		return "Property{" + "id=" + getId() + ", refSource='" + getRefSource() + "'" + ", refId='" + getRefId() + "'"
				+ ", name='" + getName() + "'" + ", transaction='" + getTransaction() + "'" + ", type='" + getType()
				+ "'" + ", description='" + getDescription() + "'" + ", createdDate='" + getCreatedDate() + "'"
				+ ", price=" + getPrice() + ", roomCount=" + getRoomCount() + ", bedroomCount=" + getBedroomCount()
				+ ", livingArea=" + getLivingArea() + ", surfaceArea=" + getSurfaceArea() + ", url='" + getUrl() + "'"
				+ "}";
	}
}
