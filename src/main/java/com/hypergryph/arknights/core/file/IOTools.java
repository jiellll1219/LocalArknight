package com.hypergryph.arknights.core.file;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;public class IOTools {
  public static String ReadNormalFile(String FilePath) {
    try {
       File jsonFile = new File(FilePath);
       FileReader fileReader = new FileReader(jsonFile);
       Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "UTF-8");
       StringBuffer Buffer = new StringBuffer(); int ReadChar;
       while ((ReadChar = reader.read()) != -1) {
         Buffer.append((char)ReadChar);
      }
       fileReader.close();
       reader.close();
       return Buffer.toString();
     } catch (IOException e) {
       e.printStackTrace();
       return null;
    } 
  }  
  public static JSONObject ReadJsonFile(String JsonFilePath) {
    try {
       File jsonFile = new File(JsonFilePath);
       FileReader fileReader = new FileReader(jsonFile);
       Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "UTF-8");
       StringBuffer Buffer = new StringBuffer(); int ReadChar;
       while ((ReadChar = reader.read()) != -1) {
         Buffer.append((char)ReadChar);
      }
       fileReader.close();
       reader.close();
       return JSONObject.parseObject(Buffer.toString(), new Feature[] { Feature.OrderedField });
     } catch (IOException e) {
       e.printStackTrace();
       return null;
    } 
  }
  
  public static Boolean SaveJsonFile(String JsonFilePath, JSONObject JsonData) {
    try {
       OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(JsonFilePath), "UTF-8");
       osw.write(JSON.toJSONString(JsonData, new SerializerFeature[] { SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue }));
       osw.flush();
       osw.close();
       return Boolean.valueOf(true);
     } catch (IOException e) {
       e.printStackTrace();
       return Boolean.valueOf(false);
    } 
  }
}
