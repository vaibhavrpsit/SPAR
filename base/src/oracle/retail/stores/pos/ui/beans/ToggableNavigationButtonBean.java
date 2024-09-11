/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ToggableNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:32 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 18:30:41  cdb
 *  @scr 0 Removed tabs from all java source code.
 *
 *  Revision 1.2  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.1  2004/02/19 16:38:57  lzhao
 *  @scr 3841 Inquiry Options Enhancement
 *  for taggling location navigation button.
 *
 *  
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.event.DocumentEvent;

import oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener;

//-------------------------------------------------------------------------
/**
 This class responsible for taggling the button on local navigation bar. 
 The button will become enable when it is satisfy input criterial. 
 
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//-------------------------------------------------------------------------
public class ToggableNavigationButtonBean extends NavigationButtonBean implements  ResponseDocumentListener
{

    protected int minLength = 0;

    //------------------------------------------------------------------------------
    /**
     *   Default constructor.
     */
    //------------------------------------------------------------------------------
    public ToggableNavigationButtonBean()
    {
        super();
        orientation = VERTICAL;
        buttonPrefix = "VerticalButton";
    }

    //--------------------------------------------------------------------------
    /**
     Creates an empty ToggableNavigationButtonBean.
     @param actions two dimensional list of buttions
     */
    //--------------------------------------------------------------------------
    public ToggableNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }


    //---------------------------------------------------------------------
    /**
     Overwrite for the DocumentListener interface.
     @Param evt the document event
     **/
    //---------------------------------------------------------------------
    public void changedUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }
    

    //---------------------------------------------------------------------
    /**
     Overwrite for the DocumentListener interface.
     @Param evt the document event
     **/
    //---------------------------------------------------------------------
    public void insertUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    //---------------------------------------------------------------------
    /**
     Overwrite for the DocumentListener interface.
     @Param evt the document event
     **/
    //---------------------------------------------------------------------
    public void removeUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    //---------------------------------------------------------------------
    /**
     Determines if the response field has text and sets the local navigation 
     button status appropriately.
     @Param evt the cocument event
     **/
    //---------------------------------------------------------------------
    public void checkAndEnableButtons(DocumentEvent evt)
    {    
        int len=evt.getDocument().getLength();

        if(len > 0)
        {
            //will need to check and see if they are already enabled
            //and if we can ignore next
            if ( len >= minLength )
            {
                actions[0][0].setEnabled(true);
            }
            else
            {
                actions[0][0].setEnabled(false);
            }
        }
        else
        {
            //will need to check and see if they are already disabled
            //and if we can ignore next
            actions[0][0].setEnabled(false);
        }
    }
    
    //---------------------------------------------------------------------
    /**
     Overwrite for the DocumentListener interface.
     @Param len the minimum length specified in corresponding layout screen  
     **/
    //---------------------------------------------------------------------
    public void setMinLength(int len)
    {
        minLength = len;
    }

    //---------------------------------------------------------------------
    /**
     Get the miminum length of gift code.
     @return int the minimum length specified in corresponding layout screen 
     **/
    //---------------------------------------------------------------------
    public int getMinLength()
    {
        return minLength;
    }
}
