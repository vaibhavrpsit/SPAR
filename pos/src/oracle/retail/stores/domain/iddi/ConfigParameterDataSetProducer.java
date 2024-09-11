package oracle.retail.stores.domain.iddi;

import oracle.retail.stores.foundation.iddi.AbstractDataSetProducer;
import oracle.retail.stores.foundation.iddi.TableQueryInfo;

public class ConfigParameterDataSetProducer extends AbstractDataSetProducer {
  private final String[] TABLE_FIELDS = new String[] { "*" };
  
  public void initializeDataSet() {
    getDataSetMetaData();
  }
  
  public TableQueryInfo getTableQueryInfo(String tableName) {
    TableQueryInfo tableQueryInfo = new TableQueryInfo(tableName);
    tableQueryInfo.setTableFields(this.TABLE_FIELDS);
    return tableQueryInfo;
  }
  
  public String getDataSetKey() {
    return this.dataSetKey;
  }
  
  public void setDataSetKey(String dataSetKey) {
    this.dataSetKey = dataSetKey;
  }
}
