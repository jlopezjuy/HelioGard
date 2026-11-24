package ar.com.heliguard.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ar.com.heliguard.app.domain.TariffPlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TariffPlanDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 140)
    private String name;

    @NotNull
    @Size(max = 3)
    private String currencyCode;

    @Size(max = 160)
    private String distributor;

    @NotNull
    private OrganizationDTO organization;

    @NotNull
    private InstallationDTO installation;

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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public InstallationDTO getInstallation() {
        return installation;
    }

    public void setInstallation(InstallationDTO installation) {
        this.installation = installation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TariffPlanDTO)) {
            return false;
        }

        TariffPlanDTO tariffPlanDTO = (TariffPlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tariffPlanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TariffPlanDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", currencyCode='" + getCurrencyCode() + "'" +
            ", distributor='" + getDistributor() + "'" +
            ", organization=" + getOrganization() +
            ", installation=" + getInstallation() +
            "}";
    }
}
