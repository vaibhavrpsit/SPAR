/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/IppPrinterActionGroup.java /main/18 2013/11/20 12:06:45 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       11/16/13 - add default printer bean for pos to be consistent
 *                         with mpos
 *    jswan     10/10/13 - Fixed issue with sending eReceipt when network
 *                         printer offline or misconfigured.
 *    blarsen   10/03/12 - Changed cached FOP printer to a hash map. This
 *                         supports MPOS' multi-network-printer feature. Each
 *                         MPOS device can be assigned a unique network
 *                         printer.
 *    blarsen   09/28/12 - Added support for MPOS' multi-network-printer
 *                         feature. The printer's bean ID is now passed into
 *                         the various printDocument() and related methods.
 *    cgreene   12/20/11 - refactor some actiongroup settings into print
 *                         manager
 *    cgreene   12/19/11 - update scripting constants
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    cgreene   10/26/10 - refactor alwaysPrintLineFeed into
 *                         AbstractPrinterGroup
 *    rsnayak   10/26/10 - Bill Pay ereceipt changes
 *    rsnayak   10/07/10 - Bill Pay E-Receipt fix
 *
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    02/05/10 - merged with Jack's changes for ereceipt printing
 *    jswan     02/04/10 - Fixing two defects (HPQC 261 and 680), issue with
 *                         gift reciepts and returns, email not sent for sales
 *                         when configured for network printer.
 *    abondala  01/03/10 - update header date
 *    acadar    12/16/09 - cleanup
 *    acadar    12/15/09 - cleanup and refactoring of PdfReceiptPrinter's use
 *                         in POSPrinterActionGroup
 *    acadar    12/14/09 - cleanup
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.manager.device.FoReceiptPrinter;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedDocumentManager;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;
import oracle.retail.stores.printing.PrintRequestAttributes;
import oracle.retail.stores.printing.PrintRequestAttributesIfc;

/**
 * IppPrinterActionGroup defines the IppPrinter specific device operations
 * available to POS applications.
 */
public class IppPrinterActionGroup extends AbstractPrinterActionGroup
{
    private static final long serialVersionUID = 6862257207552684288L;

    /** PrintService instance*/
    private String printService;

    /** printer mine type*/
    private String printMimeType = "application/pdf";

    /** Printer locale*/
    private Locale locale;

    /** Paper orientation*/
    private String orientation;

    /** Number of copies*/
    private int copies = 1;

    /** Media*/
    private String media;

    /**Sheet Collate flag */
    private boolean sheetCollate = false;

    /**Sides */
    private String sides;

    /** Quality*/
    private String quality;

    /** Configuration file for fop
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    private String fopConfig;

    /** Configuration file for xml
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    private String xmlConfig;

    /** Data files location*/
    private String dataLocation;

    /** boolean that indicates if data files will be deleted
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    private boolean dataDelete;

    /**
     * Formats a document and sends it to the printer
     * @param EYSPrintableDocumentIfc
     * @throws DeviceException if error occurs
     */
    public void printDocument(EYSPrintableDocumentIfc document) throws DeviceException
    {
        printDocument(null, document);
    }

    /**
     * Formats a document and sends it to the printer.
     *
     * @param printerID
     * @param EYSPrintableDocumentIfc
     * @throws DeviceException if error occurs
     */
    public void printDocument(String printerID, EYSPrintableDocumentIfc document) throws DeviceException
    {
        boolean isCleanReceipt = false;
        if(document instanceof BlueprintedReceipt && ((BlueprintedReceipt)document).getParameterBean() instanceof ReceiptParameterBeanIfc)
        {
            ReceiptParameterBeanIfc parameters = (ReceiptParameterBeanIfc)((BlueprintedReceipt)document).getParameterBean();
            if(parameters.getTransaction() instanceof SaleReturnTransactionIfc)
            {
                if(parameters.isEreceipt())
                {
                    isCleanReceipt = true;
                }

            }
            else if (parameters.getTransaction() instanceof BillPayTransactionIfc)
            { // Added for bill pay transactions to print e-receipts
                    if (parameters.isEreceipt())
                    {
                        isCleanReceipt = true;
                    }
            }
        }

        // If eReceipt then create PdfReceiptPrinter object instead of FoReceiptPrinter.
        ReceiptPrinterIfc receiptPrinter = getReceiptPrinter(printerID, document, isCleanReceipt);

        // create new Receipt
        applyReceiptPrinter(document, receiptPrinter);

        // print Receipt
        document.printDocument();
    }

