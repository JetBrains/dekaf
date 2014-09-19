import org.junit.runner.JUnitCore;

/**
 * @author Leonid Bushuev from JetBrains
 */
public class RunUnitTests {

  public static void main(String[] args) {
    JUnitCore.main(UnitTestsSuite.class.getName());
    /*
    Result result = JUnitCore.runClasses(UnitTestsSuite.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
    System.out.println(result.wasSuccessful());
    */
  }

}
