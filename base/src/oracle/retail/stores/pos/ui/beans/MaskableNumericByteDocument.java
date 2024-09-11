/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MaskableNumericByteDocument.java /rgbustores_13.4x_generic_branch/6 2011/09/08 15:52:44 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/08/11 - removed private member 'maskChar' in favor of
 *                         DomainUtil.getMaskChar()
 *    ohorne    08/04/11 - added DomainUtil.getMaskChar()
 *    tksharma  07/29/11 - Moved EncryptionUtily from foundation project to
 *                         EncryptionClient
 *    cgreene   06/17/11 - added support for automatically masking input like a
 *                         card number
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         1/4/2008 4:46:41 PM    Alan N. Sinton  CR
 *       29849: Refactor of customer table to use encrypted, hashed and masked
 *        fields.
 *  2    360Commerce 1.1         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *       29677: Check in changes per code review.  Reviews are Michael Barnett
 *        and Tony Zgarba.
 *  1    360Commerce 1.0         11/30/2007 1:24:39 AM  Alan N. Sinton  CR
 *       29677: To support protection of PAN data when swiped from MSR.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Arrays;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import oracle.retail.stores.domain.utility.DomainUtil;

/**
 * Extended NumericByteDocument to allow a mask character to be used.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/6 $
 */
public class MaskableNumericByteDocument extends NumericByteDocument
{
  private static final long serialVersionUID = 3687010632418952869L;

  private boolean cardNumber;

  /**
   * Constructor. Defaults size to maximum.
   */
  public MaskableNumericByteDocument()
  {
    this(Integer.MAX_VALUE);
  }

  /**
   * Constructor.
   * 
   * @param maxLength the maximum length
   */
  public MaskableNumericByteDocument(int maxLength)
  {
    this(maxLength, false);
  }

  /**
   * Constructor.
   * 
   * @param maxLength the maximum length
   * @param cardNumber mask the contents as if it were a credit card.
   */
  public MaskableNumericByteDocument(int maxLength, boolean cardNumber)
  {
    super(maxLength);
    this.cardNumber = cardNumber;
  }

  /**
   * Determines if the text can be inserted. Overridden to insert mask chars as
   * needed.
   * 
   * @param offset the offset at which the text should be inserted
   * @param text the text to be inserted
   * @param attributes the set of attributes for the text
   * @exception BadLocationException if the offset is invalid
   */
  public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException
  {
    super.insertString(offset, text, attributes);
    if (isCardNumber() && isNumeric(text))
    {
      maskCardNumber();
    }
  }

  /**
   * Removes text from this model.
   * 
   * @param offset the offset at which text will be removed
   * @param length the number of characters to be removed
   */
  @Override
  public void remove(int offset, int length) throws BadLocationException
  {
    super.remove(offset, length);
    if (isCardNumber())
    {
      maskCardNumber();
    }
  }

  /**
   * Add six maskCars to the text until the input looks like a masked card
   * number.
   * <p>
   * 
   * <pre>
   * Masking examples:
   * 16 digits: 123456******1234
   * 14 digits: 1234******1234
   * 12 digits: 12******1234
   * 10 digits: ******1234
   * </pre>
   */
  protected void maskCardNumber() throws BadLocationException
  {
    // are there any digits?
    byte[] text = getTextBytes();
    if (text == null)
    {
      return;
    }
    StringBuilder digits = new StringBuilder(text.length);
    int len = text.length;
    for (int i = 0; i < len; i++)
    {
      if (Character.isDigit((char) text[i]))
      {
        digits.append((char) text[i]);
      }
    }
    if (digits.length() == 0)
    {
      // there are no digits. Remove all the masking.
      super.remove(0, len);
      return;
    }

    // there are some digits, so mask them.
    char[] chars = new char[6];
    Arrays.fill(chars, DomainUtil.getMaskChar());
    String mask = new String(chars);

    // strip out all the digits, then insert the mask before the last four.
    int idx = (digits.length() > 4) ? digits.length() - 4 : 0;
    digits.insert(idx, mask);
    // calling replace ends up calling remove above, so call super.remove
    super.remove(0, len);
    super.insertStringWithoutCharValidation(0, digits.toString(), SimpleAttributeSet.EMPTY);
  }

  /**
   * Sets the text as bytes
   * 
   * @param byte array
   */
  @Override
  public void setTextBytes(byte[] value)
  {
    if (value != null)
    {
      boolean numericOrMask = true;
      int len = value.length;
      for (int i = 0; (i < len) && numericOrMask; ++i)
      {
        if (!Character.isDigit((char) value[i]) && value[i] != DomainUtil.getMaskChar())
        {
          numericOrMask = false;
        }
      }
      if (numericOrMask)
      {
        byte[] b = new byte[1];
        for (int i = 0; i < len; i++)
        {
          b[0] = value[i];
          String text = new String(b);
          try
          {
            insertSingleByteString(i, text, null);
          }
          catch (BadLocationException e)
          {
          }
        }
      }
    }
  }

  /**
   * Return true if this field should treat its contents like a card number,
   * i.e. whether it will mask the middle six chars.
   * 
   * @return the cardNumber
   */
  public boolean isCardNumber()
  {
    return cardNumber;
  }

  /**
   * Set whether this field should treat its contents like a card number, i.e.
   * whether it will mask the middle six chars.
   * 
   * @param cardNumber the cardNumber to set
   */
  public void setCardNumber(boolean cardNumber)
  {
    this.cardNumber = cardNumber;
  }

  /**
   * Overridden in this class to return true if text also contains the mask
   * char.
   */
  @Override
  protected boolean isNumeric(String text)
  {
    boolean numeric = true;
    int len = text.length();

    for (int i = 0; i < len; i++)
    {
      char ch = text.charAt(i);
      if (!Character.isDigit(ch) && ch != DomainUtil.getMaskChar())
      {
        numeric = false;
        break;
      }
    }
    return numeric;
  }
}
