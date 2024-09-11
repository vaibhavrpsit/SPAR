package max.retail.stores.exportfile.rtlog.fieldmappers;

import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.exportfile.ExportFileException;
import oracle.retail.stores.exportfile.formater.FieldFormatIfc;
import oracle.retail.stores.exportfile.formater.RecordFormatIfc;
import oracle.retail.stores.exportfile.mapper.ColumnMapIfc;
import oracle.retail.stores.exportfile.mapper.EntityMapperIfc;
import oracle.retail.stores.exportfile.mapper.FieldMapper;
import oracle.retail.stores.exportfile.mapper.FieldMapperIfc;
import oracle.retail.stores.xmlreplication.extractor.ReplicationExportException;
import oracle.retail.stores.xmlreplication.result.EntityIfc;
import oracle.retail.stores.xmlreplication.result.Row;

public class MAXTransactionNumberMapper extends FieldMapper implements FieldMapperIfc {
  public static final int TRANSACTION_SEQUENCE_LENGTH = 7;
  
  public int map(String columnValue, Row row, ColumnMapIfc columnMap, FieldFormatIfc field, RecordFormatIfc record, EntityIfc entity, EntityMapperIfc entityMapper) throws ExportFileException {
    StringBuilder transactionNumber = new StringBuilder("");
    try {
      transactionNumber.append(removeLeadingZeros((String)row.getFieldValue("ID_STR_RT")));
      String workstationID = row.getFieldValue("ID_WS").toString();
      transactionNumber.append(removeLeadingZeros(workstationID));
      transactionNumber.append(StringUtils.leftPad(row.getFieldValue("AI_TRN").toString(), 7, "0"));
    } catch (ReplicationExportException re) {
      throw new ExportFileException("ReplicationExportException when generating TransactionNumber" + re.getMessage(), re);
    } 
    field.setValue(transactionNumber.toString());
    return 1;
  }
  
  private String removeLeadingZeros(String storeID) {
    long numericStoreID;
    if (storeID == null)
      return storeID; 
    try {
      numericStoreID = Long.parseLong(storeID);
    } catch (NumberFormatException ex) {
      return storeID;
    } 
    storeID = String.valueOf(numericStoreID);
    return storeID;
  }
}
