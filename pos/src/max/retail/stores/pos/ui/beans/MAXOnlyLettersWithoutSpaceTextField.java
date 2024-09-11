/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	Rev 1.0     July 04,2017        Nayya Gupta			Expect only characters in first name and last name
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;

//-------------------------------------------------------------------------
/**
 * This field allows input to be valid if it meets max and min lengt
 * requirements and is only letters. All input will be converted to uppercase.
 *
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
// -------------------------------------------------------------------------
public class MAXOnlyLettersWithoutSpaceTextField extends ConstrainedTextField {
	/**
	 *
	 */
	private static final long serialVersionUID = -4279147192599607625L;

	/**
	 * revision number
	 */
	public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

	/**
	 * atribute for allowing or not spaces
	 */
	protected boolean isSpaceAllowed = false;

	// ---------------------------------------------------------------------
	/**
	 * Constructor.
	 */
	// ---------------------------------------------------------------------
	public MAXOnlyLettersWithoutSpaceTextField() {
		this("");
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructor.
	 *
	 * @param value
	 *            the default text for the field
	 */
	// ---------------------------------------------------------------------
	public MAXOnlyLettersWithoutSpaceTextField(String value) {
		this(value, 0, Integer.MAX_VALUE);
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructor.
	 *
	 * @param value
	 *            the default text for the field
	 * @param minLength
	 *            the minimum length for a valid field
	 * @param maxLength
	 *            the maximum length for a valid field
	 */
	// ---------------------------------------------------------------------
	public MAXOnlyLettersWithoutSpaceTextField(String value, int minLength, int maxLength) {
		super(value, minLength, maxLength);
	}

	// --------------------------------------------------
	/**
	 * Gets the default model for the Constrained field
	 *
	 * @return the model for length constrained fields
	 */
	// ---------------------------------------------------------------------
	@Override
	protected Document createDefaultModel() {
		return new MAXOnlyLettersWithoutSpaceDocument(Integer.MAX_VALUE);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns default display string.
	 * <P>
	 *
	 * @return String representation of object
	 */
	// ---------------------------------------------------------------------
	@Override
	public String toString() {
		String strResult = new String(
				"Class: LSIPLOnlyLettersWithoutSpaceTextField (Revision " + getRevisionNumber() + ") @" + hashCode());
		return (strResult);
	}

	// ---------------------------------------------------------------------
	/**
	 * Gets isSpaceAllowed flag.
	 *
	 * @return Returns the isSpaceAllowed.
	 */
	// ---------------------------------------------------------------------
	public boolean isSpaceAllowed() {
		return isSpaceAllowed;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets isSpaceAllowed flag.
	 *
	 * @param value
	 *            set the space allowed flag
	 */
	// ---------------------------------------------------------------------
	public void setSpaceAllowed(boolean value) {
		((MAXOnlyLettersWithoutSpaceDocument) getDocument()).setSpaceAllowed(value);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <P>
	 *
	 * @return String representation of revision number
	 */
	// ---------------------------------------------------------------------
	@Override
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}
}
