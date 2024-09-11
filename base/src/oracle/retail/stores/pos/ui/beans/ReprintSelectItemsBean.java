/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReprintSelectItemsBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:24:43 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:13:43 PM  Robert Pearse   
 *
 Revision 1.3  2004/04/27 22:25:53  dcobb
 @scr 4452 Feature Enhancement: Printing
 Code review updates.
 *
 Revision 1.2  2004/04/23 16:06:58  tfritz
 @scr 4452 - This screen now displays the Totals bean.
 *
 Revision 1.1  2004/04/22 21:27:13  dcobb
 @scr 4452 Feature Enhancement: Printing
 Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.ListCellRenderer;

//--------------------------------------------------------------------------
/**
    The ReprintSelectItemsBean presents a list of items from a transaction 
    that the user can select to print a gift receipt.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReprintSelectItemsBean extends SaleBean
{
    /** revision number **/
    public static final String revisionNumber           = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public ReprintSelectItemsBean()
    {
        super();
        setName("ReprintSelectItemsBean");
    }

    //---------------------------------------------------------------------
    /**
        Initializes the line item renderer.  This gives extending classes
        the oportunity to set up their own renderer.
        @return the header panel
    **/
    //---------------------------------------------------------------------
    protected ListCellRenderer getRenderer()
    {
        renderer = new ReturnLineItemRenderer();
        return renderer;
    }

    //---------------------------------------------------------------------
    /**
        Activate this screen.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        list.addFocusListener(this);
    }
    
    //--------------------------------------------------------------------------
    /**
        Deactivates this bean.
    **/
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        list.removeFocusListener(this);
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
        ReprintSelectItemsBean bean = new ReprintSelectItemsBean();
        frame.add("Center", bean);
        frame.setSize(bean.getSize());
        frame.setVisible(true);
    }
}
