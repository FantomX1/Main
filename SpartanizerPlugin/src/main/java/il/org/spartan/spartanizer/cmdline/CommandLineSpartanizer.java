package il.org.spartan.spartanizer.cmdline;

import java.io.*;

/** A configurable version of the CommandLineSpartanizer that relies on
 * {@link CommandLineApplicator} and {@link CommandLineSelection}
 * @author Matteo Orru'
 * @since 2016 */
public class CommandLineSpartanizer extends AbstractCommandLineSpartanizer {
  private final String name;

  CommandLineSpartanizer(final String path) {
    this(path, system.folder2File(path));
  }

  CommandLineSpartanizer(final String inputPath, final String name) {
    this.inputPath = inputPath;
    this.name = name;
  }

  @Override public void apply() {
    System.out.println(inputPath);
    try {
      Reports.initializeFile(folder + name + ".before.java", "before");
      Reports.initializeFile(folder + name + ".after.java", "after");
      Reports.intializeReport(folder + name + ".CSV", "metrics");
      Reports.intializeReport(folder + name + ".spectrum.CSV", "spectrum");
      
      CollectApplicator.defaultApplicator().passes(20)
      .selection(CommandLineSelection.of(CommandLineSelection.Util.getAllCompilationUnit(inputPath))).go();
      
      CommandLineApplicator.defaultApplicator().passes(20)
          .selection(CommandLineSelection.of(CommandLineSelection.Util.getAllCompilationUnit(inputPath))).go();
      Reports.close("metrics");
      Reports.close("spectrum");
      Reports.closeFile("before");
      Reports.closeFile("after");
    } catch (final IOException x) {
      x.printStackTrace();
    }
  }
}