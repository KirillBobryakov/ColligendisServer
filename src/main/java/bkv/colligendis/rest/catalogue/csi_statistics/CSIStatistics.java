package bkv.colligendis.rest.catalogue.csi_statistics;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CSIStatistics {
    private CSICountry country;
    private Integer totalSubjects;
    // private List<CSISubject> subjects = new ArrayList<>(0);
    private Integer totalIssuers;
    // private List<CSIIssuer> issuers = new ArrayList<>(0);
    private Integer totalNTypes;

    private List<CSICurrency> currencies = new ArrayList<>(0);

    public CSIStatistics() {
    }

    public CSIStatistics(CSICountry country, Integer totalSubjects, Integer totalIssuers, Integer totalNTypes) {
        this.country = country;
        this.totalSubjects = totalSubjects;
        this.totalIssuers = totalIssuers;
        this.totalNTypes = totalNTypes;
    }

    public void setCountry(CSICountry country) {
        this.country = country;
    }

    public void setCountry(String name, String numistaCode) {
        this.country = new CSICountry(name, numistaCode);
    }
}
