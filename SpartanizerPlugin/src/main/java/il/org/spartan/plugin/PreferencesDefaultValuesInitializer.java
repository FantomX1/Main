package il.org.spartan.plugin;

import static il.org.spartan.plugin.PreferencesResources.*;
import static il.org.spartan.plugin.PreferencesResources.TipperGroup.*;

import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.preference.*;

import il.org.spartan.plugin.PreferencesResources.*;

/** This class is called by Eclipse when the plugin is first loaded and has no
 * default preference values. These are set by the values specified here.
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2016/03/28 */
public final class PreferencesDefaultValuesInitializer extends AbstractPreferenceInitializer {
  @Override public void initializeDefaultPreferences() {
    final IPreferenceStore s = store();
    // s.setDefault(PLUGIN_STARTUP_BEHAVIOR_ID, "remember");
    s.setDefault(NEW_PROJECTS_ENABLE_BY_DEFAULT_ID, true);
    for (final TipperGroup ¢ : TipperGroup.values())
      s.setDefault(¢.id, true);
  }
}
