/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerSelectBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *    tzgarba   02/25/09 - Removed test class dependencies from shipping
 *                         source.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 10 2003 15:31:56   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:10   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:57:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:56   msg
 * Initial revision.
 * 
 *    Rev 1.6   08 Feb 2002 18:52:28   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.5   28 Jan 2002 16:00:34   baa
 * fixing dual list
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.4   26 Jan 2002 18:52:32   baa
 * ui fixes
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.3   25 Jan 2002 21:03:42   baa
 * ui fixes for customer
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.2   Jan 24 2002 14:28:42   mpm
 * Added renderer entries for CustomerSelect bean.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//java imports
import java.awt.Dimension;
import java.util.Vector;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    CustomerSelectBean is used by Customer Lookup Service when more than
 *    one Customer that matches a criteria is found.<p>
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
 *      @deprecated as of release 5.0.0
 */
//------------------------------------------------------------------------------
public class CustomerSelectBean extends DualListBean
{


    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    //--------------------------------------------------------------------------
    public CustomerSelectBean()
    {
        super();
        beanModel = new DualListBeanModel();

    }


    //--------------------------------------------------------------------------
    /**
     *    Configures the top scroll pane. <P>
     */
    //--------------------------------------------------------------------------
    protected void configureTopScrollPane()
    {
        super.configureTopScrollPane();
        getTopScrollPane().setPreferredSize(new Dimension(160, 60));
        getTopScrollPane().setMinimumSize(new Dimension(160, 60));

    }



    //--------------------------------------------------------------------------
    /**
     *  Enables or disables the Query list and panel.
     *    @param aValue true to enable, false to disable
     */
    //--------------------------------------------------------------------------
    protected void enableQuery(boolean aValue)
    {
        getTopList().setEnabled(aValue);
        getTopScrollPane().setEnabled(aValue);

        getScrollPane().setEnabled(aValue);
        getList().setEnabled(aValue);
    }

    //--------------------------------------------------------------------------
    /**
     *     Updates the bean if It's been changed
     */
    //--------------------------------------------------------------------------
    protected void updateBean()
    {
        DualListBeanModel model = (DualListBeanModel)beanModel;

        POSListModel topListModel = (POSListModel) getTopList().getModel();
        topListModel.removeAllElements();
        topListModel.addElement(model.getTopListVector().firstElement());

        Vector matches = model.getListVector();
        POSListModel posListModel = new POSListModel(matches);
        // set the customer matches
        getList().setModel(posListModel);
        getList().requestFocusInWindow();

    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
