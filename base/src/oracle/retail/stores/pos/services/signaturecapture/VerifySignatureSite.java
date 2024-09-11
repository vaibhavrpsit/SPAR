/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/signaturecapture/VerifySignatureSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  03/19/09 - Fixed signature capture dialog issue for PDO
 *                         transaction
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:32 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 03 2003 15:44:12   RSachdeva
 * Add CIDScreen support
 * Resolution for POS SCR-3355: Add CIDScreen support
 * 
 *    Rev 1.0   Aug 29 2003 16:07:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 30 2003 15:02:14   vxs
 * clear signature capture device by putting up a blank form.
 * Resolution for POS SCR-2781: Ingenico Device doesn't clear Signature after Credit Transaction is complete.
 * 
 *    Rev 1.0   Apr 29 2002 15:02:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.signaturecapture;

// java imports
import java.awt.Point;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SignatureDialogBeanModel;

//------------------------------------------------------------------------------
/**
 * Display the signature verification screen if needed.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//------------------------------------------------------------------------------
public class VerifySignatureSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "VerifySignatureSite";

    //--------------------------------------------------------------------------
    /**
     * Displays the signature verification screen.
     * 
     * @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SignatureCaptureCargo cargo = (SignatureCaptureCargo)bus.getCargo();

        // check whether transaction is special order or PDO
        if (cargo.isOrderTransaction())
        {
            Letter letter = new Letter(CommonLetterIfc.YES);
            cargo.setOrderTransactionFlag(false);
            bus.mail(letter, BusIfc.CURRENT);
        }
        else
        {
            // display the signature for verification
            SignatureDialogBeanModel model = new SignatureDialogBeanModel((Point[])cargo.getSignature());
            model.setResourceID("VerifySignature");
            model.setType(DialogScreensIfc.SIGNATURE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    }
}
