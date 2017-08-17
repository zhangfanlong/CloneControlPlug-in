package Myplugin.CloneControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	public static  Map<String,List<String>> currentVersionJavaFiles;
	

	// The plug-in ID
	public static final String PLUGIN_ID = "Myplugin.CloneControl"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		currentVersionJavaFiles = new HashMap<String,List<String>>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static ITextEditor getActiveEditor() {
		return (ITextEditor)getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	public static IEditorPart[] getInstanciatedEditors() {
		List result = new ArrayList(0);
		IWorkbench workbench = getDefault().getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		for (int windowIndex = 0; windowIndex < windows.length; windowIndex++) {
			IWorkbenchPage[] pages = windows[windowIndex].getPages();
			for (int pageIndex = 0; pageIndex < pages.length; pageIndex++) {
				IEditorReference[] references = pages[pageIndex].getEditorReferences();
				for (int refIndex = 0; refIndex < references.length; refIndex++) {
					IEditorPart editor = references[refIndex].getEditor(false);
					if (editor != null)
						result.add(editor);
				}
			}
		}
		return (IEditorPart[]) result.toArray(new IEditorPart[result.size()]);
	}

}
