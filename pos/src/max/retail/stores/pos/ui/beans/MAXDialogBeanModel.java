/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		intial draft for Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;



public class MAXDialogBeanModel extends DialogBeanModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -8833243666683215301L;
	/**
	 * Flag to change dialog text color
	 */
	private boolean changeTextColor = false;
	/**
	 * Text that need to change color
	 */
	private String textForColorChange = null;
	
	protected String[] letters = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };

	/**
	 * Default constructor.
	 */
	public MAXDialogBeanModel() {
		super();
	}

	/**
	 * Constructor initializes fieldArgs with String[].
	 *
	 * @param String
	 *            [] args - the arguments for the model
	 */
	public MAXDialogBeanModel(String[] args) {
		super(args);
	}

	/**
	 * @return the changeTextColor
	 */
	public boolean isChangeTextColor() {
		return changeTextColor;
	}

	/**
	 * @param changeTextColor the changeTextColor to set
	 */
	public void setChangeTextColor(boolean changeTextColor) {
		this.changeTextColor = changeTextColor;
	}

	/**
	 * @return the textForColorChange
	 */
	public String getTextForColorChange() {
		return textForColorChange;
	}

	/**
	 * @param textForColorChange the textForColorChange to set
	 */
	public void setTextForColorChange(String textForColorChange) {
		this.textForColorChange = textForColorChange;
	}
	
    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: DialogBeanModel Revision: " + revisionNumber + "\n");
        buff.append("ResourceID [" + fieldResourceID + "]\n");
        buff.append("BannerColor [" + fieldBannerColor + "]\n");
        buff.append("Type                   [" + fieldType + "]\n");
        buff.append("Title                  [" + fieldTitle + "]\n");
        buff.append("Desc                   [" + fieldDescription + "]\n");
        buff.append("Args                   [" + fieldArgs + "]\n");
        buff.append("UiGeneratedError       [" + uiGeneratedError + "]\n");
        buff.append("UiGeneratedCancel      [" + uiGeneratedCancel + "]\n");
        buff.append("UiGeneratedHelp        [" + uiGeneratedHelp + "]\n");

        buff.append("Letters                [");
        if (letters != null)
        {    
            for (int i = 0; i < letters.length; i++)
            {
                buff.append(" " + letters[i]);
            }
        }
        else
        {
            buff.append("No Letters Defined");
        }
        buff.append("]");

        return(buff.toString());
    }

	public void setType(int acknowledgement, String string) {
		// TODO Auto-generated method stub
		
	}
}

