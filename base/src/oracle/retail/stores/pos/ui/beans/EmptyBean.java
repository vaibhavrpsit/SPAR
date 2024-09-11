/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmptyBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:59 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:52 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *     Placeholder bean that displays an empty panel.
 *    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
//------------------------------------------------------------------------------
public class EmptyBean extends BaseBeanAdapter
{
    /** revision number */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    
    //--------------------------------------------------------------------------
    /**
     *    Default onstructor.
     */
    public EmptyBean() 
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the class.
     */
    protected void initialize() 
    {
        setName("EmptyBean");
        UI_PREFIX = "EmptyBean";
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }
    
    //--------------------------------------------------------------------------
    /**
     *     Sets the model 
     *     @param model UIModelIfc model 
     */
    public void setModel(UIModelIfc model)
    {
    }
    
    //--------------------------------------------------------------------------
    /**
     *    Returns default display string.
     *    @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) + 
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
        
    }

    //--------------------------------------------------------------------------
    /**
     *    Retrieves the Team Connection revision number.
     *    @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
    
    //--------------------------------------------------------------------------
    /**
     *     main entrypoint - starts the part when it is run as an application
     *     @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) 
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EmptyBean());
    }
}
