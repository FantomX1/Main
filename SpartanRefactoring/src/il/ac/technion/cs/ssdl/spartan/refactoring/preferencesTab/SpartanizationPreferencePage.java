package il.ac.technion.cs.ssdl.spartan.refactoring.preferencesTab;

import il.ac.technion.cs.ssdl.spartan.builder.Plugin;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 * TODO: Author?
 * TODO: Since?
 * TODO: Congratulations and good job for starting this.
 */

public class SpartanizationPreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	public SpartanizationPreferencePage() {
		super(GRID);
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
		setDescription("Select how the compiler will regard each tranformation suggestion\n\n");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new ComboFieldEditor("Comparison With Boolean",
				"Comparison With Boolean:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Forward Declaration",
				"Forward Declaration:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Inline Single Use",
				"Inline Single Use:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Rename Return Variable to $",
				"Rename Return Variable to $:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Shortest Branch First",
				"Shortest Branch First:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Shortest Operand First",
				"Shortest Operand First:", options, getFieldEditorParent()));
		addField(new ComboFieldEditor("Ternarize", "Ternarize:", options,
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(@SuppressWarnings("unused") final IWorkbench workbench) {
		super.initialize();
	}

	private static String[][] options = new String[][] { { "Error", "Error" },
		{ "Warning", "Warning" }, { "Ignore", "Ignore" } };
}