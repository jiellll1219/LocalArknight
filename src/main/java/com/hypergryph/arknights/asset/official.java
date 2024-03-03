package com.hypergryph.arknights.asset;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.file.IOTools;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/assetbundle/official/{os}/assets" })
public class official {
  private static final Logger LOGGER = LogManager.getLogger();

  @RequestMapping({ "/{assetsHash}/{fileName}" })
  public ResponseEntity<FileSystemResource> getFile(@PathVariable("os") String os,
      @PathVariable("assetsHash") String assetsHash, @PathVariable("fileName") String fileName,
      HttpServletResponse response, HttpServletRequest request) throws IOException {
    String clientIp = ArknightsApplication.getIpAddr(request);

    Boolean redirect = Boolean
        .valueOf(ArknightsApplication.serverConfig.getJSONObject("assets").getBooleanValue("enableRedirect"));
    String version = ArknightsApplication.serverConfig.getJSONObject("version").getJSONObject("android")
        .getString("resVersion");

    if (ArknightsApplication.loadedModDownloadList.contains(fileName)) {
      for (int i = 0; i < ArknightsApplication.loadedModDownloadList.size(); i++) {
        if (ArknightsApplication.loadedModDownloadList.getString(i).equals(fileName)) {
          File file1 = new File(ArknightsApplication.loadedModPathList.getString(i));
          if (file1.exists()) {
            return export(file1, assetsHash);
          }
        }
      }
    }

    String filePath = System.getProperty("user.dir") + "/assets/" + version + "/direct/";
    if (redirect.booleanValue()) {
      filePath = System.getProperty("user.dir") + "/assets/" + version + "/redirect/";
      JSONArray localFiles = ArknightsApplication.serverConfig.getJSONObject("assets").getJSONArray("localFiles");
      if (!localFiles.contains(fileName)) {
        response.sendRedirect("https://ak.hycdn.cn/assetbundle/official/Android/assets/" + version + "/" + fileName);
        return null;
      }
    }

    File file = new File(filePath, fileName);
    if (file.exists()) {
      return export(file, assetsHash);
    }
    LOGGER.warn("正在下载 " + version + "/" + fileName);
    HttpUtil.downloadFile("https://ak.hycdn.cn/assetbundle/official/Android/assets/" + version + "/" + fileName,
        filePath + fileName);

    file = new File(filePath, fileName);
    if (file.exists()) {
      LOGGER.info("[/" + clientIp + "] /" + version + "/" + fileName);
      return export(file, assetsHash);
    }
    return null;
  }

  public static String downLoadFromUrl(String urlStr, String fileName, String savePath) {
    try {
      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setConnectTimeout(3000);

      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

      InputStream inputStream = conn.getInputStream();

      byte[] getData = readInputStream(inputStream);

      File saveDir = new File(savePath);
      if (!saveDir.exists()) {
        saveDir.mkdir();
      }

      File dir = new File(saveDir + File.separator);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      File file = new File(dir, fileName);
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(getData);
      if (fos != null) {
        fos.close();
      }
      if (inputStream != null) {
        inputStream.close();
      }

      return saveDir + File.separator + fileName;
    } catch (Exception e) {
      e.printStackTrace();

      return "";
    }
  }

  public static byte[] readInputStream(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024];
    int len = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((len = inputStream.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    bos.close();
    return bos.toByteArray();
  }

  public ResponseEntity<FileSystemResource> export(File file, String assetsHash) {
    if (file == null) {
      return null;
    }
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "attachment; filename=" + file.getName());
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");
    headers.add("Last-Modified", (new Date()).toString());
    headers.add("ETag", String.valueOf(System.currentTimeMillis()));

    if (file.getName().equals("hot_update_list.json")) {
      JSONObject hot_update_list = IOTools.ReadJsonFile(file.getPath());

      hot_update_list.put("versionId", assetsHash);

      JSONArray newAbInfos = new JSONArray();
      Iterator<Object> iterator = hot_update_list.getJSONArray("abInfos").iterator();

      while (iterator.hasNext()) {
        JSONObject abInfo = (JSONObject) iterator.next();
        if (abInfo.getString("hash").length() == 24) {
          abInfo.put("hash", assetsHash);
        }
        if (!ArknightsApplication.loadedModNameList.contains(abInfo.getString("name"))) {
          newAbInfos.add(abInfo);
        }
      }

      for (int i = 0; i < ArknightsApplication.loadedModList.size(); i++) {
        newAbInfos.add(ArknightsApplication.loadedModList.getJSONObject(i));
      }

      hot_update_list.put("abInfos", newAbInfos);
      IOTools.SaveJsonFile(System.getProperty("user.dir") + "/cache/hot_update_list.json", hot_update_list);
      file = new File(System.getProperty("user.dir") + "/cache/hot_update_list.json");
    }
    return ((ResponseEntity.BodyBuilder) ResponseEntity.ok().headers(headers)).contentLength(file.length())
        .contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
  }
}