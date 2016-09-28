package il.org.spartan.plugin;

import static il.org.spartan.plugin.PreferencesResources.*;
import static il.org.spartan.plugin.PreferencesResources.WringGroup.*;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.eclipse.ui.*;

import il.org.spartan.plugin.PreferencesResources.*;
import il.org.spartan.spartanizer.dispatch.*;

/** ??
 * @author Daniel Mittelman
 * @year 2016 */
public final class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  /** An event handler used to re-initialize the {@link Trimmer} spartanization
   * once a tipper preference was modified. */
  static class SpartanPropertyListener implements IPropertyChangeListener {
    @Override public void propertyChange(@SuppressWarnings("unused") final PropertyChangeEvent __) {
      Toolbox.refresh();
      try {
        RefreshAll.go();
      } catch (final Exception e) {
        LoggingManner.logEvaluationError(this, e);
      }
    }
  }

  public static final String WRING_COMBO_OPTIONS[][] = { { "Enabled", "on" }, { "Disabled", "off" } };
  private final SpartanPropertyListener listener;

  public PreferencesPage() {
    super(GRID);
    listener = new SpartanPropertyListener();
  }

  /** Build the preferences page by adding controls */
  @Override public void createFieldEditors() {
    addField(new ComboFieldEditor(PLUGIN_STARTUP_BEHAVIOR_ID, PLUGIN_STARTUP_BEHAVIOR_TEXT, PLUGIN_STARTUP_BEHAVIOR_OPTIONS, getFieldEditorParent()));
    addField(new BooleanFieldEditor(NEW_PROJECTS_ENABLE_BY_DEFAULT_ID, NEW_PROJECTS_ENABLE_BY_DEFAULT_TEXT, getFieldEditorParent()));
    final GroupFieldEditor g = new GroupFieldEditor("Enabled spartanizations", getFieldEditorParent());
    for (final WringGroup ¢ : WringGroup.values())
      g.add(new ComboFieldEditor(¢.id, ¢.label, WRING_COMBO_OPTIONS, g.getFieldEditor()));
    addField(g);
    g.init();
  }

  @Override public void init(@SuppressWarnings("unused") final IWorkbench __) {
    setPreferenceStore(WringGroup.store());
    setDescription(PAGE_DESCRIPTION);
    store().addPropertyChangeListener(listener);
  }
}
