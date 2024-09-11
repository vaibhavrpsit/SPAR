/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/CustomerLaunchShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/01/09 - Set the offline indicator to OFFLINE_ADD instead of
 *                         OFFLINE_LINK as orders require the attached customer
 *                         object.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:26:56   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 06 2003 16:22:44   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the Sale service to the Customer service.
    <p>
    $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class CustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8503248783524557335L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.validate.CustomerLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    protected SaleCargoIfc saleCargo = null;


    //----------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        saleCargo= (SaleCargoIfc)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        String transactionID = null;
        EmployeeIfc operador = saleCargo.getOperator();

        if (saleCargo.getTransaction() != null)
        {
            transactionID = saleCargo.getTransaction().getTransactionID();
            if (saleCargo.getTransaction().getCustomer() != null)
            {
                cargo.setCustomerLink(true);
                cargo.setOriginalCustomer(saleCargo.getTransaction().getCustomer());
            }
        }
        else
        {
          CustomerUtilities.journalCustomerEnter(bus, operador.getEmployeeID(), transactionID);
        }

        cargo.setRegister(saleCargo.getRegister());
        cargo.setTransactionID(transactionID);
        cargo.setEmployee(operador);
        cargo.setOperator(operador);
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        cargo.setOfflineExit(false);

     }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
