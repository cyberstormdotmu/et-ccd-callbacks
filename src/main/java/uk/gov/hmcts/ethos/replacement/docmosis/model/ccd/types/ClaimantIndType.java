package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ClaimantIndType {

    @JsonProperty("claimant_title1")
    private String claimantTitle;
    @JsonProperty("claimant_title_other")
    private String claimantTitleOther;
    @JsonProperty("claimant_forenames")
    private String claimantForenames;
    @JsonProperty("claimant_surname")
    private String claimantSurname;
    @JsonProperty("claimant_date_of_birth")
    private String claimantDateOfBirth;
    @JsonProperty("claimant_gender")
    private String claimantGender;

    public String claimantFullName() {
        return String.join(" ", notNullOrEmptyAtt(new ArrayList<>(), Arrays.asList(claimantTitle,
                claimantTitleOther, getInitials(), claimantSurname)));
    }

    private String getInitials() {
        if (!isNullOrEmpty(claimantForenames)) {
            return Arrays.stream(claimantForenames.split(" ")).map(str -> str.substring(0, 1)).collect(Collectors.joining(" "));
        }
        return "";
    }

    private List<String> notNullOrEmptyAtt(List<String> fullClaimantName, List<String> attributes) {
        for (String aux : attributes) {
            if (!isNullOrEmpty(aux)) fullClaimantName.add(aux);
        }
        return fullClaimantName;
    }
}