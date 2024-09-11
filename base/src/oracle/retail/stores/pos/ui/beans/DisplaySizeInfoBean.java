/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DisplaySizeInfoBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1  2004/03/05 14:39:37  baa
 *   @scr 3561  Returns
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 10 2003 16:02:20   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:22   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:53:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 09 2002 10:46:14   mpm
 * More text externalization.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
    This class displays the journal display screen.
    It is used with the DisplayTextBeanModel class. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
**/
//----------------------------------------------------------------------------
public class DisplaySizeInfoBean extends DisplayTextBean
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //---------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
     **/
    //---------------------------------------------------------------------
    public DisplaySizeInfoBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
       Activate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
    }
    //---------------------------------------------------------------------
    /**
       Deactivate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
     }

    //---------------------------------------------------------------------
    /**
       Update the bean with fresh data
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        setDisplayText(beanModel.getDisplayText());
        //displayTextArea.setCaretPosition(0);
    }

    //---------------------------------------------------------------------
    /**
       Set the focus for the screen.
       @param aFlag 
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
    }
 
 
    /** 
     * main method
     * @param args
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        DisplaySizeInfoBean bean = new DisplaySizeInfoBean();

        StringBuffer text = new StringBuffer("This is some text. ");
        text.append("And this is some more text. There is still more text. ");
        text.append("Even more text.");

        bean.setDisplayText(text.toString());

        UIUtilities.doBeanTest(bean);
    }

}
