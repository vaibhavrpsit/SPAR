/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/houseaccount/HouseAccountTenderExpDateUISite.java /rgbustores_13.4x_generic_branch/3 2011/08/01 10:46:35 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  07/29/11 - Moved EncryptionUtily from foundation project to
 *                         EncryptionClient
 *    ohorne    07/15/11 - removed card data on undo or cancel
 *    ohorne    06/01/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.houseaccount;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Determines whether a screen prompting for Exp Date needs to be displayed
 */
@SuppressWarnings("serial")
public class HouseAccountTenderExpDateUISite extends PosSiteActionAdapter
{
  protected KeyStoreEncryptionManagerIfc encryptionManager;

  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive
   * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
   */
  @Override
  public void arrive(BusIfc bus)
  {
    // If we have an MSR model, then no need to prompt for exp. date,
    // otherwise prompt the user unless we have a House card for which
    // no exp date is required
    TenderCargo cargo = (TenderCargo) bus.getCargo();
    if (cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL) == null)
    {
      boolean expDateRequired = false;
      try
      {
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        expDateRequired = pm.getBooleanValue(ParameterConstantsIfc.HOUSEACCOUNT_HouseCardExpDateRequired);
        if (expDateRequired)
        {
          showCreditCardExpDateScreen(bus);
          return;
        }
      }
      catch (ParameterException e)
      {
        logger.error("HouseCardExpDateRequired parameter not found");
      }
    }
    // this will only get mailed if we don't need to prompt for an exp. date
    bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
  }

  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart
   * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
   */
  @Override
  public void depart(BusIfc bus)
  {
    if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
    {
      // Get information from UI
      POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
      TenderCargo cargo = (TenderCargo) bus.getCargo();
      // if expiration date is manually entered encrypt it.
      String uiDate = null;
      try
      {
        uiDate = formatExpDate(ui.getInput());
      }
      catch (TenderException te)
      {
        if (te.getErrorCode() == TenderErrorCodeEnum.INVALID_EXPIRATION_DATE)
        {
          showCreditCardExpDateScreen(bus);
          return;
        }
      }
      String expirationDate = getEncryptedData(uiDate.getBytes());
      cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE, expirationDate);
    }
    else if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO)
        || bus.getCurrentLetter().getName().equals(CommonLetterIfc.CANCEL))
    {
      // remove card data
      TenderCargo cargo = (TenderCargo) bus.getCargo();
      cargo.getTenderAttributes().remove(TenderConstants.ENCIPHERED_CARD_DATA);
    }
  }

  /**
   * Format Expiration date from MMYYYY To YYMM
   * 
   * @param date
   * @return
   * @throws TenderException
   */
  private String formatExpDate(String date) throws TenderException
  {
    if (date == null)
    {
      return null;
    }
    try
    {
      EYSDate eysDate = Utility.createInstance().parseExpirationDate(EYSDate.CARD_FORMAT_MMYYYY, date);
      return eysDate.toFormattedString(EYSDate.FORMAT_YYMM);
    }
    catch (ADOException e)
    {
      String message = "Configuration problem: could not instantiate UtilityIfc instance";
      logger.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  /**
   * Display Card Expiration Date Screen
   * 
   * @param bus
   */
  private void showCreditCardExpDateScreen(BusIfc bus)
  {
    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    TDOUIIfc tdo = null;
    try
    {
      tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CreditExpDate");
    }
    catch (TDOException tdoe)
    {
      tdoe.printStackTrace();
    }
    ui.showScreen(POSUIManagerIfc.CREDIT_EXP_DATE, tdo.buildBeanModel(null));
  }

  /**
   * This method calls the encryption manager to encrypt the data.
   * 
   * @param byte[] text
   * @throws EncryptionServiceException if unable to encrypt the data
   **/

  public String getEncryptedData(byte[] clearText)
  {
    String encryptedText = null;
    if (clearText != null)
    {
      try
      {
        encryptedText = getEncryptionManager().getBase64encode(getEncryptionManager().encrypt(clearText));
      }
      catch (EncryptionServiceException e)
      {
        String message = "unable to encrypt the text";
        throw new RuntimeException(message, e);
      }
    }
    return encryptedText;
  }

  /**
   * Lazy initialization of the KeyStoreEncryptionManagerIfc instance.
   * 
   * @return the KeyStoreEncryptionManagerIfc instance.
   */
  protected KeyStoreEncryptionManagerIfc getEncryptionManager()
  {
    if (encryptionManager == null)
    {
      encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway.getDispatcher().getManager(
          KeyStoreEncryptionManagerIfc.TYPE);
    }
    return encryptionManager;
  }

}
