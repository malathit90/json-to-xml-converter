package com.risksense.converters;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Factory class for creating instances of {@link XmlJsonConverterI}.
 */
@Service
public class ConverterFactory implements XmlJsonConverterI {

  private static final Logger logger = LoggerFactory.getLogger(ConverterFactory.class);

  @Override
  public String convertJsonToXml(String data) {
    Object jsonData;

    try {
      jsonData = new JSONObject(data);
    } catch (JSONException e) {
      try {
        jsonData = new JSONArray(data);
      } catch (JSONException e1) {
        return null;
      }
    }

    return convertToXml(null, jsonData);
  }

  private String convertToXml(String name, Object data) {
    String nameValue = (name == null) ? "" : " name=\"" + name + "\"";
    if (data instanceof Number) {
      return "<number" + nameValue + ">" + data + "</number>";
    } else if (data instanceof String) {
      return "<string" + nameValue + ">" + data + "</string>";
    } else if (data instanceof Boolean) {
      return "<boolean" + nameValue + ">" + data + "</boolean>";
    } else if (data instanceof JSONArray) {
      StringBuilder builder = new StringBuilder("<array" + nameValue + ">");
      for (int i = 0; i < ((JSONArray) data).length(); i++) {
        builder.append(convertToXml(null, ((JSONArray) data).get(i)));
      }
      builder.append("</array>");
      return builder.toString();
    } else if (data instanceof JSONObject) {
      StringBuilder builder = new StringBuilder("<object" + nameValue + ">");
      Iterator it = ((JSONObject) data).keys();
      while (it.hasNext()) {
        String key = it.next().toString();
        Object objData = ((JSONObject) data).get(key);
        builder.append(convertToXml(key, objData));
      }
      builder.append("</object>");
      return builder.toString();
    } else if (data == JSONObject.NULL) {
      return "<null" + nameValue + "/>";
    } else {
      throw new UnsupportedOperationException("Data type "
          + data.getClass() + " not yet supported");
    }
  }
}
