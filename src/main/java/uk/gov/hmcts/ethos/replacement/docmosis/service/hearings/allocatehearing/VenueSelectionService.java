package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.helper.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.VenueService;

import java.util.List;
import java.util.UUID;

@Service
public class VenueSelectionService {
    private final VenueService venueService;

    public VenueSelectionService(VenueService venueService) {
        this.venueService = venueService;
    }

    public void initHearingCollection(CaseData caseData) {
        var venues = venueService.getVenues(TribunalOffice.valueOfOfficeName(caseData.getManagingOffice()));
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            var hearingTypeItem = new HearingTypeItem();
            hearingTypeItem.setId(UUID.randomUUID().toString());
            caseData.setHearingCollection(List.of(hearingTypeItem));

            var hearingType = new HearingType();
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(venues);
            hearingType.setHearingVenue(dynamicFixedListType);
            hearingTypeItem.setValue(hearingType);
        } else {
            for (var hearingItemType : caseData.getHearingCollection()) {
                var hearingType = hearingItemType.getValue();
                var dynamicFixedListType = hearingType.getHearingVenue();
                if (dynamicFixedListType == null) {
                    dynamicFixedListType = new DynamicFixedListType();
                    hearingType.setHearingVenue(dynamicFixedListType);
                }
                dynamicFixedListType.setListItems(venues);
            }
        }
    }

    public DynamicFixedListType createVenueSelection(TribunalOffice tribunalOffice, DateListedType selectedListing) {
        var listItems = venueService.getVenues(tribunalOffice);
        var selectedVenue = selectedListing.getHearingVenueDay();
        return DynamicFixedListType.from(listItems, selectedVenue);
    }
}
