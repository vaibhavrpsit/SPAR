/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InputPanelBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:46 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:22 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:06 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 *
 *  Revision 1.2  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:46   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.BorderLayout;


//----------------------------------------------------------------------------
/**
     This class takes input from the response area.
     
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class InputPanelBean extends CycleRootPanel 
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //------------------------------------------------------------------------
    /**
     * Constructor
    */
    //------------------------------------------------------------------------
    public InputPanelBean()
    {
        super(new BorderLayout());
        UI_PREFIX = "PromptArea";
        initialize();
    }
    
    //------------------------------------------------------------------------
    /**
     * Initialize the class
    */
    //------------------------------------------------------------------------
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }
    
}
