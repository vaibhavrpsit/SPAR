/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/



package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.Vector;

import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

public class MAXSelectCustomerTypeBeanModel extends ReasonBeanModel
/*     */ {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   protected static final String revisionNumber = "$Revision: /main/14 $";
   protected BigDecimal fieldValue = null;
   protected Vector customerType;

   public Vector getCustomerType() {
	return customerType;
}

public void setCustomerType(Vector customerType) {
	this.customerType = customerType;
}

}

//protected boolean booleanValue = false;




/*public MAXSelectCustomerTypeBeanModel()
{}

  public BigDecimal getValue()
   {
     return this.fieldValue;
   }

   public void setValue(BigDecimal value)
   {
     this.fieldValue = value;
  }*/
 
