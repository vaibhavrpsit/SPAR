/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OfflinePaymentBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   09 Jul 2003 23:19:20   baa
 * modify screen to get customer name
 * 
 *    Rev 1.1   Aug 14 2002 18:18:04   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//---------------------------------------------------------------------
/**
    This model is used by Offline Payment Bean.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
    @see oracle.retail.stores.pos.ui.beans.OfflinePaymentBean
 */
//---------------------------------------------------------------------
public class OfflinePaymentBeanModel extends POSBaseBeanModel
{
    /** Revision number supplied by source-code control system **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** Layaway Number */
    protected String layawayNumber; 
      
    /** Experation Date */    
    protected EYSDate expirationDate;
    
    /** Balance Due */
    protected CurrencyIfc balanceDue; 
             
    /** PaymentAmount */
    protected CurrencyIfc paymentAmount; 
    
    /** @deprecated as of release 6.0 replaced by customer name*/
    protected String firstName; 
    
    /** @deprecated as of release 6.0 replaced by customer name*/
    protected String lastName; 

    /** Customer name* */
    protected String customerName;     
    
    /** First Run through screen */
    protected boolean firstRun = true; 
    
    //---------------------------------------------------------------------
    /**
        OfflinePaymentBeanModel constructor.
     */
    //---------------------------------------------------------------------
    public OfflinePaymentBeanModel() 
    {
        super();
    }
    
    //---------------------------------------------------------------------
    /**
        Resets the values in the model.        
     */
    //---------------------------------------------------------------------
    public void resetModel() 
    {
        layawayNumber = null;
        expirationDate = null;
        balanceDue = null;
        paymentAmount = null;
        firstName = null;
        lastName = null;
        firstRun = true;
        StatusBeanModel sbModel = new StatusBeanModel();
        sbModel.setCustomerName(" ");
        setStatusBeanModel( sbModel );        
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the layawayNumber value.
        @return The layawayNumber
     */
    //---------------------------------------------------------------------
    public String getLayawayNumber() 
    {
        return layawayNumber;
    }
    //---------------------------------------------------------------------
    /**
        Sets the layawayNumber value.
        @param String layawayNumber
     */
    //---------------------------------------------------------------------
    public void setLayawayNumber(String value) 
    {
        this.layawayNumber = value;
    }
        
    
    //---------------------------------------------------------------------
    /**
        Gets the expiration date value.
        @return The expirationDate.
     */
    //---------------------------------------------------------------------
    public EYSDate getExpirationDate() 
    {
        return expirationDate;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the expiration date value.
     * @param expirationDate The new value for the expirationDate.
     */
    //--------------------------------------------------------------------- 
    public void setExpirationDate(EYSDate date) 
    {
        expirationDate = date;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the balance due value.
        @return The balanceDue.
     */
    //--------------------------------------------------------------------- 
    public CurrencyIfc getBalanceDue() 
    {
        return balanceDue;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the balance due value.
     * @param balanceDue The new value for the balanceDue.
     */
    //--------------------------------------------------------------------- 
    public void setBalanceDue(CurrencyIfc balance) 
    {
        balanceDue = balance;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the amount paid value.
        @return The amountPaid.
    **/
    //--------------------------------------------------------------------- 
    public CurrencyIfc getPaymentAmount() 
    {
        return paymentAmount;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the amount paid value.
     * @param amountPaid The new value for the amountPaid.
     */
    //--------------------------------------------------------------------- 
    public void setPaymentAmount(CurrencyIfc paid) 
    {
        this.paymentAmount = paid;
    }
    
    //---------------------------------------------------------------------
    /**
        Gets the first Name.
        @return String firstName
    **/
    //--------------------------------------------------------------------- 
    public String getFirstName() 
    {
        return firstName;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the first name.
     * @param String FirstName
     */
    //--------------------------------------------------------------------- 
    public void setFirstName(String value) 
    {
        this.firstName = value;
    } 
    
    //---------------------------------------------------------------------
    /**
        Gets the last name.
        @return String lastName
    **/
    //--------------------------------------------------------------------- 
    public String getLastName() 
    {
        return lastName;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the last name.
     * @param String lastName
     */
    //--------------------------------------------------------------------- 
    public void setLastName(String value) 
    {
        this.lastName = value;
    } 
    
    //---------------------------------------------------------------------
    /**
        Is it the first run .
        @return boolean firstRun
    **/
    //--------------------------------------------------------------------- 
    public boolean isFirstRun() 
    {
        return firstRun;
    }
    
    //---------------------------------------------------------------------
    /**
     * Sets the first Run.
     * @param boolean firstRun
     */
    //--------------------------------------------------------------------- 
    public void setFirstRun(boolean value) 
    {
        this.firstRun = value;
    }
    
    //--------------------------------------------------------------------- 
    /**
     * Retrieves the customer name
     * @return String the customer name
     */
    //---------------------------------------------------------------------
    public String getCustomerName()
    {
        return customerName;
    }
    //---------------------------------------------------------------------
    /**
     * Sets the customer name
     * @param name  the customer name
     */
    //---------------------------------------------------------------------
    public void setCustomerName(String name)
    {
        customerName = name;
    }    
    //---------------------------------------------------------------------
    /**
        Return string representation of the OfflinePaymentBeanModel values.
        <P>
        @return OfflinePaymentBeanModel values as a string
    **/
    //--------------------------------------------------------------------- 
    
    public String toString() 
    {
        StringBuffer buf=new StringBuffer("OfflinePaymentBeanModel{");
        if ( getLayawayNumber() != null )
            buf.append("\n LayawayNumber = ").append(getLayawayNumber()).append(",");
        if ( getExpirationDate() != null )
            buf.append("\n Expiration Date = ").append(getExpirationDate().toFormattedString()).append(",");
        if ( getBalanceDue() != null )            
            buf.append("\n Balance Due = ").append(getBalanceDue().toFormattedString()).append(",");
        if ( getPaymentAmount() != null )
            buf.append("\n Payment Amount = ").append(getPaymentAmount().toFormattedString()).append(",");
        if ( getFirstName() != null )
            buf.append("\n First Name = ").append(getFirstName()).append(",");
        if ( getFirstName() != null )
            buf.append("\n Customer Name = ").append(getCustomerName()).append(",");            
        if ( getLastName() != null )
            buf.append("\n Last Name = ").append(getLastName()).append(",");buf.append("}\n");
        
            buf.append("\n First Run = ").append(isFirstRun()).append(",");buf.append("}\n");
        return buf.toString();
    }  
    
    //---------------------------------------------------------------------
    /**
       Retrieves the revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }


}