    /**
     * PrintNormal
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#printNormal
     */
    public void printNormal(int station, String data) throws DeviceException
    {
        ReceiptPrinterIfc receiptPrinter = getReceiptPrinter(null);
        receiptPrinter.printJob(data);
    }

    /**
     * Creates an instance of the XMLReceiptPrinter
     * @param EYSPrintableDocumentIfc document
     * @return XmlReceiptPrinter
     */
    protected ReceiptPrinterIfc getReceiptPrinter(String printerID) throws DeviceException
    {
        FoReceiptPrinter fopReceiptPrinter = getPrinter(printerID, false);
        fopReceiptPrinter.setCleanReceipt(false);

        // for reports the filename consists of the blueprintName + registerid + timestamp
        // for all the rest, filename is the transaction id
        String blueprintName = "Default";

        String fileName = blueprintName + System.currentTimeMillis();
        fopReceiptPrinter.setOutputFileName(fileName);

        //set the blueprint name in the IppPrinter so we can  locate the associated fop
        //load the xml configuration file
        fopReceiptPrinter.setXmlDocument(getXMLTemplate(blueprintName));
        fopReceiptPrinter.setFopTemplate(getFOPTemplate(blueprintName));

        //initializeIppPrinter();
        return fopReceiptPrinter;
    }

    /**
     * Creates an instance of the XMLReceiptPrinter
     * @param EYSPrintableDocumentIfc document
     * @return XmlReceiptPrinter
     */
    protected ReceiptPrinterIfc getReceiptPrinter(String printerID, EYSPrintableDocumentIfc document, boolean isCleanReceipt) throws DeviceException
    {

        FoReceiptPrinter fopReceiptPrinter = getPrinter(printerID, isCleanReceipt);
        fopReceiptPrinter.setCleanReceipt(isCleanReceipt);

        // for reports the filename consists of the blueprintName + registerid + timestamp
        // for all the rest, filename is the transaction id
        String blueprintName = (((BlueprintedReceipt)document).getParameterBean().getDocumentType());

        String fileName = blueprintName + System.currentTimeMillis();
        if ( ((BlueprintedReceipt)document).getParameterBean() instanceof ReceiptParameterBeanIfc)
        {
            ReceiptParameterBeanIfc rpBean = ((ReceiptParameterBeanIfc)((BlueprintedReceipt)document).getParameterBean());
            if(rpBean.getTransaction() != null)
            {
                fileName = rpBean.getTransaction().getTransactionID();
            }
            if (!Util.isEmpty(rpBean.getEReceiptFileNameAddition()))
            {
                fileName = fileName + rpBean.getEReceiptFileNameAddition();
            }
        }

        //set the blueprint name in the IppPrinter so we can  locate the associated fop
        fopReceiptPrinter.setOutputFileName(fileName);

        //load the xml configuration file
        fopReceiptPrinter.setXmlDocument(getXMLTemplate(blueprintName, isCleanReceipt));
        fopReceiptPrinter.setFopTemplate(getFOPTemplate(blueprintName, isCleanReceipt));

        return fopReceiptPrinter;
    }


    /**
     * Gets the printer based on the specified printerID or, if empty, the default printer.
     *
     * @param printerID  The ID for the printer to return.
     * @return The printer associated with the specified ID.
     * @deprecated in 14.0 use getPrinter(String printerID, boolean isCleanReceipt) instead
     */
    public FoReceiptPrinter getPrinter(String printerID)
    {
        return getPrinter(printerID, false);
    }

    /**
     * Gets the printer based on the specified printerID or, if empty, the default printer.
     *
     * @param printerID  The ID for the printer to return.
     * @param isCleanReceipt Indicates if the caller has request an email receipt.
     * @return The printer associated with the specified ID.
     */
    public FoReceiptPrinter getPrinter(String printerID, boolean isCleanReceipt)
    {
        String localPrinterID = printerID;
        if (Util.isEmpty(localPrinterID))
        {
            // If caller requests a clean receipt, i.e. an email receipt, then
            // the default printer configuration is the IPP email printer.
            // Otherwise it is the real IPP printer configuration.
            if (isCleanReceipt)
            {
                localPrinterID = FoReceiptPrinter.BEAN_KEY_EMAIL_IPP;
            }
            else 
            {
                localPrinterID = (String)BeanLocator.getDeviceBean(FoReceiptPrinter.BEAN_KEY_IPP_DEFAULT);
            }
        }
        
        return lookupFopReceiptPrinter(localPrinterID);
    }

