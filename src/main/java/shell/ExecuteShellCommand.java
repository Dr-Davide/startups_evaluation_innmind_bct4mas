package shell;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Class to run commands on shell (cli)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class ExecuteShellCommand {

  private static final Logger log = Logger.getLogger(ExecuteShellCommand.class);

  /**
   * Execute a bash command. We can handle complex bash commands including multiple executions (; |
   * && ||), quotes, expansions ($), escapes (\), e.g.: "cd /abc/def; mv ghi 'older ghi '$(whoami)"
   * 
   * @param command
   * @return true if bash got started, but your command may have failed.
   */
  public static boolean executeBashCommand(String command) {
    boolean success = false;
    log.info("Executing BASH command:\n   " + command);
    Runtime runtime = Runtime.getRuntime();
    // StringBuffer output = new StringBuffer();
    // Use bash -c so we can handle things like multi commands separated by ; and
    // things like quotes, $, |, and \. My tests show that command comes as
    // one argument to bash, so we do not need to quote it to make it one thing.
    // Also, exec may object if it does not have an executable file as the first
    // thing,
    // so having bash here makes it happy provided bash is installed and in path.
    String[] commands = {"bash", "-c", command};
    try {
      // Process process = runtime.exec(commands);
      Process process = runtime.exec(command);

      process.waitFor();
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = "";

      while ((line = bufferedReader.readLine()) != null) {
        log.info(line);
        // output.append(line + "\n");
      }

      bufferedReader.close();
      success = true;
    } catch (Exception e) {
      System.err.println("Failed to execute bash with command: " + command);
      e.printStackTrace();
    }
    // log.info(output);
    return success;
  }

}
