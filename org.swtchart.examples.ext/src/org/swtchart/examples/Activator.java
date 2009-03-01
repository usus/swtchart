package org.swtchart.examples;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    /** The shared instance */
    private static Activator plugin;

    /*
     * @see AbstractUIPlugin#start(BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        setPlugin(this);
    }

    /*
     * @see AbstractUIPlugin#stop(BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        setPlugin(null);
        super.stop(context);
    }

    /**
     * Sets the shared instance
     * 
     * @param activator
     *            the shared instance
     */
    private static void setPlugin(Activator activator) {
        plugin = activator;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
}
