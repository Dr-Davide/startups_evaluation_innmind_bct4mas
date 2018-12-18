package model;

import model.dao.FeatureDAO;
import org.apache.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Utils {
  private static final Logger log = Logger.getLogger(FeatureDAO.class);

  /**
   * Parse the JSON Array or null value to String as "s1,s2,s3" or ""
   *
   * @param jsonObject
   * @return
   */
  public static String parseFeatureComposition(JsonObject jsonObject) {
    String serviceCompositionString = "";
    JsonArray serviceCompositionJsonArray;
    if (jsonObject.isNull("FeatureComposition")) {
      log.info("SERVICE COMPOSITION IS NULL");
    } else {
      serviceCompositionJsonArray = jsonObject.getJsonArray("FeatureComposition");
      for (int i = 0; i < serviceCompositionJsonArray.size(); i++) {
        String singleFeature = serviceCompositionJsonArray.get(i).toString();
        log.info(serviceCompositionJsonArray.get(i).toString());
        if (i == 0) {
          serviceCompositionString = serviceCompositionString + singleFeature;
        } else {
          serviceCompositionString = serviceCompositionString + "," + singleFeature;
        }
      }
    }
    return serviceCompositionString;
  }
}
