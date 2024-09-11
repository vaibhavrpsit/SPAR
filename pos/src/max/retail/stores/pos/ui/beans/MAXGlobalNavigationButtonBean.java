/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import javax.swing.event.DocumentEvent;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ActionNotFoundException;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.UIAction;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener;
import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;

public class MAXGlobalNavigationButtonBean extends MAXNavigationButtonBean
implements  ResponseDocumentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7065332658127249568L;
	/**
    revision number supplied by Team Connection
**/
public static final String revisionNumber = "$Revision: 3$";
/**
    Constant for the NEXT action.
**/
public static final String NEXT  = "Next";
/**
    Constant for the CLEAR action.
**/
public static final String CLEAR = "Clear";
/**
    Indicates if this bean should manage the enable property of
    Next Button.
**/
protected boolean manageNextButton  = true;

protected int minLength = 0;

//------------------------------------------------------------------------------
/**
*   Default constructor.
*/
//------------------------------------------------------------------------------
public MAXGlobalNavigationButtonBean()
{
    super();
    orientation = HORIZONTAL;
    buttonPrefix = "HorizontalButton";
}

//--------------------------------------------------------------------------
/**
   Creates an empty NavigationButtonBean.
   @param actions two dimensional list of buttions
*/
//--------------------------------------------------------------------------
public MAXGlobalNavigationButtonBean(UIAction[][] actions)
{
    this();
    initialize(actions);
}

//------------------------------------------------------------------------
/**
 * Sets the model for the current settings of this bean.
 * @param model the model for the current values of this bean
*/
//------------------------------------------------------------------------
public void setModel(UIModelIfc model)
{
    if (model == null)
    {
        throw new NullPointerException("Attempt to set EmployeeMasterBeanModel to null");
    }
    if (model instanceof POSBaseBeanModel)
    {
        baseModel   = (POSBaseBeanModel) model;
    }

    if (baseModel.getGlobalButtonBeanModel() != null)
    {
        buttonModel = baseModel.getGlobalButtonBeanModel();
        if (buttonModel.getNewButtons() != null)
        {
            configureButtons(buttonModel.getNewButtons());
        }

        if (buttonModel.getModifyButtons() != null)
        {
            modifyButtons(buttonModel.getModifyButtons());
        }
    }
}

//--------------------------------------------------------------------------
/**
    Set the manageNextButton boolean.
    @param manage a string with the value of "true" or "false"
**/
//--------------------------------------------------------------------------
public void setManageNextButton(String manage)
{
    manageNextButton = UIUtilities.getBooleanValue(manage);
}

public void setMinLength(int len)
{
   minLength = len;
}

public int getMinLength()
{
   return minLength;
}
//---------------------------------------------------------------------
/**
    Determines if the response field has text and sets the "Next"
    and "Clear" buttons appropriately.
    @Param evt the cocument event
**/
//---------------------------------------------------------------------
public void checkAndEnableButtons(DocumentEvent evt)
{
    try
    {
        int len=evt.getDocument().getLength();

        if(len > 0)
        {
            //will need to check and see if they are already enabled
            //and if we can ignore next
            getUIAction(CLEAR).setEnabled(true);
            if ( len >= minLength)
            {
                if (manageNextButton)
                {
                    getUIAction(NEXT).setEnabled(true);
                }
            }
            else
            {
                if (manageNextButton)
                {
                    getUIAction(NEXT).setEnabled(false);
                }
            }
        }
        else
        {
            //will need to check and see if they are already disabled
            //and if we can ignore next
            getUIAction(CLEAR).setEnabled(false);
            if (manageNextButton)
            {
                getUIAction(NEXT).setEnabled(false);
            }
        }
    }
    catch(ActionNotFoundException e)
    {
        Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXGlobalNavigationButtonBean.class);
        logger.warn( "GlobalNavigationButtonBean.checkAndEnableButtons() did not find the NEXT or CLEAR action.");
    }
}

//---------------------------------------------------------------------
/**
    Implemented for the DocumentListener interface.
    @Param evt the cocument event
**/
//---------------------------------------------------------------------
public void changedUpdate(DocumentEvent evt)
{
    checkAndEnableButtons(evt);
}

//---------------------------------------------------------------------
/**
    Implemented for the DocumentListener interface.
    @Param evt the cocument event
**/
//---------------------------------------------------------------------
public void insertUpdate(DocumentEvent evt)
{
    checkAndEnableButtons(evt);
}

//---------------------------------------------------------------------
/**
    Implemented for the DocumentListener interface.
    @Param evt the cocument event
**/
//---------------------------------------------------------------------
public void removeUpdate(DocumentEvent evt)
{
    checkAndEnableButtons(evt);
}

//---------------------------------------------------------------------
/**
    Adds (actually sets) the validation listener on the Next button.
    @Param listener the Validate Action Listener
**/
//---------------------------------------------------------------------
public void addValidateActionListener(ValidateActionListener listener)
{
    try
    {
        getUIAction(NEXT).setActionListener(listener);
    }
    catch(ActionNotFoundException e)
    {
        Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXGlobalNavigationButtonBean.class);
        logger.warn( "GlobalNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
    }
}

//---------------------------------------------------------------------
/**
    Removes (actually resets) the validation listener on the Next button.
    @Param listener the Validate Action Listener
**/
//---------------------------------------------------------------------
public void removeValidateActionListener(ValidateActionListener listener)
{
    try
    {
        getUIAction(NEXT).resetActionListener();
    }
    catch(ActionNotFoundException e)
    {
        Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXGlobalNavigationButtonBean.class);
        logger.warn( "GlobalNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
    }
}

//---------------------------------------------------------------------
/**
    Adds (actually sets) the clear listener on the Clear button.
    @Param listener the Clear Action Listener
**/
//---------------------------------------------------------------------
public void addClearActionListener(ClearActionListener listener)
{
    try
    {
        getUIAction(CLEAR).setActionListener(listener);
    }
    catch(ActionNotFoundException e)
    {
        Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXGlobalNavigationButtonBean.class);
        logger.warn( "GlobalNavigationButtonBean.addClearActionListener() did not find the NEXT action.");
    }
}

//---------------------------------------------------------------------
/**
    Removes (actually resets) the Clear listener on the Next button.
    @Param listener the Clear Action Listener
**/
//---------------------------------------------------------------------
public void removeClearActionListener(ClearActionListener listener)
{
    try
    {
        getUIAction(CLEAR).resetActionListener();
    }
    catch(ActionNotFoundException e)
    {
        Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXGlobalNavigationButtonBean.class);
        logger.warn( "GlobalNavigationButtonBean.addClearActionListener() did not find the NEXT action.");
    }
}
}
