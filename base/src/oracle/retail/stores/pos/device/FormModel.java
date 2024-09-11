/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/FormModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
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
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;

/**
 * This class defines the model for the CPOI Form.
 */
public class FormModel implements DeviceModelIfc
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7432880207649019658L;

    /**
     * Form name.
     */
    protected String formName = "";

    /**
     * Button action.
     */
    protected String buttonAction = "";

    /**
     * Text Data.
     */
    protected String textData = "";

    /**
     * Returns the buttonAction.
     * @return Returns the buttonAction.
     */
    public String getButtonAction()
    {
        return buttonAction;
    }

    /**
     * Sets the buttonAction value.
     * @param buttonAction The buttonAction to set.
     */
    public void setButtonAction(String buttonAction)
    {
        this.buttonAction = buttonAction;
    }

    /**
     * Returns the formName.
     * @return Returns the formName.
     */
    public String getFormName()
    {
        return formName;
    }

    /**
     * Sets the formName value.
     * @param formName The formName to set.
     */
    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    /**
     * Returns the textData.
     * @return Returns the textData.
     */
    public String getTextData()
    {
        return textData;
    }

    /**
     * Sets the textData value.
     * @param textData The textData to set.
     */
    public void setTextData(String textData)
    {
        this.textData = textData;
    }
    

}
