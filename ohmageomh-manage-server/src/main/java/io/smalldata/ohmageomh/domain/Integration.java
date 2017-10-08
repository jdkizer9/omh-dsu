package io.smalldata.ohmageomh.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Integration.
 */
@Entity
@Table(name = "integration")
@Document(indexName = "integration")
public class Integration extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany
    @JoinTable(name = "integration_data_type",
               joinColumns = @JoinColumn(name="integrations_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="data_types_id", referencedColumnName="id"))
    private Set<DataType> dataTypes = new HashSet<>();

    @ManyToMany(mappedBy = "integrations")
    @JsonIgnore
    private Set<Study> studies = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DataType> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Set<DataType> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public Set<Study> getStudies() {
        return studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
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
        Integration integration = (Integration) o;
        if (integration.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), integration.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Integration{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
