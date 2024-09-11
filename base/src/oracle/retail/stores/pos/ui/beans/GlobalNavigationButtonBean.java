/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GlobalNavigationButtonBean.java /main/22 2013/10/07 09:51:05 rahravin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rahravin  10/01/13 - Manage Delete button in phone number entry in POS
 *    mkutiana  04/15/13 - clear button management bug - was not getting
 *                         enabled/disabled appropriately
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    npoola    10/08/10 - Set the actual state of the button incased defined
 *                         in the *uicfg.xml
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/07/09 - update to setOrientation method
 *    cgreene   10/19/09 - XbranchMerge cgreene_bug-9027679 from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/19/09 - override deactivate method to ensure that minlength
 *                         is reset to zero
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:59 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:46   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:20   msg
 * Initial revision.
 * 
 *    Rev 1.7   19 Feb 2002 12:44:44   baa
 * disable enter if len less that min
 * Resolution for POS SCR-929: Able to enter less than min/more than max on Gift Cert Entry screen
 *
 *    Rev 1.6   11 Feb 2002 12:21:02   baa
 * enable clear button after len > 0
 * Resolution for POS SCR-541: Clear is not enabled until 3 characters are entered on GR Inquiry screen
 *
 *    Rev 1.4   08 Feb 2002 18:52:32   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   30 Jan 2002 16:42:46   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;

import oracle.retail.stores.foundation.manager.gui.UIBeanIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener;
import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;
import oracle.retail.stores.pos.ui.beans.ValidatingFormattedTextField.ValidatingFormattedTextDocument;

import org.apache.log4j.Logger;

/**
 * This class contains one constant that forces the button bar to be horizontal.
 * 
 * @version $Revision: /main/22 $
 */
public class GlobalNavigationButtonBean extends NavigationButtonBean
    implements ResponseDocumentListener
{
    private static final long serialVersionUID = 9111583286895000796L;
    private static final Logger logger = Logger.getLogger(GlobalNavigationButtonBean.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * Constant for the NEXT action.
     */
    public static final String NEXT = "Next";

    /**
     * Constant for the CLEAR action.
     */
    public static final String CLEAR = "Clear";

    /**
     * Indicates if this bean should manage the enable property of Next Button.
     */
    protected boolean manageNextButton = true;

    /**
     * The minimum length of the "response document" before enabling 'Next'.
     */
    protected int minLength = 0;

    /**
     * Default constructor.
     */
    public GlobalNavigationButtonBean()
    {
        super();
        setOrientation(HORIZONTAL);
    }

    /**
     * Creates an empty NavigationButtonBean.
     * 
     * @param actions two dimensional list of buttions
     */
    public GlobalNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#popupMenu(String, UIBeanIfc, UIModelIfc)
     */
    @Override
    public void popupMenu(String menuName, UIBeanIfc childBean, UIModelIfc model)
    {
        setModel(model);
    }

    /**
     * Sets the model for the current settings of this bean.
     * 
     * @param model the model for the current values of this bean
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set EmployeeMasterBeanModel to null");
        }
        if (model instanceof POSBaseBeanModel)
        {
            baseModel = (POSBaseBeanModel)model;
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


    /**
     * Overridden to reset the {@link #minLength} member when this bean is
     * deactivated. This prevents subsequent uses of this bean from getting the
     * wrong minimum length set.
     * 
     * @see oracle.retail.stores.pos.ui.beans.NavigationButtonBean#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        minLength = 0;
    }

    /**
     * Set the manageNextButton boolean.
     * 
     * @param manage a string with the value of "true" or "false"
     */
    public void setManageNextButton(String manage)
    {
        manageNextButton = UIUtilities.getBooleanValue(manage);
    }

    /**
     * Set the minimum length of the "response document" before enabling 'Next'.
     *
     * @see oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener#setMinLength(int)
     */
    public void setMinLength(int len)
    {
        minLength = len;
    }

    /**
     * Get the minimum length of the "response document" before enabling 'Next'.
     * 
     * @return
     * @see #setMinLength(int)
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Determines if the response field has text and sets the "Next" and "Clear"
     * buttons appropriately.
     * 
     * @Param evt the cocument event
     */
    public void checkAndEnableButtons(DocumentEvent evt)
    {
        try
        {
            int len = evt.getDocument().getLength();

            if (len > 0)
            {
                // will need to check and see if they are already enabled
                // and if we can ignore next

                Boolean clearButtonState = (buttonStates.get(CLEAR) == null) ? true : buttonStates.get(CLEAR)
                        .booleanValue();
                if (evt.getDocument() instanceof ValidatingFormattedTextDocument)
                {
                    String text = evt.getDocument().getText(0, len);
                    Pattern pat = Pattern.compile("[a-zA-Z0-9]");
                    Matcher matcher = pat.matcher(text);

                    if (matcher.find())
                    {
                        getUIAction(CLEAR).setEnabled(clearButtonState.booleanValue());
                    }
                    else
                    {
                        getUIAction(CLEAR).setEnabled(false);
                    }
                }
                else
                {
                    getUIAction(CLEAR).setEnabled(clearButtonState.booleanValue());
                }

                if (len >= minLength)
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
                // will need to check and see if they are already disabled
                // and if we can ignore next
                getUIAction(CLEAR).setEnabled(false);
                if (manageNextButton)
                {
                    getUIAction(NEXT).setEnabled(false);
                }
            }
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the NEXT or CLEAR action.");
        }
        catch (BadLocationException e)
        {
            logger.warn("Invalid location.");
        }
    }

    /**
     * Implemented for the DocumentListener interface.
     * 
     * @Param evt the cocument event
     */
    public void changedUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Implemented for the DocumentListener interface.
     * 
     * @Param evt the cocument event
     */
    public void insertUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Implemented for the DocumentListener interface.
     * 
     * @Param evt the cocument event
     */
    public void removeUpdate(DocumentEvent evt)
    {
        checkAndEnableButtons(evt);
    }

    /**
     * Adds (actually sets) the validation listener on the Next button.
     * 
     * @Param listener the Validate Action Listener
     */
    public void addValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            getUIAction(NEXT).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the NEXT action.");
        }
    }

    /**
     * Removes (actually resets) the validation listener on the Next button.
     * 
     * @Param listener the Validate Action Listener
     */
    public void removeValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            getUIAction(NEXT).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the NEXT action.");
        }
    }

    /**
     * Adds (actually sets) the clear listener on the Clear button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void addClearActionListener(ClearActionListener listener)
    {
        try
        {
            getUIAction(CLEAR).setActionListener(listener);
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the CLEAR action.");
        }
    }

    /**
     * Removes (actually resets) the Clear listener on the Next button.
     * 
     * @Param listener the Clear Action Listener
     */
    public void removeClearActionListener(ClearActionListener listener)
    {
        try
        {
            getUIAction(CLEAR).resetActionListener();
        }
        catch (ActionNotFoundException e)
        {
            logger.warn("Did not find the CLEAR action.");
        }
    }
}
