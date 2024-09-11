/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/POSBeanConfigurator.java /main/11 2012/10/22 16:40:03 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:06 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 15 2002 17:55:42   baa
 * apply foundation  updates to UISubsystem
 *  
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 * 
 *    Rev 1.0   Mar 18 2002 11:51:42   msg
 * Initial revision.
 * 
 *    Rev 1.8   Mar 11 2002 18:01:36   mpm
 * Cleaned up text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.7   Feb 25 2002 10:51:12   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.6   Feb 23 2002 15:04:08   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Feb 12 2002 18:55:18   mpm
 * Modified to use international text support in setResourceBundleFileName().
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Jan 19 2002 12:09:54   mpm
 * Fixed merge problems.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0.1.0   Jan 19 2002 10:28:44   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:33:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.util.Properties;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.DefaultBeanConfigurator;
import oracle.retail.stores.foundation.manager.gui.InternationalTextSupport;
import oracle.retail.stores.foundation.manager.gui.UIBeanIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.beans.EYSPOSBeanIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBean;

/**
 * This class is the EYS POS specific implementation of the Bean Configurator.
 * It calls all beans through the EYSPOSBeanIfc rather than through reflection.
 * 
 * @version $Revision: /main/11 $
 */
public class POSBeanConfigurator extends DefaultBeanConfigurator
{
    private static final long serialVersionUID = 119116639324786933L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(POSBeanConfigurator.class);

    /**
     * EYSPOS specific implementation of setModel. This method transports data
     * from the business logic to the bean
     * 
     * @param model UIModelIfc model
     * @param beanInstance UIBeanIfc
     */
    public void setModel(UIModelIfc model, UIBeanIfc beanInstance) throws ConfigurationException
    {
        if (beanInstance instanceof EYSPOSBeanIfc)
        {
            // If the updateStatusBean flag is is true, do the set model only
            // if the bean and instance of the status bean.
            POSBaseBeanModel pModel = (POSBaseBeanModel) model;
            if (pModel.getUpdateStatusBean())
            {
                if (beanInstance instanceof StatusBean)
                {
                    EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;
                    bean.setModel(model);
                }
            }
            else
            {
                // Otherwise call the set model.
                EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;
                bean.setModel(model);
            }
        }
    }

    /**
     * This method is called from the POSBeanSpec class. It configures all
     * beans.
     * 
     * @param beanInstance UIBeanIfc
     */
    public void configure(UIBeanIfc beanInstance)
    {
        if (beanInstance instanceof EYSPOSBeanIfc)
        {
            EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;
            bean.configure();
        }
    }

    /**
     * Resets the text on a bean when the locale changes.
     * 
     * @param beanInstance bean to reset text on
     * @throws ConfigurationException
     */
    public void resetText(UIBeanIfc beanInstance) throws ConfigurationException
    {
        if (beanInstance instanceof EYSPOSBeanIfc)
        {
            EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;

            // reset the properties bundle with the new locale
            setResourceBundleFilename(bean.getBeanSpecName(),
                    beanInstance,
                    bean.getResourceBundleFilename());
        }
    }

    /**
     * EYSPOS specific implementation of updateModel. This method allows each
     * bean to update the common copy of the model with its data.
     * 
     * @param beanInstance UIBeanIfc
     */
    public void updateModel(UIBeanIfc beanInstance) throws ConfigurationException
    {
        if (beanInstance instanceof EYSPOSBeanIfc)
        {
            EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;
            bean.updateModel();
        }
    }

    /**
     * EYSPOS uses this method to set a properties object on the DialogBean.
     * 
     * @param beanName String
     * @param beanInstance UIBeanIfc
     * @param fileName location of the DialonBean text.
     */
    public void setResourceBundleFilename(String beanName, UIBeanIfc beanInstance, String filename)
            throws ConfigurationException
    {
        if (filename != null && beanInstance instanceof EYSPOSBeanIfc)
        {
            String bundles[] = { BundleConstantsIfc.COMMON_BUNDLE_NAME, filename };
            // get the properties from the international text support object
            Properties props = InternationalTextSupport.getInternationalBeanText(beanName, bundles);

            // Set the properties object on the bean.
            EYSPOSBeanIfc bean = (EYSPOSBeanIfc) beanInstance;
            bean.setBeanSpecName(beanName);
            bean.setResourceBundleFilename(filename);
            if (props != null)
            {
                bean.setProps(props);
            }
        }
    }
}
