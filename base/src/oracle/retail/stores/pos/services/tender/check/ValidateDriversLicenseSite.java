/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/ValidateDriversLicenseSite.java /main/13 2012/03/29 15:26:14 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    ohorne    08/18/11 - APF: Check cleanup
 *    tksharma  07/29/11 - Moved EncryptionUtily from foundation project to
 *                         EncryptionClient
 *    rrkohli   07/19/11 - encryption cr
 *    rrkohli   07/19/11 - encryption CR
 *    rrkohli   06/22/11 - Encryption CR
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   29 Jan 2004 08:57:32   Tim Fritz
 * Allowing an invalid driver's license format is now checking security access.
 * 
 *    Rev 1.2   Dec 08 2003 11:20:02   bwf
 * Send letter when necessary.
 * Resolution for 3558: App hangs when Validate Driver License Format = No and tender with Check
 * 
 *    Rev 1.1   Nov 13 2003 16:56:30   bwf
 * Create bad card swipe scree for check id entry.
 * Resolution for 3429: Check/ECheck Tender
 * 
 *    Rev 1.0   Nov 07 2003 16:11:54   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates the drivers license if needed.
 * 
 * @version $Revision: /main/13 $
 */
public class ValidateDriversLicenseSite extends PosSiteActionAdapter
{
  private static final long serialVersionUID = 744217169324718523L;

  /** revision number **/
  public static final String revisionNumber = "$Revision: /main/13 $";

  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive
   * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
   */
  @Override
  public void arrive(BusIfc bus)
  {
    TenderCargo cargo = (TenderCargo) bus.getCargo();
    HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
    boolean mailLetter = false;
    
    // if not swiped and DL
    if (tenderAttributes.get(TenderConstants.MSR_MODEL) == null
        && tenderAttributes.get(TenderConstants.ID_TYPE).equals("DriversLicense"))
    {
      // if need to validate license
      UtilityIfc util;
      try
      {
        util = Utility.createInstance();
      }
      catch (ADOException e)
      {
        String message = "Configuration problem: could not instantiate UtilityIfc instance";
        logger.error(message, e);
        throw new RuntimeException(message, e);
      }
      if (util.getParameterValue("ValidateDriverLicenseFormat", "Y").equals("Y"))
      {
        try
        {
          EncipheredDataIfc checkID = (EncipheredDataIfc) tenderAttributes
              .get(TenderConstants.ENCIPHERED_DATA_ID_NUMBER);

          byte[] decryptedCheckID = checkID.getDecryptedNumber();
          util.validateDriversLicense(decryptedCheckID,
              (String) tenderAttributes.get(TenderConstants.ID_STATE),
              (String) tenderAttributes.get(TenderConstants.ID_COUNTRY));

          Util.flushByteArray(decryptedCheckID);
          mailLetter = true;
        }
        catch (TenderException e)
        {
          // display error message
          TenderErrorCodeEnum errorCode = e.getErrorCode();
          cargo.setAccessFunctionID(RoleFunctionIfc.ACCEPT_INVALID_DL_FORMAT);
          if (errorCode == TenderErrorCodeEnum.INVALID_LICENSE)
          {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "InvalidLicense");
          }
        }
      }
      else
      {
        mailLetter = true; // dont validate mail letter
      }
    }
    else if (tenderAttributes.get(TenderConstants.MSR_MODEL) != null)
    {
      // make sure swiped id was ok
      MSRModel msrModel = (MSRModel) tenderAttributes.get(TenderConstants.MSR_MODEL);

      byte[] track2 = msrModel.getTrack2Data();
      // if the track 2 data is null or too little data, it is bad
      if (!(track2 != null && track2.length > 1))
      {
        displayBadSwipe(bus);
      }
      else
      {
        mailLetter = true;
      }
    }
    else
    {
      mailLetter = true;
    }

    // mail letter if needed
    if (mailLetter)
    {
      bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
  }

  /**
   * This method displays the bad id swipe error.
   * 
   * @param bus
   */
  protected void displayBadSwipe(BusIfc bus)
  {
    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    TenderCargo cargo = (TenderCargo) bus.getCargo();
    HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
    // Display error message
    DialogBeanModel dialogModel = new DialogBeanModel();
    String args[] = { (String) tenderAttributes.get(TenderConstants.ID_TYPE) };
    dialogModel.setArgs(args);
    dialogModel.setResourceID("BadMSRReadError");
    dialogModel.setType(DialogScreensIfc.ERROR);
    dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
  }
}
