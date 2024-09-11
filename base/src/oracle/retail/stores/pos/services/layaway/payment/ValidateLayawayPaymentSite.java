/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/ValidateLayawayPaymentSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   May 19 2003 11:18:24   adc
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

//foundation imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;


//------------------------------------------------------------------------------
/**
    This class retrieves the input from the payment detail screen and compare
    the entered layaway fee with the existing one. If the are different a check access
    needs to be performed.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ValidateLayawayPaymentSite extends PosSiteActionAdapter
{                                       // begin class ValidateLayawayPaymentSite
    /**
        lane name constant
    **/
    public static final String LANENAME = "ValidateLayawayPayment";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        sequence number constant
    **/
    protected static final long GENERATE_SEQUENCE_NUMBER = -1;
    /**
        date format for journal string
    **/
    public static final String dateFormat = "MM/dd/yyyy";

    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        the payment information is retrieved from the ui and placed
        in the cargo. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin traverse()
        
         String letter = "Online";
         LayawayCargo cargo = (LayawayCargo) bus.getCargo();

        // Check the data exception error code
        if (cargo.getDataExceptionErrorCode() == DataException.CONNECTION_ERROR)
        {
            letter = "Offline";
        }
        
        bus.mail(new Letter(letter), BusIfc.CURRENT);


    }                                   // end traverse()

}                                       // end class ValidateLayawayPaymentSite
