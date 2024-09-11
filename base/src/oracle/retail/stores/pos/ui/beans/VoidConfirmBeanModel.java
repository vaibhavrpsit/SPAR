/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/VoidConfirmBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse   
 *
 *  Revision 1.2  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 15 2004 17:22:52   epd
 * Removed Date field
 * 
 *    Rev 1.0   Aug 29 2003 16:13:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:19:10   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:40   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

// foundation imports

//---------------------------------------------------------------------
/**
 * This is the bean model for the VoidConfirmBean. <P>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.ui.beans.VoidConfirmBean
 */
//---------------------------------------------------------------------
public class VoidConfirmBeanModel extends DecimalWithReasonBeanModel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Transaction Number **/
    protected String fieldTransactionNumber = "";
    /** Date
     * @deprecated since 7.0 (Date no longer used) 
     **/
    protected String strDate = "";
    /** Amount**/
    protected String strAmount = "";
    /** Transaction type **/
    protected String fieldTransactionType = "";

    //---------------------------------------------------------------------
    /**
    * VoidConfirmBeanModel constructor comment.
    */
    //---------------------------------------------------------------------
    public VoidConfirmBeanModel()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
    * Gets the amount property (BigDecimal) value.
    * @return The amount property value.
    * @see #setAmount
    * @deprecated as of release 5.5 replace by setAmountString()
    */
    //---------------------------------------------------------------------
    public BigDecimal getAmount()
    {
        return getValue();
    }
    //---------------------------------------------------------------------
    /**
    * Gets the amount property (BigDecimal) value.
    * @return The amount property value.
    * @see #setAmountString
    */
    //---------------------------------------------------------------------    
    public String getAmountString()
    {
        return strAmount;
    }
    //---------------------------------------------------------------------
    /**
    * Gets the transaction type (java.lang.String) value.
    * @return String transaction type
    */
    //---------------------------------------------------------------------
    public String getTransactionType()
    {
        return fieldTransactionType;
    }

    //---------------------------------------------------------------------
    /**
    * Gets the date property (java.util.Date) value.
    * @return The date property value.
    * @see #setDate
    * @deprecated since 7.0 (Field no longer displayed)
    */
    //---------------------------------------------------------------------
    public String getDateString()
    {
        return strDate;
    }
    //---------------------------------------------------------------------
    /**
    * Gets the transactionNumber property (java.lang.String) value.
    * @return The transactionNumber property value.
    * @see #setTransactionNumber
    */
    //---------------------------------------------------------------------
    public String getTransactionNumber()
    {
        return fieldTransactionNumber;
    }

    //---------------------------------------------------------------------
    /**
    * Sets the amount property (BigDecimal) value.
    * @param amount The new value for the property.
    * @see #getAmount
    */
    //---------------------------------------------------------------------
    public void setAmountString(String amount)
    {
        strAmount = amount;
    }
    //---------------------------------------------------------------------
    /**
    * Sets the transaction type (java.lang.String) value.
    * @param transactionType The new value for the property.
    * @see #getTransactionType
    */
    //---------------------------------------------------------------------
    public void setTransactionType(String transactionType)
    {
        fieldTransactionType = transactionType;
    }

   //---------------------------------------------------------------------
    /**
    * Sets the date property (java.lang.Date) value.
    * @param date The new string value for the property.
    * @see #getDateString
    * @deprecated since 7.0 Field no longer displayed
    */
    //---------------------------------------------------------------------
    public void setDateString(String date)
    {
       strDate =  date;
    }
    
    //---------------------------------------------------------------------
    /**
    * Sets the transactionNumber property (java.lang.String) value.
    * @param transactionNumber The new value for the property.
    * @see #getTransactionNumber
    */
    //---------------------------------------------------------------------
    public void setTransactionNumber(String transactionNumber)
    {
        fieldTransactionNumber = transactionNumber;
    }
}
