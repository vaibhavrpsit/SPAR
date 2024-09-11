/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FingerprintBean.java /main/1 2011/01/24 17:29:40 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   10/21/10 - fingerprint reader bean
 *    blarsen   05/28/10 - Fingerprint Bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    This class is used to display a simple fingerprint icon on the work panel.
 *    <P>
 *    No model is needed since not data is gathered.
 *    
 *    @version $Revision: /main/1 $
 */
//------------------------------------------------------------------------------
public class FingerprintBean extends ValidatingBean
{

    private static final long serialVersionUID = 7790923844652177038L;
    
    protected JLabel fingerprintLabel = null;
    
    //--------------------------------------------------------------------------
    /**
     *    Default Constructor.
     */
    public FingerprintBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *    Return the POSBaseBeanModel.
     *    @return posBaseBeanModel as POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
       return beanModel;
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the class.
     */
    protected void initialize()
    {
        setName("FingerprintBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeLabels();
        initLayout();
    }
    
    /**
     * Initialize the layout.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        UIUtilities.layoutComponent(this, fingerprintLabel, null,          0, 0, false);

    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        Image fingerprintImage = UIUtilities.getImage("images/fingerprint.gif", this);
        ImageIcon fingerprintIcon = new ImageIcon(fingerprintImage);
        fingerprintLabel = uiFactory.createLabel("", "", fingerprintIcon, UI_LABEL);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        FingerprintBean bean = new FingerprintBean();

        UIUtilities.doBeanTest(bean);
    }
}
