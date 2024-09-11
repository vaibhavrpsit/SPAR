/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/capturecustomerinfo/CaptureCustomerInfoCargo.java /main/14 2013/01/10 15:05:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:45 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $
     Revision 1.11  2004/06/25 14:56:32  khassen
     Added comments.

     Revision 1.10  2004/06/23 16:38:50  khassen
     @scr 5780 - modified the setCustomer() method to load in proper values.

     Revision 1.9  2004/06/21 14:22:41  khassen
     @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.

     Revision 1.8  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.7  2004/03/02 04:27:06  khassen
     @scr 0 Capture Customer Info use-case - Modifications to tour script and sites.  Added verification for postal code.

     Revision 1.6  2004/02/27 21:08:36  khassen
     @scr 0 Capture Customer Info use-case

     Revision 1.5  2004/02/27 19:23:02  khassen
     @scr 0 Capture Customer Info use-case


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package oracle.retail.stores.pos.services.tender.capturecustomerinfo;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
/**
 *
 * @author kph
 *
 * Cargo class for the capture customer info use-case.
 */
public class CaptureCustomerInfoCargo implements CargoIfc
{
    protected CaptureCustomerInfoBeanModel model;
    protected CaptureCustomerIfc customer;
    protected int tenderType;
    protected String screenType;
    protected TransactionIfc transaction;
    protected CurrencyIfc balanceDue;
    protected int transactionType;
    protected CodeListIfc personalIDTypes;

    /**
     * Constructor.
     *
     */
    public CaptureCustomerInfoCargo()
    {
        model = null;
    }

    public CaptureCustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * sets the CaptureCustomer object.
     * @param customer the CaptureCustomer object.
     */
    public void setCustomer(CaptureCustomerIfc customer)
    {
        this.customer = customer;
    }

    /**
     * sets the CaptureCustomer object by copying the Customer
     * data across.
     * @param customer the CustomerIfc object.
     */
    public void setCustomer(CustomerIfc customer)
    {
        if (customer == null)
        {
            return;
        }
        if (this.customer == null)
        {
            this.customer = DomainGateway.getFactory().getCaptureCustomerInstance();
        }
        this.customer.setFirstName(customer.getFirstName());
        this.customer.setLastName(customer.getLastName());
        this.customer.setCompanyName(customer.getCustomerName());
        this.customer.setAddressList(customer.getAddressList());
        this.customer.setPhoneList(customer.getPhoneList());
    }

    /**
     * Returns the bean model associated with this particular
     * cargo.
     * @return the capture customer info bean model.
     */
    public CaptureCustomerInfoBeanModel getModel()
    {
        return model;
    }

    /**
     * Sets the bean model for this cargo.
     *
     * @param A CaptureCustomerInfoBeanModel object.
     */
    public void setModel(CaptureCustomerInfoBeanModel m)
    {
        model = m;
    }

    /**
     * Returns the tender type for this transaction.
     * @return the tender type.
     */
    public int getTenderType()
    {
        return tenderType;
    }

    /**
     * sets the tender type for this transaction.
     * @param t the tender type.
     */
    public void setTenderType(int t)
    {
        tenderType = t;
    }

    /**
     * gets the screen type for this transaction.
     * @return the string corresponding to the screen type.
     */
    public String getScreenType()
    {
        return screenType;
    }

    /**
     * sets the screen type for this transaction.
     * @param s
     */
    public void setScreenType(String s)
    {
        screenType = s;
    }

    public TransactionIfc getTransaction()
    {
        return transaction;
    }

    public void setTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    public CurrencyIfc getBalanceDue()
    {
        return balanceDue;
    }

    public void setBalanceDue(CurrencyIfc balanceDue)
    {
        this.balanceDue = balanceDue;
    }

    public int getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(int type)
    {
        transactionType = type;
    }

    /**
     * @return the personalIDTypes
     */
    public CodeListIfc getPersonalIDTypes()
    {
        return personalIDTypes;
    }

    /**
     * @param personalIDTypes the personalIDTypes to set
     */
    public void setPersonalIDTypes(CodeListIfc personalIDTypes)
    {
        this.personalIDTypes = personalIDTypes;
    }


}
