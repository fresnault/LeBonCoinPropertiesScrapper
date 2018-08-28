package fr.fresnault.properties.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A PropertyPhoto.
 */
@Entity
@Table(name = "property_photo")
public class PropertyPhoto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_seq")
    private Integer photoSeq;

    @Column(name = "photo_thumb_url")
    private String photoThumbUrl;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne
    @JsonIgnoreProperties("photos")
    private Property property;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPhotoSeq() {
        return photoSeq;
    }

    public PropertyPhoto photoSeq(Integer photoSeq) {
        this.photoSeq = photoSeq;
        return this;
    }

    public void setPhotoSeq(Integer photoSeq) {
        this.photoSeq = photoSeq;
    }

    public String getPhotoThumbUrl() {
        return photoThumbUrl;
    }

    public PropertyPhoto photoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
        return this;
    }

    public void setPhotoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public PropertyPhoto photoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Property getProperty() {
        return property;
    }

    public PropertyPhoto property(Property property) {
        this.property = property;
        return this;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertyPhoto propertyPhoto = (PropertyPhoto) o;
        if (propertyPhoto.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), propertyPhoto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PropertyPhoto{" +
            "id=" + getId() +
            ", photoSeq=" + getPhotoSeq() +
            ", photoThumbUrl='" + getPhotoThumbUrl() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            "}";
    }
}
