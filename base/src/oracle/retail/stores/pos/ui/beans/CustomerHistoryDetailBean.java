/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerHistoryDetailBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 22:14:54  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:56:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:48   msg
 * Initial revision.
 * 
 *    Rev 1.3   26 Jan 2002 18:52:26   baa
 * ui fixes
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.2   Jan 19 2002 10:29:32   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   23 Oct 2001 16:54:48   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   19 Oct 2001 15:34:06   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//--------------------------------------------------------------------------
/**
    The CustomerHistoryDetailBean presents a list of items from a transaction that
    is associated with the customer
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerHistoryDetailBean extends SaleBean
{
    /** revision number **/
    public static final String revisionNumber           = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public CustomerHistoryDetailBean()
    {
        super();
        setName("CustomerHistoryDetailBean");
    }

    //---------------------------------------------------------------------
    /**
        Starts the part when it is run as an application
        <p>
        @param args command line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        CustomerHistoryDetailBean bean = new CustomerHistoryDetailBean();
        frame.add("Center", bean);
        frame.setSize(bean.getSize());
        frame.setVisible(true);
    }
}
