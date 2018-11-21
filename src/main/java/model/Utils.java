package model;

import model.dao.ServiceDAO;
import org.apache.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Utils {
  private static final Logger log = Logger.getLogger(ServiceDAO.class);

  /**
   * Parse the JSON Array or null value to String as "s1,s2,s3" or ""
   *
   * @param jsonObject
   * @return
   */
  public static String parseServiceComposition(JsonObject jsonObject) {
    String serviceCompositionString = "";
    JsonArray serviceCompositionJsonArray;
    if (jsonObject.isNull("ServiceComposition")) {
      log.info("SERVICE COMPOSITION IS NULL");
    } else {
      serviceCompositionJsonArray = jsonObject.getJsonArray("ServiceComposition");
      for (int i = 0; i < serviceCompositionJsonArray.size(); i++) {
        String singleService = serviceCompositionJsonArray.get(i).toString();
        log.info(serviceCompositionJsonArray.get(i).toString());
        if (i == 0) {
          serviceCompositionString = serviceCompositionString + singleService;
        } else {
          serviceCompositionString = serviceCompositionString + "," + singleService;
        }
      }
    }
    return serviceCompositionString;
  }
}
