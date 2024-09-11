/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/blueprint/BlueprintedDocumentManager.java /main/33 2014/02/05 15:03:02 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  01/31/14 - released the stream handles
 *    arabalas  09/24/13 - reverting the changes
 *    arabalas  09/19/13 - closed the zipFile handles
 *    abondala  09/04/13 - initialize collections
 *    abhinavs  06/26/13 - Reverting Fortify fix
 *    abhinavs  06/25/13 - Fixing unreleased file resources issues reported by
 *                         Fortify
 *    sgu       04/24/13 - use correct path separator
 *    tksharma  12/11/12 - jdom update 2.0.4
 *    abhinavs  12/06/12 - Fixing HP fortify redundant null check issues
 *    mchellap  09/27/12 - Fixed fiscal receipt store copy printing
 *    abhineek  09/26/12 - fixed classcast exception for Franking POS
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    mchellap  05/09/12 - VLR receipt printing changes
 *    cgreene   12/20/11 - refactor some actiongroup settings into print
 *                         manager
 *    cgreene   12/19/11 - update scripting constants
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    vtemker   03/07/11 - Print Preview for Reports - fixed review comments
 *    vtemker   03/03/11 - Changes for Print Preview Reports Quickwin
 *    jkoppolu  09/23/10 - BUG#856, Only 'store copy' of the receipt should be
 *                         printed when the order is reprinted.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/13/10 - added support for blueprints in zip files
 *    cgreene   04/12/10 - add ability to search a list of directories for
 *                         blueprints
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/04/10 - added functionality for parameter
 *                         AutoPrintCustomerCopy for voids
 *    abondala  01/03/10 - update header date
 *    cgreene   04/10/09 - correct getBlueprint method which may not find a
 *                         recently added blueprint if a default one has
 *                         already been loaded
 *    cgreene   03/20/09 - added funct to re-read bpt file if its timestamp is
 *                         newer
 *    acadar    02/12/09 - use default locale for date/time printing in the
 *                         receipts
 *    cgreene   11/07/08 - remove old receipts from context and
 *                         blueprintsDisabled option
 *    cgreene   10/17/08 - added better npe message to getFileName
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt.blueprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Element;

import com.sun.xml.internal.txw2.Document;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.dtd.BptDocMgrScriptIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.receipt.AlterationReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManager;
import oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedDocumentManager.ReceiptConfiguration;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.receipts.model.Blueprint;
import oracle.retail.stores.receipts.xml.DocumentUtility;
import com.sun.xml.internal.txw2.Document;

/**
 * A printable document manager that uses {@link Blueprint}s to print the
 * desired receipt or report. The blueprints are retrieved from the file system
 * and cached for reuse.
 * 
 * @author cgreene
 * @since 13.1
 */
@SuppressWarnings("deprecation")
public class BlueprintedDocumentManager extends PrintableDocumentManager implements BptDocMgrScriptIfc
{
    /** debug logger */
    private static final Logger logger = Logger.getLogger(BlueprintedDocumentManager.class);
    
    /** path separator */
    public static final char PATH_SEPARATOR = '/';

    /** A cache of previously read blueprint (*.bpt) files. */
    protected Map<String, Blueprint> cacheBlueprints;

    /** A map of document types to receipt configuration. */
    protected Map<String, ReceiptConfiguration> receiptConfigs;

    /** The receipt document that can process the blueprint instructions. */
    private BlueprintedReceipt blueprintedReceipt;

    private BlueprintedFiscalReceipt blueprintedFiscalReceipt;

    /** Whether or not to persist the beans as (*.ser). */
    private boolean persistBeansAsDataObject;

    /** The directory containing (*.bpt) files and where to write (*.ser) files. */
    private String receiptsPath;

    /** The print preview text */
    private StringBuilder previewText;

    /** The directory containing (*.xml) files for ipp templates */
    private String templatesPathXML;

    /** The directory containing (*.xsl) files for ipp templates */
    private String templatesPathFOP;

    /** Whether or not to add header to every page */
    protected boolean repeatHeader = false;

    /** Blueprints used for printing header information */
    protected String headerBlueprints = "";

    /** Whether or not to add footer to every page */
    protected boolean repeatFooter = false;

    /** Blueprints used for printing footer information */
    protected String footerBlueprints = "";

    /**
     * Constructor.
     */
    public BlueprintedDocumentManager()
    {
        cacheBlueprints = new HashMap<String, Blueprint>(0);
        receiptConfigs = new HashMap<String, ReceiptConfiguration>(0);
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.receipt.PrintableDocumentManager#printReceipt
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc)
     */
    @Override
    public void printReceipt(SessionBusIfc bus, PrintableDocumentParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);

        persistBean(receiptDataBean);

        BlueprintedReceipt receipt = null;

