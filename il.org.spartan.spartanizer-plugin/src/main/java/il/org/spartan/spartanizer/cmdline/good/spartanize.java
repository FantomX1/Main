package il.org.spartan.spartanizer.cmdline.good;

import fluent.ly.*;
import il.org.spartan.external.*;

/** This is a command line program, which can be thought of as a spartan
 * compiler. For each {@code .java} file it find, it encounters, it creates a
 * corresponding {@code .javas} file, which is the spartanized version of the
 * original. The {@code .javas} will be in the same folder as the {@code .java}
 * file, and would be overwritten each time this program is done.
 * @author Matteo Orru'
 * @since 2017-06-25 */
public class spartanize {
  @External(alias = "i", value = "input folder") @SuppressWarnings("CanBeFinal") protected static String inputFolder = system.isWindows() ? "" : ".";
  @External(alias = "o", value = "output folder") @SuppressWarnings("CanBeFinal") protected static String outputFolder = "/tmp";
  protected static final String[] defaultArguments = as.array("..");
  public static void main(final String[] args) {
    if (args.length != 0)
      as.list(args).forEach(λ -> new BatchSpartanizer(λ).fire());
    else
      new BatchSpartanizer(".", "current-working-directory").fire();
  }
}
