package fabric;

import model.pojo.Activity;
import org.apache.log4j.Logger;
import start.HFJson2Pojo;
import start.StartClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;

public class Utils {

  private static final Logger log = Logger.getLogger(Utils.class);

  /**
   * Serialize AppUser object to file in the fabricUserPath defined in the file
   * {@link StartClass}.HF_CONFIG_FILE
   *
   * @param fabricUser The object to be serialized
   * @throws IOException
   */
  public static void serialize(UserImplementation fabricUser) throws IOException {
    Path path;
    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String certFolderPath = hfJson2Pojo.getFabricUserPath(); // "bc-mas-app/hfc-key-store/"
    log.info("certFolderPath: " + certFolderPath);
    // TODO: Da sistemare SMELL CODE REFACTORING ASAP
    // get the workingDir absolute Path
    String workingDir = System.getProperty("user.dir");

    String[] pathParts = workingDir.split("/");
    log.info("pathParts.length: " + pathParts.length);

    // in SOURCE: /home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas OK(vogliamo andare qua, trovare il modo elegante di farlo)
    // in JAR: /home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas/target FIX
    log.info("workingDir serialize: " + workingDir);
    ObjectOutputStream objectOutputStream;
    // TODO: Levare hardcoding (fargli fare cd .. se si trova su target e sul readme scrivi di far girare il .jar dal folder target e non cambiare)
    //    if (workingDir.equals("/home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas")) {
    if (pathParts[pathParts.length - 1].equals("target")) {
      log.debug("TEMPORARY FIX DIRECTORY PATH");
      String[] newPathParts = new String[pathParts.length - 1];
      for (int i = 0; i < pathParts.length - 1; i++) {
        newPathParts[i] = pathParts[i];
      }
      String fixedPath = String.join("/", newPathParts);
      log.info("fixedPath: " + fixedPath);

      //      File jarDir = new File(
      //          "/home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas/" + certFolderPath
      //              + fabricUser.getName());

      File jarDir = new File(fixedPath + "/" + certFolderPath + fabricUser.getName());

      log.info("FIXED PATH IN JAR jarDir.getAbsolutePath(): " + jarDir.getAbsolutePath());
      FileOutputStream fileOutputStream = new FileOutputStream(jarDir);
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(fabricUser);
    } else if (pathParts[pathParts.length - 1].equals("tourism_trust_bct4mas")) {
      log.debug("OK");
      path = Paths.get(certFolderPath + fabricUser.getName());
      objectOutputStream = new ObjectOutputStream(Files.newOutputStream(path));
      objectOutputStream.writeObject(fabricUser);
    }

    // OLD CODE
    //    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
    //      oos.writeObject(fabricUser);
    //    }
  }

  //  OLD serialize (before .jar bestemmie)
  //  public static void serialize(UserImplementation fabricUser) throws IOException {
  //    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
  //    String certFolderPath = hfJson2Pojo.getFabricUserPath(); // "bc-mas-app/hfc-key-store/"
  //
  //    try (ObjectOutputStream oos = new ObjectOutputStream(
  //        Files.newOutputStream(Paths.get(certFolderPath + fabricUser.getName())))) {
  //      oos.writeObject(fabricUser);
  //    }
  //  }

