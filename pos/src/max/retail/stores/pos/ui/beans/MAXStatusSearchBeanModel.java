package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXStatusSearchBeanModel extends POSBaseBeanModel
{
  public static String revisionNumber = "$Revision: 3$";

  protected boolean clearUIFields = true;

  protected boolean emptyDate = true;
  protected EYSDate startDateField = null;
  protected EYSDate endDateField = null;
  protected String statusField = null;

  protected int statusFieldIndex = 0;

  public EYSDate getStartDate()
  {
    EYSDate startDateTime = DomainGateway.getFactory().getEYSDateInstance();
    if (this.startDateField != null)
    {
      startDateTime.initialize(this.startDateField.getYear(), this.startDateField.getMonth(), this.startDateField.getDay(), 0, 0, 0);

      this.startDateField = startDateTime;
    }
    return this.startDateField;
  }

  public EYSDate getEndDate()
  {
    EYSDate endDateTime = DomainGateway.getFactory().getEYSDateInstance();
    if (this.endDateField != null)
    {
      endDateTime.initialize(this.endDateField.getYear(), this.endDateField.getMonth(), this.endDateField.getDay(), 23, 59, 59);

      this.endDateField = endDateTime;
      if (this.startDateField != null)
      {
        setEmptyDate(false);
      }
    }
    return this.endDateField;
  }

  /** @deprecated */
  public String getStatus()
  {
    return this.statusField;
  }

  public int getStatusIndex()
  {
    return this.statusFieldIndex;
  }

  public String[] getOrderStatusDescriptors()
  {
    return OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS;
  }

  public void setStartDate(EYSDate startDate)
  {
    this.startDateField = startDate;
  }

  public void setEndDate(EYSDate EndDate)
  {
    this.endDateField = EndDate;
  }

  /** @deprecated */
  public void setStatus(String status)
  {
    this.statusField = status;
  }

  public void setStatusIndex(int statusIndex)
  {
    this.statusFieldIndex = statusIndex;
  }

  public void setEmptyDate(boolean emptyDate)
  {
    this.emptyDate = emptyDate;
  }

  public boolean getEmptyDate()
  {
    return this.emptyDate;
  }

  public void setclearUIFields(boolean value)
  {
    this.clearUIFields = value;
  }

  public boolean getclearUIFields()
  {
    return this.clearUIFields;
  }

  public String toString()
  {
    StringBuffer buff = new StringBuffer();

    buff.append("Class: MAXStatusSearchBeanModel Revision: " + revisionNumber + "\n");
    return buff.toString();
  }
}