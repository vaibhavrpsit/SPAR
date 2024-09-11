/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	Rev 1.0     July 04,2017        Nayya Gupta			Expect only characters in first name and last name
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import oracle.retail.stores.pos.ui.beans.ConstrainedTextDocument;

//-------------------------------------------------------------------------
/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is only letters. The letters will be converted to
 * uppercase.
 *
 * @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
 */
// -------------------------------------------------------------------------
class MAXOnlyLettersWithoutSpaceDocument extends ConstrainedTextDocument {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -2365343700823017896L;

	/** revision number supplied by Team Connection */
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
	public MAXOnlyLettersWithoutSpaceDocument() {
		this(Integer.MAX_VALUE);
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructor.
	 *
	 * @param maxLength
	 *            the maximum length
	 */
	// ---------------------------------------------------------------------
	public MAXOnlyLettersWithoutSpaceDocument(int maxLength) {
		super(maxLength);
	}

	// ---------------------------------------------------------------------
	/**
	 * Determines if the text can be inserted.
	 *
	 * @param offset
	 *            the offset at which the text should be inserted
	 * @param text
	 *            the text to be inserted
	 * @param attributes
	 *            the set of attributes for the text
	 * @exception BadLocationException
	 *                if the offset is invalid
	 */
	// ---------------------------------------------------------------------
	@Override
	public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException {
		if (text != null) {
			boolean onlyLetter = true;
			char[] buf = text.toCharArray();
			for (int i = 0; (i < buf.length) && onlyLetter; ++i) {
				if (!Character.isLetter(buf[i])) {
					onlyLetter = false;
				}
				if (Character.isWhitespace(buf[i])) {
					onlyLetter = isSpaceAllowed;
				}

			}
			if (onlyLetter) {
				super.insertString(offset, new String(buf), attributes);
			}
		}
	}

	/**
	 * @return Returns the isSpaceAllowed.
	 */
	public boolean isSpaceAllowed() {
		return isSpaceAllowed;
	}

	/**
	 * @param isSpaceAllowed
	 *            The isSpaceAllowed to set.
	 */
	public void setSpaceAllowed(boolean isSpaceAllowed) {
		this.isSpaceAllowed = isSpaceAllowed;
	}
}
