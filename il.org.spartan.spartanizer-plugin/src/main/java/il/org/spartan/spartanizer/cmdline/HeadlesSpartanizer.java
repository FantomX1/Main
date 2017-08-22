package il.org.spartan.spartanizer.cmdline;

import static il.org.spartan.tide.*;

import java.io.*;
import java.nio.file.*;
import org.eclipse.jdt.core.dom.*;
import fluent.ly.*;
import il.org.spartan.external.*;
import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.cmdline.SpartanizationComparator.*;
import il.org.spartan.spartanizer.cmdline.tables.*;
import il.org.spartan.spartanizer.java.*;
import il.org.spartan.spartanizer.plugin.*;
import il.org.spartan.spartanizer.traversal.*;
import il.org.spartan.tables.*;
import il.org.spartan.utils.*;

/** This class you can spartanize a directory easily. Or you can extends this
 * class and configure it to fit to your own needs.
 * @author oran1248
 * @author Matteo Orru'
 * @since 2017-04-11 */
public class HeadlesSpartanizer extends GrandVisitor {
  
  @External(alias = "cp", value = "copy file") @SuppressWarnings("CanBeFinal") boolean copy;
  @External(alias = "u", value = "unique") @SuppressWarnings("CanBeFinal") boolean unique;
  @External(alias = "s", value = "statistics") @SuppressWarnings("CanBeFinal") boolean stats;

  static Table summaryTable;
  static Table tippersTable;
  static HeadlesSpartanizer hs;
  JavaProductionFilesVisitor v;
  TextualTraversals traversals = new TextualTraversals();
  CurrentData data = new CurrentData();
  static PrintWriter beforeWriter, afterWriter;
  
  public static void main(final String[] args){
    hs = new HeadlesSpartanizer(args);
    hs.goAll();
  }

  private void goAll() {
    CurrentData.locations.stream().forEach(λ -> {
      CurrentData.location = λ; 
      go(λ);
    });
    notify.endBatch();
  }
  
