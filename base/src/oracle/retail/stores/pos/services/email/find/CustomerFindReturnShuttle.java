/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/CustomerFindReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:21 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:23  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   30 Oct 2001 16:10:48   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 24 2001 11:17:28   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.email.EmailCargo;

//--------------------------------------------------------------------------
/**
   This shuttle returns to Email from Customer Find.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerFindReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5035176377061586609L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.email.find.CustomerFindReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected CustomerMainCargo customerCargo  = null;

    //----------------------------------------------------------------------
    /**
       Load Customer Find Cargo data into the shuttle for transfer to EMail.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        customerCargo = (CustomerMainCargo) bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Set customer string; the customer whose EMessages are to be retrieved.
       Set the search method indicator so Lookup Service knows that search was
       performed by Customer. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        EmailCargo cargo = (EmailCargo) bus.getCargo();
        cargo.setSelectedCustomer(customerCargo.getCustomer());
        cargo.setDataExceptionErrorCode(customerCargo.getDataExceptionErrorCode());

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
