package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ENGLANDWALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ethos.replacement.docmosis.utils.InternalException.ERROR_MESSAGE;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseCreationForCaseWorkerServiceTest {

    @InjectMocks
    private CaseCreationForCaseWorkerService caseCreationForCaseWorkerService;
    @Mock
    private CcdClient ccdClient;
    private CCDRequest ccdRequest;
    private SubmitEvent submitEvent;
    private String authToken;
    @Mock
    private SingleReferenceService singleReferenceService;
    @Mock
    private MultipleReferenceService multipleReferenceService;


    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        CaseDetails caseDetails = new CaseDetails();
        CaseData caseData = MultipleUtil.getCaseData("2123456/2020");
        caseData.setCaseRefNumberCount("2");
        caseData.setPositionTypeCT("PositionTypeCT");
        DynamicFixedListType officeCT = new DynamicFixedListType();
        DynamicValueType valueType = new DynamicValueType();
        valueType.setCode(ENGLANDWALES_CASE_TYPE_ID);
        officeCT.setValue(valueType);
        caseData.setOfficeCT(officeCT);
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseTypeId("Manchester");
        caseDetails.setJurisdiction("Employment");
        caseDetails.setState(ACCEPTED_STATE);
        ccdRequest.setCaseDetails(caseDetails);
        submitEvent = new SubmitEvent();
        authToken = "authToken";
    }

    @Test(expected = Exception.class)
    public void caseCreationRequestException() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenThrow(new InternalException(ERROR_MESSAGE));
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, authToken);
    }

    @Test
    public void caseCreationRequest() throws IOException {
        when(ccdClient.startCaseCreation(anyString(), any())).thenReturn(ccdRequest);
        when(ccdClient.submitCaseCreation(anyString(), any(), any())).thenReturn(submitEvent);
        SubmitEvent submitEvent1 = caseCreationForCaseWorkerService.caseCreationRequest(ccdRequest, authToken);
        assertEquals(submitEvent1, submitEvent);
    }

    @Test
    public void generateCaseRefNumbers() {
        when(singleReferenceService.createReference("Manchester",2)).thenReturn("2100001/2019");
        when(multipleReferenceService.createReference("Manchester_Multiple",1)).thenReturn("2100005");
        CaseData caseData = caseCreationForCaseWorkerService.generateCaseRefNumbers(ccdRequest);
        assertEquals("2100001/2019", caseData.getStartCaseRefNumber());
        assertEquals("2", caseData.getCaseRefNumberCount());
        assertEquals("2100005", caseData.getMultipleRefNumber());
    }



}