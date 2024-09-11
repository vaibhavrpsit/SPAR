/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/PromptTransactionSite.java /main/11 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:39 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/10/2005 10:38:00    Rohit Sachdeva  6429:
 *         Optional date in the transaction ID should be taken up as business
 *         date when entered as per format specified
 *    3    360Commerce1.2         3/31/2005 15:29:32     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:27     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:28     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:02:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:16:02   msg
 * Initial revision.
 *
 *    Rev 1.1   26 Apr 2002 11:02:46   vxs
 * Added cargo.setVisitedSuspendedListSite(false); in arrive()
 * Resolution for POS SCR-1612: Susp/Resume - SUSP_NOT_RETRIEVABLE - system crashes when pressing enter
 *
 *    Rev 1.0   Mar 18 2002 11:39:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Prompts for transaction ID.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class PromptTransactionSite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "PromptTransactionSite";

    /**
     * Prompts for transaction ID.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();
        // last site visited as of now is not the displaySuspendedListSite
        cargo.setVisitedSuspendedListSite(false);
        // issue prompt
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel prModel = new PromptAndResponseModel();
        prModel.setMinLength(Integer.toString(TransactionID.getTransactionIDLength()));
        prModel.setMaxLength(Integer.toString(TransactionID.getTransactionIDLength()));

        POSBaseBeanModel posModel = new POSBaseBeanModel();
        posModel.setPromptAndResponseModel(prModel);
        ui.showScreen(POSUIManagerIfc.PROMPT_RETRIEVE_TRANSACTION, new POSBaseBeanModel());
    }
}
