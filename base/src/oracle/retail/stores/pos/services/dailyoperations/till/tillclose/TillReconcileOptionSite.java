/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/TillReconcileOptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.3  2004/02/12 16:49:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:28:44   msg
 * Initial revision.
 * 
 *    Rev 1.3   22 Jan 2002 15:54:54   baa
 * convert to new security model, Role/Security updates
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.2   04 Jan 2002 16:03:26   epd
 * fixed bug
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   06 Nov 2001 14:44:58   epd
 * small change to method name
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   05 Nov 2001 16:50:28   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   29 Oct 2001 16:14:58   epd
 * Updated files to remove reference to Till related parameters.  This information, formerly contained in parameters, now resides as register settings obtained from the RegisterIfc class.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:17:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

// java imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//------------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillReconcileOptionSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2302614411187281888L;


    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();

        RegisterIfc register = cargo.getRegister();
        String TillCountTillAtClose= FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountTillAtReconcile()];

        // Display Till Reconcile option screen if TillReconcile is true.
        if (register.isTillReconcile())
        {
            cargo.setAccessFunctionID(RoleFunctionIfc.RECONCILE_TILL);
            POSUIManagerIfc ui;
            ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("TillReconcile");
            model.setType(DialogScreensIfc.CONFIRMATION);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else // Otherwise we are not reconciling
        {
            bus.mail(new Letter(CommonLetterIfc.NO), BusIfc.CURRENT);
        }
    }
}