  public final void go(final String dirPath) {
    setUp();
    (new GrandVisitor(new String[] {dirPath}) {
      
      {
        listen(new Tapper() {
          @Override public void beginBatch(){
            System.err.println(" --- Begin Batch Process --- ");
            initializeWriter();
          }
          @Override public void beginFile() {
            System.err.println("Begin " + CurrentData.fileName);
            traversals.traversal.notify.begin();
            tippersTable.col("Project",CurrentData.location);
            tippersTable.col("File",CurrentData.fileName);
            tippersTable.nl();
          }
          @Override public void beginLocation() {
            // System.err.println("Begin " + CurrentData.location);
            try {
              initializeBeforeAfter();
            } catch (FileNotFoundException x) {
              x.printStackTrace();
            }
          }
          @Override public void endBatch() {
            System.err.println(" --- End Batch Process --- ");
            done();
          }
          @Override public void endFile() {
            //System.err.println("End " + CurrentData.fileName);
            summarize(CurrentData.location,CurrentData.before,CurrentData.after);
            traversals.traversal.notify.end();
          }
          @Override public void endLocation() {
            //System.err.println("End " + CurrentData.location);
            finalizeBeforeAfter();
          }
        });
      }
      protected void done() {
        finalizeWriters();
      }     
      private void finalizeWriters() {
        Traversal.table.close();
        tippersTable.close();
        summaryTable.close();
      }
      void summarize(String project, String before, String after) {
        summarize(project,asCu(before),asCu(after)); 
      }
      public void summarize(final String project, final ASTNode before, final ASTNode after) {
        summaryTable//
            .col("Project", project)//
            .col("File", CurrentData.fileName)//
            .col("Path", CurrentData.relativePath);
        reportCUMetrics(before, "before");
        reportCUMetrics(after, "after");
        summaryTable.nl();
      }
      @SuppressWarnings({ "rawtypes", "unchecked" })
      void reportCUMetrics(final ASTNode ¢, final String id) {
        for (final NamedFunction f : functions())
          summaryTable.col(f.name() + "-" + id, f.function().run(¢));
      }
      void initializeWriter() {
        if(tippersTable == null)
          tippersTable = new Table("tippers" + "-" + corpus, outputFolder);
        System.err.println(Traversal.table == null);
        if(Traversal.table == null)
          Traversal.table = new Table("tippers2" + "-" + corpus, outputFolder);
        if (summaryTable == null)
          summaryTable = new Table(Table.classToNormalizedFileName(Table_Summary.class) + "-" 
                        + corpus, outputFolder);
      }
      @Override public void visitFile(final File f) {
        CurrentData.fileName = f.getName();
        traversals.traversal.fileName = f.getName();
        traversals.traversal.project = CurrentData.location;
        notify.beginFile();
        try {
          CurrentData.relativePath = Paths.get(f.getCanonicalPath()).subpath(Paths.get(inputFolder).getNameCount(), Paths.get(f.getCanonicalPath()).getNameCount()) + "";
          CurrentData.absolutePath = Paths.get(f.getCanonicalPath()).subpath(Paths.get(inputFolder).getNameCount(), Paths.get(f.getCanonicalPath()).getNameCount()) + "";
        } catch (IOException ¢) {
          ¢.printStackTrace();
        }
        if (!spartanize(f))
          return;
        try {
          CurrentData.before = FileUtils.read(f);
          CurrentData.after = perform(CurrentData.before);
          analyze(CurrentData.before, CurrentData.after);
        } catch (final IOException ¢) {
          note.io(¢);
        }
        notify.endFile();
      }
      protected void analyze(final String before, final String after) {
        try {
          if(copy && !unique){
            Path pathname = Paths.get(outputFolder + File.separator + Paths.get(CurrentData.relativePath).getParent());
            if (!Files.exists(pathname)) 
              new File(pathname + "").mkdirs();
            FileUtils.writeToFile(outputFolder + File.separator + CurrentData.relativePath , after);
          } else if (copy && unique) {
            try {
              initializeBeforeAfter();
            } catch (FileNotFoundException x) {
              x.printStackTrace();
            }
            writeBeforeAfter(before, after);
          }
         } catch (final FileNotFoundException ¢) {
          note.io(¢);
          ¢.printStackTrace();
        }
      }
      void finalizeBeforeAfter() {
        beforeWriter.close();
        beforeWriter = null;
        afterWriter.close();
        afterWriter = null;
      }
      private void writeBeforeAfter(final String before, final String after) throws FileNotFoundException {
        writeFile(before, "before", beforeWriter);
        writeFile(after, "after", afterWriter);
      }
      void initializeBeforeAfter() throws FileNotFoundException {
        if (beforeWriter == null) 
          beforeWriter = new PrintWriter(outputFolder +  File.separator + CurrentData.location + "-" + "before" + ".java");
        if (afterWriter == null) 
          afterWriter = new PrintWriter(outputFolder +  File.separator + CurrentData.location + "-" + "after" + ".java");
      }
      @SuppressWarnings("unused")
      private void writeFile(final String before, final String name, PrintWriter writer) throws FileNotFoundException {
        Path path = Paths.get(outputFolder);
        if (Files.notExists(path)) 
          new File(path + File.separator + name + ".java").mkdirs();
        //initializeBeforeWriter(name, writer, path);
        writer.append(before);
        writer.flush();
      }
      
      @SuppressWarnings("unused")
      private void initializeWriter(final String name, PrintWriter writer, Path path) throws FileNotFoundException {
        if (beforeWriter == null) 
          beforeWriter = new PrintWriter(path +  File.separator + name + ".java");
      }
     
      CompilationUnit asCu(final String before) {
        return (CompilationUnit) makeAST.COMPILATION_UNIT.from(before);
      }
    }).visitAll(astVisitor());
    tearDown();
  }
  

  static NamedFunction<ASTNode> m(final String name, final ToInt<ASTNode> f) {
    return new NamedFunction<>(name, f);
  }
  
  public static NamedFunction<?>[] functions() {
    return as.array(//
        m("length - ", metrics::length), //
        m("essence - ", λ -> Essence.of(λ + "").length()), //
        m("tokens - ", λ -> metrics.tokens(λ + "")), //
        m("nodes - ", countOf::nodes), //
        m("body - ", metrics::bodySize), //
        m("methodDeclaration - ", λ -> !iz.methodDeclaration(λ) ? -1 : extract.statements(az.methodDeclaration(λ).getBody()).size()),
        m("tide - ", λ -> clean(λ + "").length()));//
  }
  
  public final String fixedPoint(final String from) {
    return traversals.fixed(from);
  }
  
  public HeadlesSpartanizer(final String[] args){
    super(args);
  }
  protected void setUp() {
    initializeWriters();
    notify.beginBatch();
  }
  private void initializeWriters() {
    
  }

  protected void tearDown() {
    notify.endBatch();
  }
  @SuppressWarnings("static-method") 
  protected boolean spartanize(@SuppressWarnings("unused") final File __) {
    return true;
  }
  @SuppressWarnings("static-method") protected ASTVisitor astVisitor() {
    return new ASTVisitor() {/**/};
  }
  protected String perform(final String fileContent) {
    return fixedPoint(fileContent);
  }
 
}
