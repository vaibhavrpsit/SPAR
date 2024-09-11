package max.retail.stores.pos.ui.beans;



import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
   Transports data from the busness logic to the SummaryCountBean. <P>
   @version $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXCouponDenominationCountBeanModel implements Serializable
{                                       // begin class SummaryCountBeanModel
  // This id is used to tell
  // the compiler not to generate a
  // new serialVersionUID.
  //
  static final long serialVersionUID = -4570778420861477590L;
  
 

  /**
      revision number supplied by source-code-control system
  **/
  public static String revisionNumber = "$Revision: 4$";
  /**
      the Description of the current tender
  **/
  protected String description = "";
  /**
      the field label
  **/
  protected String label = "";
  /**
      the field label tag
  **/
  protected String labelTag = "";
  /**
      This field is the mapping between tender types and button action
      as defined in tenderuicfg.xml for associated button
  **/
  
  
  
  protected CurrencyIfc amount = null;
  /**
      Negative allowed.
  **/
  protected int quantity = 0;
  /**
      flag to determine whether the entry field is disabled.
  **/
  protected boolean fieldDisabled = true;
  /**
      flag to determine whether the entry field is hidden.
  **/
  protected boolean fieldHidden = false;
  /**
      The amount expected to be in the amount field.
  **/
  protected CurrencyIfc totalAmount = null;
  
  //----------------------------------------------------------------------
  /**
          Constructs SummaryCountBeanModel object.
  **/
  //----------------------------------------------------------------------
  public MAXCouponDenominationCountBeanModel()
  {                                   // begin SummaryCountBeanModel()
  }                                   // end SummaryCountBeanModel()

  //----------------------------------------------------------------------
  /**
      Creates clone of this object. <P>
      @return Object clone of this object
  **/
  //----------------------------------------------------------------------
  public Object clone()
  {                                   // begin clone()
      // instantiate new object
      MAXCouponDenominationCountBeanModel c = new MAXCouponDenominationCountBeanModel();
      // set values
      if (description != null)
      {
          c.setDescription(new String(description));
      }
      if (label != null)
      {
          c.setLabel(new String(label));
      }
      if (amount != null)
      {
          c.setAmount((CurrencyIfc) amount.clone());
      }
      c.setFieldDisabled(fieldDisabled);
      c.setFieldHidden(fieldHidden);
      // pass back Object
      return((Object) c);
  }                                   // end clone()


  //----------------------------------------------------------------------
  /**
      Determines if two objects are identical. <P>
      @param obj object to compare with
      @return true if the objects are identical, false otherwise
  **/
  //----------------------------------------------------------------------
  public boolean equals(Object obj)
  {                                   // begin equals()
      boolean isEqual = true;
      MAXCouponDenominationCountBeanModel c = (MAXCouponDenominationCountBeanModel) obj;          // downcast the input object
      // compare all the attributes of SummaryCountBeanModel
      if (Util.isObjectEqual(description, c.getDescription())
          && Util.isObjectEqual(amount, c.getAmount()))
      {
          isEqual = true;             // set the return code to true
      }
      else
      {
          isEqual = false;            // set the return code to false
      }
      return(isEqual);
  }                                   // end equals()

  //----------------------------------------------------------------------
  /**
      Returns the currency description. <P>
      @return the currency description
  **/
  //----------------------------------------------------------------------
  public String getDescription()
  {                                   // begin getdescription()
      return(description);
  }                                   // end getdescription()

  //----------------------------------------------------------------------
  /**
      Sets the currency description. <P>
      @param value  the currency description
  **/
  //----------------------------------------------------------------------
  public void setDescription(String value)
  {                                   // begin setdescription()
      description = value;
  }                                   // end setdescription()

  //----------------------------------------------------------------------
  /**
      Returns the text in the prompt area. <P>
      @return the text in the prompt area
  **/
  //----------------------------------------------------------------------
  public String getLabel()
  {                                   // begin getLabel()
      return(label);

  }                                   // end getLabel()

  //----------------------------------------------------------------------
  /**
      Sets the text in the prompt area. <P>
      @param value  the text in the prompt area
  **/
  //----------------------------------------------------------------------
  public void setLabel(String value)
  {                                   // begin setLabel()
      label = value;
  }                                   // end setLabel()

  //----------------------------------------------------------------------
  /**
      Returns the label tag. <P>
      @return label tag
  **/
  //----------------------------------------------------------------------
  public String getLabelTag()
  {                                   // begin getLabelTag()
      return(labelTag);

  }                                   // end getLabelTag()

  //----------------------------------------------------------------------
  /**
      Sets the label tag. <P>
      @param value  label tag
  **/
  //----------------------------------------------------------------------
  public void setLabelTag(String value)
  {                                   // begin setLabelTag()
      labelTag = value;
  }                                   // end setLabelTag()

  //----------------------------------------------------------------------

  //----------------------------------------------------------------------
  /**
      Returns the amount in the input area. <P>
      @return the amount in the input area.
  **/
  //----------------------------------------------------------------------
  public CurrencyIfc getAmount()
  {                                   // begin getamount()
      return(amount);
  }                                   // end getamount()

  //----------------------------------------------------------------------
  /**
      Sets the amount in the input area. <P>
      @param value  the amount in the input area.
  **/
  //----------------------------------------------------------------------
  public void setAmount(CurrencyIfc value)
  {                                   // begin setamount()
      amount = value;
  }                                   // end setamount()


  //----------------------------------------------------------------------
  /**
      Is the field disabled. <P>
      @return true if disabled.
  **/
  //----------------------------------------------------------------------
  public boolean isFieldDisabled()
  {                                   // begin isFieldDisabled()
      return(fieldDisabled);
  }                                   // end isFieldDisabled()

  //----------------------------------------------------------------------
  /**
      Sets the field status. <P>
  **/
  //----------------------------------------------------------------------
  public void setFieldDisabled(boolean value)
  {                                   // begin setFieldDisabled()
      fieldDisabled = value;
  }                                   // end setFieldDisabled()

  //----------------------------------------------------------------------
  /**
      Is the field Hidden. <P>
      @return true if Hidden.
  **/
  //----------------------------------------------------------------------
  public boolean isFieldHidden()
  {                                   // begin isFieldHidden()
      return(fieldHidden);
  }                                   // end isFieldHidden()

  //----------------------------------------------------------------------
  /**
      Sets the field display status. <P>
  **/
  //----------------------------------------------------------------------
  public void setFieldHidden(boolean value)
  {                                   // begin setFieldHidden()
      fieldHidden = value;
  }                                   // end setFieldHidden()




public int getQuantity() {
	return quantity;
}

public void setQuantity(int quantity) {
	this.quantity = quantity;
}

public CurrencyIfc getTotalAmount() {
	return totalAmount;
}

public void setTotalAmount(CurrencyIfc totalAmount) {
	this.totalAmount = totalAmount;
}

//----------------------------------------------------------------------
  /**
      Returns default display string. <P>
      @return String representation of object
  **/
  //----------------------------------------------------------------------
  public String toString()
  {                                   // begin toString()
      // build result string
      String strResult = new String("Class:  SummaryCountBeanModel (Revision " +
                                    getRevisionNumber() +
                                    ") @" +
                                    hashCode());
      // add attributes to string
      strResult += "\ndescription:               [" + description + "]";
      if (amount == null)
      {
          strResult += "\namount:                     [null]";
      }
      else
      {
          strResult += "\namount:                     [" + amount.toString() + "]";
      }
      // pass back result
      return(strResult);
  }                                   // end toString()

  //---------------------------------------------------------------------
  /**
      Retrieves the source-code-control system revision number. <P>
      @return String representation of revision number
  **/
  //---------------------------------------------------------------------
  public String getRevisionNumber()
  {                                   // begin getRevisionNumber()
      // return string
      return(revisionNumber);
  }                                   // end getRevisionNumber()

  //---------------------------------------------------------------------
  /**
      SummaryCountBeanModel main method. <P>
      @param String args[]  command-line parameters
  **/
  //---------------------------------------------------------------------
  public static void main(String args[])
  {                                   // begin main()
      // instantiate class
      MAXCouponDenominationCountBeanModel c = new MAXCouponDenominationCountBeanModel();
      // output toString()
      System.out.println(c.toString());
  }                                   // end main()
}                                       // end class SummaryCountBeanModel
