/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CycleRootPanel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:39 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:43 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:25 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.LayoutManager;
//-------------------------------------------------------------------------
/**
   This class allows the focus behavior of a panel to be treated as the
   focus cycle root.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class CycleRootPanel extends BaseBeanAdapter
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
       flag of whether this Panel should be treated as the focus cycle
       root 
    */
    protected boolean isRoot;

    //---------------------------------------------------------------------
    /**
       Default constructor.
    */
    //---------------------------------------------------------------------
    public CycleRootPanel()
    {
        super();
        isRoot = true;
    }

    //---------------------------------------------------------------------
    /**
       Constructor that defines the layout manager.
       @param layout the layout manager
    */
    //---------------------------------------------------------------------
    public CycleRootPanel(LayoutManager layout)
    {
        super(layout);
        isRoot = true;
    }

    //---------------------------------------------------------------------
    /**
       Return whether this panel should be treated as the focus cycle root.
       The default value is <code>true</code>. <P>
       @return true if this panel should be the focus cycle root, otherwise
       false
    */
    //---------------------------------------------------------------------
    public boolean isFocusCycleRoot()
    {
        return isRoot;
    }

    //---------------------------------------------------------------------
    /**
       Set whether this panel should be a focus cycle root. <P>
       @param cycleRoot value to set the focus cycle root flag
    */
    //---------------------------------------------------------------------
    public void setFocusCycleRoot(boolean cycleRoot)
    {
        isRoot = cycleRoot;
    }
}