        // Print receipts using BlueprintedIFiscalReceipts if fiscal printer is configured
        if (isFiscalPrintingEnabled)
        {
            // Print the fiscal receipt
            boolean fiscalReceiptPrinted = printFiscalReceipt(bus, receiptDataBean);

            // Print the non fiscal copies of the receipt (eg. store copy)
            if(fiscalReceiptPrinted && receiptDataBean instanceof ReceiptParameterBeanIfc)
            {
                ((ReceiptParameterBeanIfc) receiptDataBean).setPrintStoreReceipt(true);
                ((ReceiptParameterBeanIfc) receiptDataBean).setAutoPrintCustomerCopy(false);
            }
            // get the blueprint for this receipt from a repository
            Blueprint blueprint = getBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                    receiptDataBean.getDefaultLocale());
            
            // alter any setting on this blueprint based upon parameters
            blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);

            // get a BlueprintedReceipt for this blueprint
            receipt = (BlueprintedFiscalReceipt) getBlueprintedFiscalReceipt(blueprint);
            // set data object as bean onto receipt
            
            if(receipt != null)
            {
            receipt.setParameterBean(receiptDataBean);
            
            POSDeviceActions pda = new POSDeviceActions(bus);
            try
            {
                // print non-fiscal copies
                if ((blueprint.getCopies() > 1 || !fiscalReceiptPrinted))
                {
                    pda.printDocument(receipt);
                }
                // getPreviewText() should only come after the print is done
                // the previewText attribute of Receipt is populated only after
                previewText = receipt.getPreviewText();
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to print receipt. " + e.getMessage());

                if (e.getCause() != null)
                {
                    logger.warn("DeviceException caused by: " + Util.throwableToString(e.getCause()));
                }

                throw new PrintableDocumentException("DeviceException caught while attempting to print document type: "
                        + receiptDataBean.getDocumentType(), e);
            }
            }

        }
        else
        {

            // get the blueprint for this receipt from a repository
            Blueprint blueprint = getBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                    receiptDataBean.getDefaultLocale());
            // alter any setting on this blueprint based upon parameters
            blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);

            // get a BlueprintedReceipt for this blueprint
            receipt = getBlueprintedReceipt(blueprint);
            // set data object as bean onto receipt
            //Null check is performed
            if(receipt !=null)
            {
            receipt.setParameterBean(receiptDataBean);
            boolean vlrEnabled = Gateway.getBooleanProperty("application", "VLREnabled", false);
            
            if (vlrEnabled)
            {
                // Add properties for fixed length receipt printing
                receipt.setRepeatHeader(repeatHeader);
                receipt.setRepeatFooter(repeatFooter);
                receipt.setHeaderBlueprints(headerBlueprints);
                receipt.setFooterBlueprints(footerBlueprints);
                receipt.setFixedLengthReceipt(vlrEnabled);
            }
            
            POSDeviceActions pda = new POSDeviceActions(bus);
            try
            {

                pda.printDocument(receipt);
                // getPreviewText() should only come after the print is done
                // the previewText attribute of Receipt is populated only after
                previewText = receipt.getPreviewText();
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to print receipt. " + e.getMessage());

                if (e.getCause() != null)
                {
                    logger.warn("DeviceException caused by: " + Util.throwableToString(e.getCause()));
                }

                throw new PrintableDocumentException("DeviceException caught while attempting to print document type: "
                        + receiptDataBean.getDocumentType(), e);
            }  
        }  
        }
    }

    /**
     * Prints receipt customer copy using fiscal printer.
     * 
     * @param parameterBean
     */
    public boolean printFiscalReceipt(SessionBusIfc bus, PrintableDocumentParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {

        boolean printedAnything = false;
        // get the blueprint for this receipt from a repository
        Blueprint blueprint = getFiscalBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                receiptDataBean.getDefaultLocale());
        
        if (blueprint != null)
        {
            // alter any setting on this blueprint based upon parameters
            blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);
            
            // get a BlueprintedReceipt for this blueprint
            BlueprintedFiscalReceipt receipt = (BlueprintedFiscalReceipt) getBlueprintedFiscalReceipt(blueprint);
            // set data object as bean onto receipt
            receipt.setParameterBean(receiptDataBean);            
            receipt.setPreview(false);
            
            // send to the fiscal printer
            POSDeviceActions pda = new POSDeviceActions(bus);
            try
            {
                pda.printFiscalReceipt(receipt);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to print fiscal receipt. " + e.getMessage());

                if (e.getCause() != null)
                {
                    logger.warn("DeviceException caused by: " + Util.throwableToString(e.getCause()));
                }

                throw new PrintableDocumentException("DeviceException caught while attempting to print fiscal document type: "
                        + receiptDataBean.getDocumentType(), e);
            }
            printedAnything = true;
        }
        else
        {
            logger.debug("Fiscal blueprint not configured for receipt type " + receiptDataBean.getDocumentType());
        }
        
        return printedAnything;
        
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.receipt.PrintableDocumentManager#printGiftReceipt
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc)
     */
    @Override
    public void printGiftReceipt(SessionBusIfc bus, GiftReceiptParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        printReceipt(bus, receiptDataBean);
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.receipt.PrintableDocumentManager#printOrderReceipt
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc)
     */
    @Override
    public void printOrderReceipt(SessionBusIfc bus, OrderReceiptParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        printReceipt(bus, receiptDataBean);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc#
     * printShippingSlip
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc)
     */
    public void printShippingSlip(SessionBusIfc bus, PrintableDocumentParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        printReceipt(bus, receiptDataBean);
    }

    /**
     * Save this bean to disk for use with receipt builder editor.
     * 
     * @param parameterBean
     */
    protected void persistBean(PrintableDocumentParameterBeanIfc receiptDataBean)
    {
        if (receiptDataBean.getLocale() == null)
        {
            receiptDataBean.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
        }
        if (persistBeansAsDataObject)
        {
            StringTokenizer tokens = new StringTokenizer(getReceiptsPath(), ";");
            while (tokens.hasMoreTokens())
            {
                File dir = new File(tokens.nextToken());
                if (dir.isFile())
                {
                    if (!tokens.hasMoreTokens())
                    {
                        logger.error("No directory found to persist receipt data bean.");
                    }
                    continue;
                }
                if (!dir.exists() && !dir.mkdirs())
                {
                    logger.error("Unable to create directory " + dir);
                }
                File file = new File(dir, receiptDataBean.getDocumentType() + ".ser");
                try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));)
                {                   
                    oos.writeObject(receiptDataBean);
                    break;
                }
                catch (IOException e)
                {
                    logger.error("Unable to persist receipt data bean.", e);
                }
            }
        }
    }

    /**
     * Get whether to persist the parameter beans passed to this manager as
     * serialized files.
     * 
     * @return the persistBeansAsDataObject
     */
    public boolean isPersistBeansAsDataObject()
    {
        return persistBeansAsDataObject;
    }

    /**
     * @return the receiptsPath
     */
    public String getReceiptsPath()
    {
        return receiptsPath;
    }

    /**
     * Set whether to persist the parameter beans passed to this manager as
     * serialized files.
     * 
     * @param persistBeansAsDataObject the persistBeansAsDataObject to set
     */
    public void setPersistBeansAsDataObject(boolean persistBeansAsDataObject)
    {
        this.persistBeansAsDataObject = persistBeansAsDataObject;
    }

    /**
     * @param receiptsPath the receiptsPath to set
     */
    public void setReceiptsPath(String receiptsPath)
    {
        this.receiptsPath = receiptsPath;
    }

    /**
     * @return the templatesPathFOP
     */
    public String getTemplatesPathFOP()
    {
        return templatesPathFOP;
    }

    /**
     * @param templatesPathFOP the templatesPathFOP to set
     */
    public void setTemplatesPathFOP(String templatesPathFOP)
    {
        this.templatesPathFOP = templatesPathFOP;
    }

    /**
     * @return the templatesPathXML
     */
    public String getTemplatesPathXML()
    {
        return templatesPathXML;
    }

    /**
     * @param xmlFilesDirectory the xmlFilesDirectory to set
     */
    public void setTemplatesPathXML(String templatesPathXML)
    {
        this.templatesPathXML = templatesPathXML;
    }

    /**
     * @return the ippFOPTemplate
     */
    public String getIppFOPTemplate(String documentType)
    {
        ReceiptConfiguration config = getReceiptConfiguration(documentType);
        if (config == null)
        {
            return null;
        }
        return getTemplatesPathFOP() + PATH_SEPARATOR + config.getIppFOPTemplate();
    }

    /**
     * Returns the template file path relative to the working directory.
     * 
     * @return the ippXMLTemplate
     */
    public String getIppXMLTemplate(String documentType)
    {
        ReceiptConfiguration config = getReceiptConfiguration(documentType);
        if (config == null)
        {
            return null;
        }
        return getTemplatesPathXML() +  PATH_SEPARATOR + config.getIppXMLTemplate();
    }

    /**
     * Returns the template file path relative to the working directory.
     * 
     * @return the eReceiptFOPTemplate
     */
    public String getEReceiptFOPTemplate(String documentType)
    {
        ReceiptConfiguration config = getReceiptConfiguration(documentType);
        if (config == null)
        {
            return null;
        }
        return getTemplatesPathFOP() +  PATH_SEPARATOR + config.getEReceiptFOPTemplate();
    }

    /**
     * Returns the template file path relative to the working directory.
     * 
     * @return the eReceiptXMLTemplate
     */
    public String getEReceiptXMLTemplate(String documentType)
    {
        ReceiptConfiguration config = getReceiptConfiguration(documentType);
        if (config == null)
        {
            return null;
        }
        return getTemplatesPathXML() +  PATH_SEPARATOR + config.getEReceiptXMLTemplate();
    }

    /**
     * Remove the blueprint from the cache that matches the given name. The
     * entire directory path does not need to be specified, only the file name.
     * 
     * @param fileName
     * @return
     */
    public Blueprint removeCachedBlueprint(String fileName)
    {
        return cacheBlueprints.remove(fileName);
    }

    /**
     * Lazily init a {@link BlueprintedReceipt} and set the
     * <code>blueprint</code> onto it.
     * 
     * @return
     */
    protected BlueprintedReceipt getBlueprintedReceipt(Blueprint blueprint)
    {
        if (blueprintedReceipt == null)
        {
            blueprintedReceipt = (BlueprintedReceipt) BeanLocator.getApplicationBean(BlueprintedReceipt.BEAN_KEY);
            blueprintedReceipt.setDocumentManager(this);
        }
        blueprintedReceipt.setBlueprint(blueprint);
        return blueprintedReceipt;
    }

    /**
     * Lazily init a {@link BlueprintedReceipt} and set the
     * <code>blueprint</code> onto it.
     * 
     * @return
     */
    protected BlueprintedFiscalReceipt getBlueprintedFiscalReceipt(Blueprint blueprint)
    {
        if (blueprintedFiscalReceipt == null)
        {
            blueprintedFiscalReceipt = (BlueprintedFiscalReceipt) BeanLocator.getApplicationBean(BlueprintedFiscalReceipt.BEAN_KEY);
            blueprintedFiscalReceipt.setDocumentManager(this);
        }
        blueprintedFiscalReceipt.setBlueprint(blueprint);
        return blueprintedFiscalReceipt;
    }

    /**
     * Retrieve the blueprint for the specified bean. The name of the blueprint
     * XML file depends on the bean's document type. The blueprint is cached.
     * <p>
     * 
     * @param receiptDataBean
     * @return
     * @throws PrintableDocumentException if there is an error reading the file
     */
    protected Blueprint getBlueprint(String documentType, Locale locale, Locale defaultLocale)
            throws PrintableDocumentException
    {
        logger.debug("getting blueprint for: " + documentType);
        // get name of file for bean from resource bundle
        String localeString = "_" + locale;
        String fileName = getFileName(documentType, localeString);        
        Blueprint blueprint = null;

        // get blueprint from cache
        while (blueprint == null)
        {
            long bptFileLastModified = getReceiptLastModified(fileName);
            System.out.println("vaibhav");
            blueprint = cacheBlueprints.get(fileName);

            // check if file on harddrive has a newer timestamp
            if (blueprint != null)
            {
                if (bptFileLastModified > blueprint.getModifiedTime())
                {
                    blueprint = null; // force this method to re-read file.
                }
            }

            // get blueprint from file system
            if (blueprint == null)
            {
                // read a blueprint file
                try(InputStream fis = getReceiptInputStream(fileName);)
                {
                    SAXBuilder parser = new SAXBuilder();
                    Document doc = parser.build(fis);
                    // convert xml to a blueprint
                    blueprint = getBlueprint(doc, locale, defaultLocale);
                    blueprint.setModifiedTime(bptFileLastModified);
                    // cache
                    cacheBlueprints.put(fileName, blueprint);
                }
                catch (Exception e)
                {
                    if (localeString.equals(""))
                    {
                        throw new PrintableDocumentException("Unable to get blueprint for documentType=" + documentType
                                + ", fileName=" + fileName, e);
                    }
                }
            }

            if (localeString.equals(""))
            {
                break; // no more locals to check, break out.
            }
            localeString = localeString.substring(0, localeString.lastIndexOf('_'));
            fileName = getFileName(documentType, localeString);
        }

        return blueprint;
    }

    /**
     * Retrieve the blueprint for the specified bean. The name of the blueprint
     * XML file depends on the bean's document type. The blueprint is cached.
     * <p>
     * 
     * @param receiptDataBean
     * @return
     * @throws PrintableDocumentException if there is an error reading the file
     */
    protected Blueprint getFiscalBlueprint(String documentType, Locale locale, Locale defaultLocale)
            throws PrintableDocumentException
    {
        logger.debug("getting fiscal blueprint for: " + documentType);
        Blueprint blueprint = null;
        // get name of file for bean from resource bundle
        String localeString = "_" + locale;
        String fileName = getFiscalBlueprintFileName(documentType, localeString);
        if (fileName != null && fileName.length() > 0)
        {
            // get blueprint from cache
            while (blueprint == null)
            {
                long bptFileLastModified = getReceiptLastModified(fileName);
                blueprint = cacheBlueprints.get(fileName);

                // check if file on harddrive has a newer timestamp
                if (blueprint != null)
                {
                    if (bptFileLastModified > blueprint.getModifiedTime())
                    {
                        blueprint = null; // force this method to re-read file.
                    }
                }

                // get blueprint from file system
                if (blueprint == null)
                {
                    // read a blueprint file
                    try(InputStream fis = getReceiptInputStream(fileName);)
                    {
                        SAXBuilder parser = new SAXBuilder();
                        Document doc = parser.build(fis);
                        // convert xml to a blueprint
                        blueprint = getBlueprint(doc, locale, defaultLocale);
                        blueprint.setModifiedTime(bptFileLastModified);
                        // cache
                        cacheBlueprints.put(fileName, blueprint);
                    }
                    catch (Exception e)
                    {
                        if (localeString.equals(""))
                        {
                            throw new PrintableDocumentException("Unable to get blueprint for documentType="
                                    + documentType + ", fileName=" + fileName, e);
                        }
                    }
                }

                if (localeString.equals(""))
                {
                    break; // no more locals to check, break out.
                }
                localeString = localeString.substring(0, localeString.lastIndexOf('_'));
                fileName = getFiscalBlueprintFileName(documentType, localeString);
            }

        }

        return blueprint;
    }

    /**
     * Calls {@link DocumentUtility#getBlueprint(Document)}.
     * 
     * @param doc
     * @return
     */
    protected Blueprint getBlueprint(Document doc, Locale locale, Locale defaultLocale)
    {
        Blueprint blueprint = DocumentUtility.getBlueprint(doc, locale, defaultLocale);
        return blueprint;
    }

    /**
     * Determines whether we need to print customer copy of a void receipt. This
     * method will alter the blueprint and return a copy if is is altered.
     * 
     * @param blueprint
     * @param bus
     * @param receiptDataBean
     */
    protected Blueprint prepareBlueprint(Blueprint blueprint, SessionBusIfc bus,
            PrintableDocumentParameterBeanIfc receiptDataBean)
    {
        String docType = receiptDataBean.getDocumentType();
        if (docType.toUpperCase().startsWith("VOID"))
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String[] tenderTypes;
            try
            {
                tenderTypes = pm.getStringValues(ParameterConstantsIfc.PRINTING_AutoPrintCustomerCopy);
            }
            catch (ParameterException e)
            {
                logger.error("Parameter could not be read: " + ParameterConstantsIfc.PRINTING_AutoPrintCustomerCopy, e);

                // default to requirement values
                tenderTypes = new String[] {
                        TenderLineItemConstantsIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemConstantsIfc.TENDER_TYPE_DEBIT],
                        TenderLineItemConstantsIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE],
                        TenderLineItemConstantsIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK] };
            }

            TenderableTransactionIfc origTrans = (TenderableTransactionIfc)((ReceiptParameterBeanIfc) receiptDataBean).getTransaction();
            origTrans = ((VoidTransactionIfc) origTrans).getOriginalTransaction();
            // if transaction contains a tender matching any of these types,
            // return true
            Iterator<TenderLineItemIfc> iter = origTrans.getTenderLineItemsVector().iterator();
            while (iter.hasNext())
            {
                TenderLineItemIfc tender = iter.next();
                String currentType = tender.getTypeDescriptorString();
                for (int i = 0; i < tenderTypes.length; i++)
                {
                    if (tenderTypes[i].equalsIgnoreCase(currentType))
                    {
                        // set copies to be two
                        if (blueprint.getCopies() == 1)
                        {
                            blueprint = (Blueprint) blueprint.clone();
                            blueprint.setCopies(2);
                            return blueprint;
                        }
                        break;
                    }
                }
            }
        }
        if (receiptDataBean instanceof OrderReceiptParameterBeanIfc)
        {
            OrderReceiptParameterBeanIfc orpb = (OrderReceiptParameterBeanIfc) receiptDataBean;
            if (orpb.getServiceType() == OrderCargoIfc.SERVICE_PRINT_TYPE)
                blueprint.setCopies(1);
        }
        return blueprint;
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.manager.ConfigurableManager#configure
     * (org.w3c.dom.Element)
     */
    @Override
    protected void configure(Element xmlRoot)
    {
        Element[] dmProps = XMLUtility.getChildElements(xmlRoot, ELEM_PROPERTY);

        for (int y = 0; y < dmProps.length; y++)
        {
            try
            {
                String dmPropName = dmProps[y].getAttribute(ATTR_PROPNAME);
                String dmPropValue = dmProps[y].getAttribute(ATTR_PROPVALUE);
                String dmPropType = dmProps[y].getAttribute(ATTR_PROPTYPE);
                if (logger.isDebugEnabled())
                    logger.debug("set prop " + dmPropName + " to " + dmPropValue);
                ReflectionUtility.setProperty(this, dmPropName, dmPropType, dmPropValue);
            }
            catch (Exception e)
            {
                logger.error("Error reading config script " + getConfigScript(), e);
            }
        }

        Element[] receiptElements = XMLUtility.getChildElements(xmlRoot, ELEM_RECEIPT);

        for (int i = 0; i < receiptElements.length; i++)
        {
            String docType = receiptElements[i].getAttribute(ATTR_TYPE);           
            String fileName = receiptElements[i].getAttribute(ATTR_BLUEPRINT_FILENAME);
            if (logger.isDebugEnabled())
                logger.debug("set receipt " + docType + " to " + fileName);
            ReceiptConfiguration config = new ReceiptConfiguration(fileName);
            receiptConfigs.put(docType, config);
            String fiscalFileName = receiptElements[i].getAttribute(ATTR_FISCAL_BLUEPRINT_FILENAME);
            config.setFiscalBlueprintFileName(fiscalFileName);
            config.setIppFOPTemplate(receiptElements[i].getAttribute(ATTR_TEMPLATE_FILENAME_FOP));
            config.setIppXMLTemplate(receiptElements[i].getAttribute(ATTR_TEMPLATE_FILENAME_XML));
            config.setEReceiptFOPTemplate(receiptElements[i].getAttribute(ATTR_ERECEIPT_TEMPLATE_FILENAME_FOP));
            config.setEReceiptXMLTemplate(receiptElements[i].getAttribute(ATTR_ERECEIPT_TEMPLATE_FILENAME_XML));
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManager#
     * getAlterationParameterBeanInstance
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc)
     */
    protected AlterationReceiptParameterBeanIfc getAlterationParameterBeanInstance(SessionBusIfc bus,
            ReceiptParameterBeanIfc receiptParameters)
    {
        AlterationReceiptParameterBeanIfc bean = super.getAlterationParameterBeanInstance(bus, receiptParameters);
        if (isPersistBeansAsDataObject())
        {
            persistBean(bean);
        }
        return bean;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManager#
     * getGiftReceiptParameterBeanInstance
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc)
     */
    public GiftReceiptParameterBeanIfc getGiftReceiptParameterBeanInstance(SessionBusIfc bus,
            ReceiptParameterBeanIfc receiptParameters)
    {
        GiftReceiptParameterBeanIfc bean = super.getGiftReceiptParameterBeanInstance(bus, receiptParameters);
        if (isPersistBeansAsDataObject())
        {
            persistBean(bean);
        }
        return bean;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManager#
     * getGiftReceiptParameterBeanInstance
     * (oracle.retail.stores.foundation.tour.service.SessionBusIfc,
     * oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc,
     * oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc[])
     */
    public GiftReceiptParameterBeanIfc getGiftReceiptParameterBeanInstance(SessionBusIfc bus,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc[] lineItems)
    {
        GiftReceiptParameterBeanIfc bean = super.getGiftReceiptParameterBeanInstance(bus, transaction, lineItems);
        if (isPersistBeansAsDataObject())
        {
            persistBean(bean);
        }
        return bean;
    }

    /**
     * Determine the last modified of either the zip of bpt that the
     * <code>bptFileName</code> refers to.
     * 
     * @param bptFileName the file name
     * @return the last modified timestamp of the file
     * @see #getReceiptsPath()
     */
    private long getReceiptLastModified(String bptFileName)
    {
        StringTokenizer tokens = new StringTokenizer(getReceiptsPath(), ";");
        while (tokens.hasMoreTokens())
        {
            File path = new File(tokens.nextToken());
            if (path.isFile())
            {
                ZipFile zipFile = null;
                try
                {
                    zipFile = new ZipFile(path);
                    ZipEntry zipEntry = zipFile.getEntry(bptFileName);
                    if (zipEntry != null)
                    {
                        return zipEntry.getTime();
                    }
                }
                catch (IOException e)
                {
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Skipping path " + path + " while looking for " + bptFileName + ", " + e);
                    }
                }
            }
            else if (path.isDirectory())
            {
                File bptFile = new File(path, bptFileName);
                if (bptFile.exists())
                {
                    return bptFile.lastModified();
                }
            }
        }

        return 0;
    }

    /**
     * Loop through the list of receipt paths given to find blueprints. If
     * archives are found, search the contents. Else if a directory is found,
     * try to find the file as a child of that directory.
     * 
     * @param bptFileName the name of the blueprint file to look for
     * @return the blueprint file
     * @throws FileNotFoundException
     * @see #getReceiptsPath()
     */
    protected InputStream getReceiptInputStream(String bptFileName) throws FileNotFoundException
    {
        StringTokenizer tokens = new StringTokenizer(getReceiptsPath(), ";");
        while (tokens.hasMoreTokens())
        {
            String filePath = tokens.nextToken();
            if (filePath.startsWith("/"))
            {
                InputStream is = getClass().getResourceAsStream(filePath + bptFileName);
                if (is != null)
                {
                    return is;
                }
            }
            File path = new File(filePath);
            if (path.isFile())
            {
                ZipFile zipFile = null;
                try
                {
                    zipFile = new ZipFile(path);
                    ZipEntry zipEntry = zipFile.getEntry(bptFileName);
                    if (zipEntry != null)
                    {
                        return zipFile.getInputStream(zipEntry);
                    }
                }
                catch (IOException e)
                {
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Unable to get stream at " + path + " while looking for " + bptFileName + ", " + e);
                    }
                }
            }
            else if (path.isDirectory())
            {
                File bptFile = new File(path, bptFileName);
                if (bptFile.exists())
                {
                    return new FileInputStream(bptFile);
                }
            }
        }

        return null;
    }

    /**
     * Internal method to get a receipt file name. Expects that
     * <code>locale</code> is prepended with a "_" (or empty string).
     * <p>
     * Currently in 13.1, if the documentType starts with "VAT", this prefix is
     * stripped if it does not map to anything.
     * 
     * @param documentType
     * @param locale
     * @return
     */
    private String getFileName(String documentType, String locale)
    {
        String mappedDocument = null;
        ReceiptConfiguration config = getReceiptConfiguration(documentType);
        if (config == null)
        {
            mappedDocument = documentType + ".bpt";
        }
        else
        {
            mappedDocument = config.getBlueprintFileName();
        }
        if (mappedDocument == null)
        {
            if (documentType.startsWith("VAT"))
            {
                documentType = documentType.substring(3);
                config = getReceiptConfiguration(documentType);
                if (config == null)
                {
                    mappedDocument = documentType + ".bpt";
                }
                else
                {
                    mappedDocument = config.getBlueprintFileName();
                }
            }

            if (mappedDocument == null)
            {
                throw new NullPointerException("Blueprint is not configured for documentType " + documentType);
            }
        }
        StringBuilder fileName = new StringBuilder(mappedDocument);
        int offset = fileName.indexOf(".bpt");
        fileName.insert(offset, locale);
        return fileName.toString();
    }
    
    /**
     * Internal method to get a receipt file name. Expects that
     * <code>locale</code> is prepended with a "_" (or empty string).
     * <p>
     * Currently in 13.1, if the documentType starts with "VAT", this prefix is
     * stripped if it does not map to anything.
     * 
     * @param documentType
     * @param locale
     * @return
     */
    private String getFiscalBlueprintFileName(String documentType, String locale)
    {
        String mappedDocument = null;
        StringBuilder fileName = new StringBuilder("");
        ReceiptConfiguration config = getReceiptConfiguration(documentType);

        if (config != null)
        {
            mappedDocument = config.getFiscalBlueprintFileName();
            if (mappedDocument != null && mappedDocument.length() > 0)
            {
                fileName = new StringBuilder(mappedDocument);
                int offset = fileName.indexOf(".bpt");
                fileName.insert(offset, locale);

            }
        }
        return fileName.toString();

    }

    /**
     * Get print preview
     * 
     * @param bus
     * @param receiptDataBean
     * @return
     * @throws PrintableDocumentException
     */
    public String getPreview(SessionBusIfc bus, PrintableDocumentParameterBeanIfc receiptDataBean)
            throws PrintableDocumentException
    {
        boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);
        
    	// get the blueprint for this receipt from a repository
        Blueprint blueprint = getBlueprint(receiptDataBean.getDocumentType(), receiptDataBean.getLocale(),
                receiptDataBean.getDefaultLocale());
        // alter any setting on this blueprint based upon parameters
        blueprint = prepareBlueprint(blueprint, bus, receiptDataBean);
        
        BlueprintedReceipt receipt = null;

        // get a BlueprintedReceipt for this blueprint
        if(isFiscalPrintingEnabled)
        {
            // get a BlueprintedReceipt for this blueprint
            receipt = (BlueprintedFiscalReceipt) getBlueprintedFiscalReceipt(blueprint);
        }
        else
        {
             receipt = getBlueprintedReceipt(blueprint);
        }
        // set data object as bean onto receipt
        receipt.setParameterBean(receiptDataBean);
        // Set the isPreview Flag
        receipt.setPreview(true);

        POSDeviceActions pda = new POSDeviceActions(bus);
        try
        {
            pda.printDocument(receipt);
            // getPreviewText() should only come after the print is done
            previewText = receipt.getPreviewText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to Preview receipt. " + e.getMessage());

            if (e.getCause() != null)
            {
                logger.warn("Exception caused by: " + Util.throwableToString(e.getCause()));
            }

            throw new PrintableDocumentException("Exception caught while attempting to Preview document type: "
                    + receiptDataBean.getDocumentType(), e);
        }
        // reset the isPreview Flag
        receipt.setPreview(false);

        String preview = previewText.toString();

        return preview;
    }

    /**
     * Return the cached receipt config.
     * 
     * @return
     */
    private ReceiptConfiguration getReceiptConfiguration(String documentType)
    {
        ReceiptConfiguration config = receiptConfigs.get(documentType);
        if (config == null)
        {
            logger.warn("No receipt configuration for document type \"" + config + "\".");
        }
        return config;
    }

    /**
     * Returns repeat header flag
     * 
     * @return the repeatHeader
     */
    public boolean isRepeatHeader()
    {
        return repeatHeader;
    }

    /**
     * Sets repeat header flag
     * 
     * @param repeatHeader the repeatHeader to set
     */
    public void setRepeatHeader(boolean repeatHeader)
    {
        this.repeatHeader = repeatHeader;
    }

    /**
     * Returns repeat footer flag
     * 
     * @return the repeatFooter
     */
    public boolean isRepeatFooter()
    {
        return repeatFooter;
    }

    /**
     * Sets repeat footer flag
     * 
     * @param repeatFooter the repeatFooter to set
     */
    public void setRepeatFooter(boolean repeatFooter)
    {
        this.repeatFooter = repeatFooter;
    }

    /**
     * Returns comma separated names of blueprints use to print the receipt
     * header
     * 
     * @return the headerBlueprints
     */
    public String getHeaderBlueprints()
    {
        return headerBlueprints;
    }

    /**
     * Set the list of blueprints used for printing receipt header
     * 
     * @param headerBlueprints the headerBlueprints to set
     */
    public void setHeaderBlueprints(String headerBlueprints)
    {
        this.headerBlueprints = headerBlueprints;
    }

    /**
     * Returns comma separated names of blueprints use to print the receipt
     * footer
     * 
     * @return the footerBlueprints
     */
    public String getFooterBlueprints()
    {
        return footerBlueprints;
    }

    /**
     * Set the list of blueprints used for printing receipt header
     * 
     * @param footerBlueprints the footerBlueprints to set
     */
    public void setFooterBlueprints(String footerBlueprints)
    {
        this.footerBlueprints = footerBlueprints;
    }

    // -------------------------------------------------------------------------
    /**
     * Inner class to keep track of setting given to the various receipt
     * blueprints.
     */
    protected class ReceiptConfiguration
    {
        private String fiscalBlueprintFileName;

        private String bptFileName;

        private String ippFOPTemplate;

        private String ippXMLTemplate;

        private String eReceiptFOPTemplate;

        private String eReceiptXMLTemplate;

        /**
         * Constructor
         * 
         * @param bptFileName the file name of the BPT to set
         */
        public ReceiptConfiguration(String bptFileName)
        {
            assert (bptFileName != null);
            this.bptFileName = bptFileName;
        }

        /**
         * @return the fiscalBlueprintFileName
         */
        public String getFiscalBlueprintFileName()
        {
            return fiscalBlueprintFileName;
        }

        /**
         * @param fiscalBlueprintFileName the fiscalBlueprintFileName to set
         */
        public void setFiscalBlueprintFileName(String fiscalBlueprintFileName)
        {
            this.fiscalBlueprintFileName = fiscalBlueprintFileName;
        }

        /**
         * @return the bptFileName
         */
        public String getBlueprintFileName()
        {
            return bptFileName;
        }

        /**
         * @return the ippFOPTemplate
         */
        public String getIppFOPTemplate()
        {
            return ippFOPTemplate;
        }

        /**
         * @param ippFOPTemplate the ippFOPTemplate to set
         */
        public void setIppFOPTemplate(String ippFOPTemplate)
        {
            this.ippFOPTemplate = ippFOPTemplate;
        }

        /**
         * @return the ippXMLTemplate
         */
        public String getIppXMLTemplate()
        {
            return ippXMLTemplate;
        }

        /**
         * @param ippXMLTemplate the ippXMLTemplate to set
         */
        public void setIppXMLTemplate(String ippXMLTemplate)
        {
            this.ippXMLTemplate = ippXMLTemplate;
        }

        /**
         * @return the eReceiptFOPTemplate
         */
        public String getEReceiptFOPTemplate()
        {
            return eReceiptFOPTemplate;
        }

        /**
         * @param eReceiptFOPTemplate the eReceiptFOPTemplate to set
         */
        public void setEReceiptFOPTemplate(String eReceiptFOPTemplate)
        {
            this.eReceiptFOPTemplate = eReceiptFOPTemplate;
        }

        /**
         * @return the eReceiptXMLTemplate
         */
        public String getEReceiptXMLTemplate()
        {
            return eReceiptXMLTemplate;
        }

        /**
         * @param eReceiptXMLTemplate the eReceiptXMLTemplate to set
         */
        public void setEReceiptXMLTemplate(String eReceiptXMLTemplate)
        {
            this.eReceiptXMLTemplate = eReceiptXMLTemplate;
        }
    }
}
