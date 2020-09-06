package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesHelper;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service("multipleUploadService")
public class MultipleUploadService {

    public static final String ERROR_SHEET_NUMBER_ROWS = "Error: Number of rows expected ";

    public static final String ERROR_SHEET_NUMBER_COLUMNS = "Error: Number of columns expected ";

    public static final String ERROR_SHEET_EMPTY = "Error: Empty sheet";

    private final ExcelReadingService excelReadingService;

    @Autowired
    public MultipleUploadService(ExcelReadingService excelReadingService) {
        this.excelReadingService = excelReadingService;

    }

    public void bulkUploadLogic(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        log.info("Check errors uploading excel");

        try {

            Sheet datatypeSheet = excelReadingService.checkExcelErrors(
                    userToken,
                    MultiplesHelper.getExcelBinaryUrl(multipleDetails),
                    errors);

            validateSheet(
                    datatypeSheet,
                    multipleDetails.getCaseData(),
                    errors);

        } catch (IOException e) {

            log.error("Error reading the Excel");

        }

    }

    private void validateSheet(Sheet datatypeSheet, MultipleData multipleData, List<String> errors) {

        if (datatypeSheet.getRow(0) != null) {

            int collectionSize = multipleData.getCaseIdCollection().size();

            log.info("Case IDs: " + collectionSize);
            log.info("Number of rows: " + datatypeSheet.getLastRowNum());

            if (collectionSize != datatypeSheet.getLastRowNum()) {

                errors.add(ERROR_SHEET_NUMBER_ROWS + collectionSize);

            }

            log.info("Number of columns: " + datatypeSheet.getRow(0).getLastCellNum());

            if (datatypeSheet.getRow(0).getLastCellNum() != MultiplesHelper.HEADERS.size()) {

                errors.add(ERROR_SHEET_NUMBER_COLUMNS + MultiplesHelper.HEADERS.size());

            }

        } else {

            errors.add(ERROR_SHEET_EMPTY);

        }
    }
}
