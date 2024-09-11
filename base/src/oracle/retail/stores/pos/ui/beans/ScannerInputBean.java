/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ScannerInputBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/28/2007 1:29:33 PM  Leona R. Slepetis
 *         removed sensitive data logging PABP FR15
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:04 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:04 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/09 18:32:27  rzurga
 *   @scr 3991 Tidy up the javadoc comments
 *
 *   Revision 1.2  2004/03/09 07:19:05  rzurga
 *   @scr 3991 Tidy up comments
 *
 *   Revision 1.1  2004/03/08 18:51:18  rzurga
 *   @scr 3991 Rename ReturnReceiptInputBean to ScannerInputBean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.gui.UIConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;

//---------------------------------------------------------------------
/**
 * Receive scanner data. 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------

public class ScannerInputBean
    extends DataInputBean
    implements UIConstantsIfc
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.BaseBeanAdapter.class);

    //----------------------------------------------------------------------
    /**
        Default Constructor.
    **/
    //----------------------------------------------------------------------
    public ScannerInputBean()
    {
        super();
    }
    //---------------------------------------------------------------------
    /**
        Receive scanner data. Called by the UI Framework.
        @param data DeviceModelIfc
     **/
    //---------------------------------------------------------------------
    public void setScannerData(DeviceModelIfc data)
    {

        ScannerModel scannerModel = (ScannerModel)data;

        // Strip any leading alpha characters from the JPOS scanner input.
        // UPC codes are always numeric
        int index = 0;
        String temp = new String(scannerModel.getScanLabelData());

        while (Character.isLetter(temp.charAt(index)))
        {
            index++;
        }
        final String numeric = temp.substring(index);

        processScannerInput(numeric);

    }

    //---------------------------------------------------------------------
    /**
        Process the scanner data in preparation to be shown on screen.  Mail
        appropriate letter (Next, Failure) depending on results.
        @param the String data from the scanner
     **/
    //---------------------------------------------------------------------
    public void processScannerInput(String data)
    {
        TransactionIDIfc txnID =
            (TransactionIDIfc)DomainGateway
                .getFactory()
                .getTransactionIDInstance();
        String letterName = "Failure";
        if (txnID != null)
        {
            if (data.length()
                == (TransactionID.getTransactionIDLength()
                    + TransactionID.getBarcodeDateLength()))
            {
                String dateString = getDateStamp(data);
                data =
                    data.substring(0, TransactionID.getTransactionIDLength());
                TransactionIDIfc tranID = validateReceiptData(data);
                if (tranID != null)
                {
                    // There is a mismatch between the "official" lengths of these
                    // 2 items in the transactionID class and the input boxes.
                    // Barcode is created using the official lengths while the input box
                    // sizes are set in the UI XML file.
                    String store = tranID.getFormattedStoreID();
                    if (store.length() > TransactionID.getStoreIDLength())
                        store =
                            store.substring(
                                store.length()
                                    - TransactionID.getStoreIDLength());
                    String reg = tranID.getFormattedWorkstationID();
                    if (reg.length() > TransactionID.getWorkstationIDLength())
                        reg =
                            reg.substring(
                                reg.length()
                                    - TransactionID.getWorkstationIDLength());

                    DataInputBeanModel dataModel =
                        (DataInputBeanModel)beanModel;
                    dataModel.setValue("PurchaseDateField", dateString);
                    dataModel.setValue("StoreNumberField", store);
                    dataModel.setValue("RegisterNumberField", reg);
                    dataModel.setValue(
                        "TransactionNumberField",
                        tranID.getFormattedTransactionSequenceNumber());
                    letterName = "Next";
                    updateBean();

                }
            }
        }

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(letterName), true);

    }

    //---------------------------------------------------------------------
    /**
        Gets the date stamp section of the barcode.  Returns empty string
        if there is any trouble.
     **/
    //---------------------------------------------------------------------
    public String getDateStamp(String barcode)
    {
        String dateString = "";
        TransactionIDIfc txnID =
            (TransactionIDIfc)DomainGateway
                .getFactory()
                .getTransactionIDInstance();
        try
        {
            String tempString =
                barcode.substring(TransactionID.getTransactionIDLength());
            SimpleDateFormat df =
                new SimpleDateFormat(TransactionID.getBarcodeDateFormat());
            Date realDate = df.parse(tempString);
            EYSDate eysDate = new EYSDate(realDate);
            dateString = eysDate.toFormattedString(SimpleDateFormat.SHORT, getDefaultLocale());
        }
        catch (Exception e)
        {
            // do nothing
        }

        return dateString;
    }

    //---------------------------------------------------------------------
    /**
        Attempts to create a TransactionID using the input data.  Returns null
        if the input string cannot be parsed correctly to create the object.
     **/
    //---------------------------------------------------------------------
    public TransactionIDIfc validateReceiptData(String receiptID)
    {
        TransactionIDIfc txnID =
            (TransactionIDIfc)DomainGateway
                .getFactory()
                .getTransactionIDInstance();

        try
        {
            txnID.setTransactionID(receiptID);
        }
        catch (Exception e)
        {
            txnID = null;
        }

        return txnID;
    }

}
