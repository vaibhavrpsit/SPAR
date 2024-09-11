/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SalePromptAndResponseBean.java /rgbustores_13.4x_generic_branch/1 2011/03/21 16:19:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/27/10 - Override method setCurrentResponseText in order to
 *                         strip whitespace of input
 *    abondala  01/03/10 - update header date
 *    cgreene   08/21/09 - XbranchMerge cgreene_perffixstringoob from
 *                         rgbustores_13.1x_branch
 *    cgreene   08/21/09 - catch possible runtime exception getting text and
 *                         log it
 *    ranojha   02/18/09 - Fixed NullPointerException cases
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/28/2008 3:46:48 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    4    360Commerce 1.3         5/27/2008 7:37:28 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
 *
 *   Revision 1.6  2004/08/09 21:33:47  jdeleau
 *   @scr 6799 F11 was not deleting items in a multi-select, because it was
 *   trying to delete the data in the prompt and response bean instead.  Now
 *   all spaces will be treated as empty strings.
 *
 *   Revision 1.5  2004/07/31 17:28:42  cdb
 *   @scr 6348 Updated so updateModel doesn't update bean components. Cures a Java UI thread deadlock.
 *
 *   Revision 1.4  2004/07/02 18:11:14  mweis
 *   @scr 5798 Selection by spacebar in Returns didn't select
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 10 2003 15:03:02   dcobb
 * Migrate to JVM 1.4.1.
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;

/**
 * The prompt and response bean holds both the prompt area and the input area.
 * The prompt area is on the left, input panel is on the right.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SalePromptAndResponseBean extends PromptAndResponseBean
                                       implements ClearActionListener,
                                                  ValidateActionListener
{
    private static final long serialVersionUID = 4272976873100470746L;
    /** debug logger */
    private static final Logger logger = Logger.getLogger(SalePromptAndResponseBean.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Constant for the clear action name **/
    protected static final String CLEAR_ACTION = "Clear";
    /** Constant for the next action name **/
    protected static final String NEXT_ACTION  = "Next";
    /** Action listener to which to pass the clear action. **/
    protected ClearActionListener clearActionListener = null;

    /**
     * Default constructor.
     */
    public SalePromptAndResponseBean()
    {
        super();
    }

    /**
     * Specific implementation of updateModel.
     */
    @Override
    public void updateModel()
    {
        if (beanModel != null)
        {
            if (promptModel == null)
            {
                promptModel = new PromptAndResponseModel();
            }
            if(promptModel.getMaxLength() == null)
            {
                promptModel.setMaxLength(this.maxLength);
            }

            // Update the model, but only if there is text in the active field; the
            // performAction() method calls this method before the business logic.
            // This is synched to prevent errors with simultaneous updates.
            String text = null;
            synchronized (activeResponseField)
            {
                try
                {
                    text = activeResponseField.getText();
                }
                catch (RuntimeException e)
                {
                    logger.warn("Runtime error occurred updating sale prompt model.", e);
                }
            }

            if (text != null && text.trim().length() > 0)
            {
                promptModel.setResponseText(text);
            }
            beanModel.setPromptAndResponseModel(promptModel);
        }
    }

    /**
     * This method is called when the user presses "clear" or "next".
     *
     * @param evt the action event
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
        // Make sure the focus in the response field.
        if (activeResponseField != null)
        {
            setCurrentFocus(activeResponseField);
        }

        // Since the NEXT key does not automatically display another
        // screen, other means must be used to clear the response area.
        // This code does that.
        if (evt.getActionCommand().equalsIgnoreCase(NEXT_ACTION))
        {
            // Update the model before the field is cleared, because
            // the field will be empty when the Business Logic calls it.
            updateModel();
            UISubsystem.getInstance().mail(new Letter(NEXT_ACTION), true);

            // Clear the field.
            if (activeResponseField != null)
            {
                activeResponseField.setText("");
            }
        }

        // Clear the response field, or, if there is noting to clear,
        // pass it to the next listener.
        if (evt.getActionCommand().equalsIgnoreCase(CLEAR_ACTION))
        {
            // If the active field has text to clear, clear it;
            // otherwise alter the next clear listener, if there is one.
            if (activeResponseField != null &&
                activeResponseField.getText().trim().length() > 0)
            {
                activeResponseField.setText("");
            }
            else if (clearActionListener != null)
            {
                activeResponseField.setText(activeResponseField.getText().trim());
                clearActionListener.actionPerformed(evt);
            }
        }
    }

    /**
     * Set the {@link #currentResponseText}. Remove all spaces from input.
     *
     * @param inputText
     */
    @Override
    protected void setCurrentResponseText(String inputText)
    {
        if (inputText.contains(" "))
        {
            super.setCurrentResponseText(inputText.replace(" ", ""));
        }
        else
        {
            super.setCurrentResponseText(inputText);
        }
    }

    /**
     * Adds (actually sets) the clear listener on the Clear button.
     *
     * @param listener the Clear Action Listener
     */
    public void addClearActionListener(ClearActionListener listener)
    {
        clearActionListener = listener;
    }

    /**
     * Removes (actually resets) the Clear listener on the Next button.
     *
     * @param listener the Clear Action Listener
     */
    public void removeClearActionListener(ClearActionListener listener)
    {
        clearActionListener = null;
    }
}
