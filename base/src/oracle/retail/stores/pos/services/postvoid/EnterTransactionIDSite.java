/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/EnterTransactionIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    09/11/09 - Use one field for scanning/entering transaction id
 *                         for return with receipt
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/12/2007 3:57:48 PM   Ashok.Mondal    CR
 *         4296 :Merge from V7.2.2 to trunk.
 *    4    360Commerce 1.3         1/25/2006 4:11:00 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:29 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/10/2005 10:37:59    Rohit Sachdeva  6429:
 *         Optional date in the transaction ID should be taken up as business
 *         date when entered as per format specified
 *    3    360Commerce1.2         3/31/2005 15:28:06     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:29     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:55     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Nov 04 2003 11:16:02   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:32   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:20   epd
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 16:05:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:07:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:22:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site displays the RETURN_RECEIPT screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterTransactionIDSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the RETURN_RECEIPT screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        String currentLetter = bus.getCurrentLetter().getName();
        String screenName = POSUIManagerIfc.RETURN_RECEIPT;
        //Set Type of receipt screen to display

        //If re-entering this service use previous data
        POSBaseBeanModel model= new POSBaseBeanModel();
        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (POSBaseBeanModel) ui.getModel(screenName);
        }

         ui.showScreen(screenName, model);

    }

    //----------------------------------------------------------------------
    /**
       Redisplays the VOID_TRANSACTION screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void reset(BusIfc bus)
    {

        arrive(bus);

    }

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        EnterTransactionIDSite obj = new EnterTransactionIDSite();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
