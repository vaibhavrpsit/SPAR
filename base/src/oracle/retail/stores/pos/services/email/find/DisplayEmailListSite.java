/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/DisplayEmailListSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:58:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Feb 2002 18:14:28   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 24 2001 11:17:32   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

// foundation imports
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    This site is used to present the user with a list of emails
    to select from in order to read or reply to.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DisplayEmailListSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        Displays a list of emails. <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Initialize Variables
        EmailCargo          cargo       = (EmailCargo)bus.getCargo();
        ListBeanModel       beanModel   = new ListBeanModel();
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        //Sets Cargo to EmailCargo
        EMessageIfc[] messageList = cargo.getEMessageList();

        //EmailListBeanModel Configured
        beanModel.setListModel(messageList);

        ui.showScreen(POSUIManagerIfc.EMAIL_LIST, beanModel);
    }

    //----------------------------------------------------------------------
    /**
        Sets the selected email. <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel beanModel = (ListBeanModel)ui.getModel(POSUIManagerIfc.EMAIL_LIST);

        EmailCargo cargo = (EmailCargo) bus.getCargo();
        cargo.setSelectedMessage((EMessageIfc)beanModel.getSelectedValue());
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
