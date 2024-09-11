package oracle.retail.stores.domain.iddi;

import oracle.retail.stores.foundation.iddi.AbstractDataSetConsumer;
import oracle.retail.stores.foundation.iddi.ifc.OfflineDBHelperIfc;

public class ConfigParameterDataSetConsumer extends AbstractDataSetConsumer {
  private String dataSetKey = null;
  
  public String getDataSetKey() {
    return this.dataSetKey;
  }
  
  public void setDataSetKey(String dataSetKey) {
    this.dataSetKey = dataSetKey;
  }
  
  public String getDataImportFilePath() {
    return this.dataImportZipFilePath;
  }
  
  public void setDataImportFilePath(String dataImportFilePath) {
    this.dataImportZipFilePath = dataImportFilePath;
  }
  
  public OfflineDBHelperIfc getImportHelper() {
    return this.importHelper;
  }
  
  public void setImportHelper(OfflineDBHelperIfc importHelper) {
    this.importHelper = importHelper;
  }
}
