/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/PrintableDocumentParameterBean.java /main/11 2012/08/16 15:30:13 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    02/12/09 - replaced comments
 *    acadar    02/12/09 - use default locale for date/time printing in the
 *                         receipts
 *    glwang    02/09/09 - changes per code review
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   10/21/08 - added for default impl of param bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * A simple printable document parameter bean implementation. Extend this class
 * to add desired attributes for printing.
 * <p>
 * NOTE: Do not extend this class and use any {@link EYSPrintableDocument}
 * behavior. This class will be re-factored in two releases to not extend that
 * super-class. It is only here for back-compatibility for certain receipts.
 *
 * @author cgreene
 * @since 13.1
 */
public abstract class PrintableDocumentParameterBean extends EYSPrintableDocument
    implements PrintableDocumentParameterBeanIfc
{
    private static final long serialVersionUID = -5052946277576199170L;

    /**
     * Type of report being printed, e.g. "SummaryReport"
     */
    protected String documentType;

    /**
     * The locale used to print the receipt. Defaults to
     * {@link LocaleConstantsIfc#RECEIPT}
     */
    protected Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);

    /**
     * The locale used to print the date and time on the receipt. Defaults to
     * {@link LocaleMap#DEFAULT}
     */
    protected Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);

    /**
     * The flag to show whether the workstation is in training mode
     */
    protected boolean trainingMode=false;
    
    protected boolean fiscalReceipt = false;

    /**
     * @return the fiscalReceipt
     */
    public boolean isFiscalReceipt()
    {
        return fiscalReceipt;
    }

    /**
     * @param fiscalReceipt the fiscalReceipt to set
     */
    public void setFiscalReceipt(boolean fiscalReceipt)
    {
        this.fiscalReceipt = fiscalReceipt;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#getDocumentType()
     */
    public String getDocumentType()
    {
        return documentType;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#getDefaultLocale()
     */
    public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#setDocumentType(java.lang.String)
     */
    public void setDocumentType(String type)
    {
        this.documentType = type;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#setDefaultLocale(java.util.Locale)
     */
    public void setDefaultLocale(Locale locale)
    {
        this.defaultLocale = locale;
    }

    /**
     * Does nothing. This method will got away in two releases.
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#printDocument()
     */
    public void printDocument() throws DeviceException
    {
    }

    /**
     * Does nothing. This method will got away in two releases.
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#setParameterBean(oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc)
     */
    public void setParameterBean(PrintableDocumentParameterBeanIfc parameterBean)
    {
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#setTrainingMode(boolean)
     */
    public void setTrainingMode(boolean flag)
    {
    	this.trainingMode = flag;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc#isTrainingMode()
     */
    public boolean isTrainingMode()
    {
    	return this.trainingMode;
    }
}