  /**
   * Deserialize AppUser object from file in the fabricUserPath defined in the file
   * {@link StartClass}.HF_CONFIG_FILE
   *
   * @param name The name of the user. Used to build file name ${name}
   * @return
   * @throws Exception
   */
  static UserImplementation tryDeserialize(String name) throws Exception {
    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String certFolderPath = hfJson2Pojo.getFabricUserPath(); // "bc-mas-app/hfc-key-store/"
    log.info("certFolderPath try deserialize: " + certFolderPath);
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    // TODO: Da sistemare SMELL CODE REFACTORING ASAP
    String workingDir = System.getProperty("user.dir");
    // in SOURCE: /home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas OK(vogliamo andare qua, trovare il modo elegante di farlo)
    // in JAR: /home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas/target FIX
    log.info("workingDir serialize: " + workingDir);

    String[] pathParts = workingDir.split("/");
    log.info("pathParts.length: " + pathParts.length);

    ObjectOutputStream objectOutputStream;
    // TODO: Levare hardcoding (fargli fare cd .. se si trova su target e sul readme scrivi di far girare il .jar dal folder target e non cambiare)
    if (pathParts[pathParts.length - 1].equals("target")) {
      log.debug("TEMPORARY FIX DIRECTORY PATH");
      String[] newPathParts = new String[pathParts.length - 1];
      for (int i = 0; i < pathParts.length - 1; i++) {
        newPathParts[i] = pathParts[i];
      }
      String fixedPath = String.join("/", newPathParts);
      log.info("fixedPath: " + fixedPath);

      //      File jarDir = new File(
      //          "/home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas/" + certFolderPath
      //              + fabricUser.getName());

      File jarDir = new File(fixedPath + "/" + certFolderPath + name);
      //      File jarDir = new File(
      //          "/home/anakin/Documents/Valerio/github.com/tourism_trust_bct4mas/" + certFolderPath
      //              + name);
      log.info("file exist: " + Files.exists(Paths.get(jarDir.getAbsolutePath())));
      if (Files.exists(Paths.get(jarDir.getAbsolutePath()))) {
        log.info("I'M JAR");
        return Utils.deserialize(jarDir.getAbsolutePath());
      }
    } else if (pathParts[pathParts.length - 1].equals("tourism_trust_bct4mas")) {
      String pathName = certFolderPath + name;
      log.info("pathName: " + pathName);
      if (Files.exists(Paths.get(pathName))) {
        log.info("I'M HERE");
        return Utils.deserialize(pathName);
      }

      log.info("classLoader: " + classLoader.toString());

      //    if (Files.exists(Paths.get(pathName))) {
      //      return Utils.deserialize(pathName);
      //    }
    }
    return null;
  }


  // OLD tryDeserialize (before .jar bestemmie)
  //  static UserImplementation tryDeserialize(String name) throws Exception {
  //    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
  //    String certFolderPath = hfJson2Pojo.getFabricUserPath(); // "bc-mas-app/hfc-key-store/"
  //    String pathName = certFolderPath + name;
  //    if (Files.exists(Paths.get(pathName))) {
  //      return Utils.deserialize(pathName);
  //    }
  //    return null;
  //  }


  /**
   * Get the {@link UserImplementation} deserializing the certificate from the pathName given
   * 
   * @param pathName
   * @return
   * @throws Exception
   */
  private static UserImplementation deserialize(String pathName) throws Exception {
    try (ObjectInputStream decoder =
        new ObjectInputStream(Files.newInputStream(Paths.get(pathName)))) {
      return (UserImplementation) decoder.readObject();
    }
  }

    public static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

      log.info(format(format, args));
        System.err.flush();
        System.out.flush();

    }

    public static String format(String format, Object... args) {
        return (new Formatter()).format(format, args).toString();
    }

    /**
     * Print the activities List
     *
     * @param activitiesList
     * @return
     */
    public static void printActivitiesList(ArrayList<Activity> activitiesList) {
        int i = 0;
        for (Activity singleActivity : activitiesList) {
          log.info("Activity NUMERO " + i + ": ");
          log.info("EvaluationId: " + singleActivity.getEvaluationId().toString());
          log.info("WriterAgentId: " + singleActivity.getWriterAgentId().toString());
            System.out
                .println("DemanderAgentId: " + singleActivity.getDemanderAgentId().toString());
            System.out
                .println("ExecuterAgentId: " + singleActivity.getExecuterAgentId().toString());
            System.out
                .println("ExecutedServiceId: " + singleActivity.getExecutedServiceId().toString());
          log.info(
                "ExecutedServiceTxId: " + singleActivity.getExecutedServiceTxId().toString());
          log.info(
                "ExecutedServiceTimestamp: " + singleActivity.getExecutedServiceTimestamp()
                    .toString());
          log.info("Value: " + singleActivity.getValue().toString());
            i++;
        }
    }
}