    /**
     * Gets the Printer attributes
     * @return
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    public PrintRequestAttributesIfc getSettings()
    {
        if(settings == null)
        {
            settings = new PrintRequestAttributes();
        }

        settings.setCopies(1);
        settings.setMedia(getMedia());
        settings.setOrientation(getOrientation());
        settings.setQuality(getQuality());
        settings.setSheetCollate(this.isSheetCollate());
        settings.setSides(this.getSides());
        settings.setLocale(this.getLocale());
        settings.setPrintMimeType(this.getPrintMimeType());
        settings.setFoMimeType(this.getFoMimeType());
        settings.setFoFileExtension(this.getFoFileExtension());
        settings.setPrintServiceName(getPrintService());
        settings.setDataLocation(this.getDataLocation());
        settings.setFactoryInstance(this.getFactoryInstance());
        return settings;
    }

    /**
     * Gets the configuration for the fop
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     * @return
     */
    public String getFopConfig()
    {
        return fopConfig;
    }

    /**
     *
     * @param fopConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public void setFopConfig(String fopConfig)
    {
        this.fopConfig = fopConfig;
    }

    /**
     * gets the xml configuration
     * @return
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public String getXmlConfig()
    {
        return xmlConfig;
    }

    /**
     * Sets the xmlConfiguration file
     * @param xmlConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public void setXmlConfig(String xmlConfig)
    {
        this.xmlConfig = xmlConfig;
    }

    /**
     * Gets the Printer Locale
     * @return
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Sets the printer locale
     * @param locale
     */
    public void setLocale(String locale)
    {
        //initialize to the default  locale
        if( Util.isEmpty(locale))
        {
            this.locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        }
        else
        {
            String[] localeArray = {"","",""};
            StringTokenizer tokenizer = new StringTokenizer(locale, "_");
            int i = 0;

            while (tokenizer.hasMoreTokens())
            {
                localeArray[i] = tokenizer.nextToken();
                i = i +1 ;
            }
            this.locale = new Locale(localeArray[0], localeArray[1], localeArray[2]);
        }
    }

    /**
     * Gets the Print Service
     * @return
     */
    public String getPrintService()
    {
        return printService;
    }
    /**
     * Sets the print service
     * @param printService
     */
    public void setPrintService(String printService)
    {
        this.printService = printService;
    }

    /**
     * Indicates if generated xml and fop data can be deleted
     * @return boolean
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public boolean isDataDelete()
    {
        return dataDelete;
    }

    /**
     * Sets the data delete
     * @param dataDelete
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public void setDataDelete(boolean dataDelete)
    {
        this.dataDelete = dataDelete;
    }

    /**
     * Gets the data location
     * @return
     */
    public String getDataLocation()
    {
        return dataLocation;
    }

    /**
     * Sets the data location
     * @param dataLocation
     */
    public void setDataLocation(String dataLocation)
    {
        this.dataLocation = dataLocation;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#getOrientation()
     */
    public String getOrientation()
    {
        return orientation;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setOrientation(java.lang.String)
     */
    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#getCopies()
     */
    public int getCopies()
    {
        return copies;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setCopies(int)
     */
    public void setCopies(int copies)
    {
        this.copies = copies;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#getMedia()
     */
    public String getMedia()
    {
        return media;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setMedia(java.lang.String)
     */
    public void setMedia(String media)
    {
        this.media = media;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#isSheetCollate()
     */
    public boolean isSheetCollate()
    {
        return sheetCollate;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setSheetCollate(boolean)
     */
    public void setSheetCollate(boolean sheetCollate)
    {
        this.sheetCollate = sheetCollate;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#getSides()
     */
    public String getSides()
    {
        return sides;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setSides(java.lang.String)
     */
    public void setSides(String sides)
    {
        this.sides = sides;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#getQuality()
     */
    public String getQuality()
    {
        return quality;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.printing.PrintRequestAttributesIfc#setQuality(java.lang.String)
     */
    public void setQuality(String quality)
    {
        this.quality = quality;
    }

    /**
     * @return the printMimeType
     */
    public String getPrintMimeType()
    {
        return printMimeType;
    }

    /**
     * @param printMimeType the printMimeType to set
     */
    public void setPrintMimeType(String printMimeType)
    {
        this.printMimeType = printMimeType;
    }
}