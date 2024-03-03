package com.hypergryph.arknights;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.command.CommandManager;
import com.hypergryph.arknights.command.ICommandSender;
import com.hypergryph.arknights.console;
import com.hypergryph.arknights.core.dao.mailDao;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.file.IOTools;
import com.hypergryph.arknights.core.function.randomPwd;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ArknightsApplication {
  public static final Logger LOGGER = LogManager.getLogger();
  public static JdbcTemplate jdbcTemplate = null;
  public static JSONObject serverConfig = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/config.json");
  public static boolean enableServer = serverConfig.getJSONObject("server").getBooleanValue("enableServer");
  public static JSONObject DefaultSyncData = new JSONObject();
  public static JSONObject characterJson = new JSONObject();
  public static JSONObject roguelikeTable = new JSONObject();
  public static JSONObject stageTable = new JSONObject();
  public static JSONObject itemTable = new JSONObject();
  public static JSONObject mainStage = new JSONObject();
  public static JSONObject normalGachaData = new JSONObject();
  public static JSONObject uniequipTable = new JSONObject();
  public static JSONObject skinGoodList = new JSONObject();
  public static JSONObject skinTable = new JSONObject();
  public static JSONObject charwordTable = new JSONObject();
  public static JSONObject CrisisData = new JSONObject();
  public static JSONObject CashGoodList = new JSONObject();
  public static JSONObject GPGoodList = new JSONObject();
  public static JSONObject LowGoodList = new JSONObject();
  public static JSONObject HighGoodList = new JSONObject();
  public static JSONObject ExtraGoodList = new JSONObject();
  public static JSONObject LMTGSGoodList = new JSONObject();
  public static JSONObject EPGSGoodList = new JSONObject();
  public static JSONObject RepGoodList = new JSONObject();
  public static JSONObject FurniGoodList = new JSONObject();
  public static JSONObject SocialGoodList = new JSONObject();
  public static JSONObject AllProductList = new JSONObject();
  public static JSONObject unlockActivity = new JSONObject();
  public static JSONObject roguelike = new JSONObject();
  public static JSONArray loadedModNameList = new JSONArray();
  public static JSONArray loadedModPathList = new JSONArray();
  public static JSONArray loadedModDownloadList = new JSONArray();
  public static JSONArray loadedModList = new JSONArray();

  public static JSONObject buildingData = new JSONObject();

  public static CommandManager ConsoleCommandManager = new CommandManager();

  public static ICommandSender Sender = () -> "Console";

  public static void main(String[] args) throws Exception {
    String host = serverConfig.getJSONObject("database").getString("host");
    String port = serverConfig.getJSONObject("database").getString("port");
    String dbname = serverConfig.getJSONObject("database").getString("dbname");
    String username = serverConfig.getJSONObject("database").getString("username");
    String password = serverConfig.getJSONObject("database").getString("password");
    String extra = serverConfig.getJSONObject("database").getString("extra");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + dbname + "?" + extra);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    jdbcTemplate = new JdbcTemplate((DataSource) dataSource);

    SpringApplication springApplication = new SpringApplication(
        new Class[] { com.hypergryph.arknights.ArknightsApplication.class });
    springApplication.setBannerMode(Banner.Mode.OFF);

    String[] disabledCommands = { "--server.port=" + serverConfig.getJSONObject("server").getString("https"),
        "--spring.profiles.active=default" };

    String[] fullArgs = StringUtils.concatenateStringArrays(args, disabledCommands);
    springApplication.run(fullArgs);

    reloadServerConfig();

    String MysqlVersion = null;
    LOGGER.info("检测数据库版本中...");
    try {
      MysqlVersion = userDao.queryMysqlVersion();
    } catch (Exception e) {
      LOGGER.error("无法连接至Mysql数据库");
      System.exit(0);
    }

    if (Integer.valueOf(MysqlVersion.substring(0, 1)).intValue() < 8) {
      LOGGER.error("Mysql版本需要 >= 8.0");
      LOGGER.error("请升级后重试");
      System.exit(0);
    }

    LOGGER.info("数据库版本 " + MysqlVersion);
    LOGGER.info("服务端版本 1.9.4 Beta 3");
    LOGGER.info("客户端版本 1.7.51");
    LOGGER.info("构建时间 2022年03月15日14时18分");
    if (serverConfig.getJSONObject("server").getString("GMKey") == null) {
      serverConfig.getJSONObject("server").put("GMKey", randomPwd.getRandomPwd(64));
      IOTools.SaveJsonFile(System.getProperty("user.dir") + "/config.json", serverConfig);
      LOGGER.info("已生成新的随机管理员密钥");
    }
    LOGGER.info("管理员密钥 " + serverConfig.getJSONObject("server").getString("GMKey"));

    if (!userDao.tableExists("account").booleanValue()) {
      userDao.insertTable();
      LOGGER.info("检测到玩家数据库不存在，已自动生成");
    }

    if (!userDao.tableExists("mail").booleanValue()) {
      mailDao.insertTable();
      LOGGER.info("检测到邮件数据库不存在，已自动生成");
    }

    getTimestamp();

    LOGGER.info("启动完成! 如果需要获取帮助,请输入 \"help\"");

    (new console()).start();
  }

  public static void LoadMods() {
    loadedModNameList = new JSONArray();
    loadedModPathList = new JSONArray();
    loadedModDownloadList = new JSONArray();
    loadedModList = new JSONArray();

    JSONArray fileList = new JSONArray();

    searchDirectoryFile(new File(System.getProperty("user.dir") + "/mods"), fileList);

    for (Object filePath : fileList) {

      File modFile = new File(filePath.toString());

      ZipFile zipFile = null;
      try {
        zipFile = new ZipFile(modFile);

        if (zipFile.size() == 0)
          continue;
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {

          ZipEntry entry = entries.nextElement();
          if (!entry.isDirectory() &&
              entry.getName().indexOf(".ab") != -1) {
            String modName = entry.getName();
            if (loadedModNameList.contains(modName)) {
              LOGGER.error(filePath + " 与已加载的Mod冲突，详细：");
              LOGGER.error(modName);

              continue;
            }
            InputStream modInputStream = zipFile.getInputStream(entry);

            byte[] buff = new byte[1024];

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int num;
            while ((num = modInputStream.read(buff, 0, buff.length)) != -1) {
              byteArrayOutputStream.write(buff, 0, num);
            }
            byte[] modBuff = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            long totalSize = modFile.length();
            long abSize = modBuff.length;
            String modMd5 = DigestUtils.md5DigestAsHex(modBuff);

            JSONObject abInfo = new JSONObject(true);
            abInfo.put("name", modName);
            abInfo.put("hash", modMd5);
            abInfo.put("md5", modMd5);
            abInfo.put("totalSize", Long.valueOf(totalSize));
            abInfo.put("abSize", Long.valueOf(abSize));
            LOGGER.info(modName + " 已加载");
            loadedModList.add(abInfo);
            loadedModPathList.add(modFile.getPath().replace("\\", "/"));
            loadedModNameList.add(modName);
            modName = modName.replace("/", "_");
            modName = modName.replace("#", "__");
            modName = modName.replace(".ab", ".dat");
            loadedModDownloadList.add(modName);
          }
        }

        zipFile.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void searchDirectoryFile(File directoryPath, JSONArray fileList) {
    File[] directory = directoryPath.listFiles();
    for (File file : directory) {
      if (file.isDirectory())
        searchDirectoryFile(file, fileList);
      if (file.isFile())
        fileList.add(file.getPath().replace("\\", "/"));
    }
  }

  public static String getIpAddr(HttpServletRequest request) {
    String ipAddress = null;
    try {
      ipAddress = request.getHeader("x-forwarded-for");
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("Proxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getHeader("WL-Proxy-Client-IP");
      }
      if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("127.0.0.1")) {

          try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
        }
      }

      if (ipAddress != null) {
        if (ipAddress.contains(",")) {
          return ipAddress.split(",")[0];
        }
        return ipAddress;
      }

      return "";
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public static long getTimestamp() {
    long ts = serverConfig.getJSONObject("timestamp")
        .getLongValue(DateUtil.dayOfWeekEnum((Date) DateUtil.date()).toString().toLowerCase());
    if (ts == -1L) {
      ts = (new Date()).getTime() / 1000L;
    }
    return ts;
  }

  public static void reloadServerConfig() {
    long startTime = System.currentTimeMillis();
    LOGGER.info("载入服务器配置...");
    serverConfig = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/config.json");
    enableServer = serverConfig.getJSONObject("server").getBooleanValue("enableServer");
    LOGGER.info("载入游戏数据...");
    DefaultSyncData = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/defaultSyncData.json");
    characterJson = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/character_table.json");
    roguelikeTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/roguelike_topic_table.json");
    stageTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/stage_table.json")
        .getJSONObject("stages");
    itemTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/item_table.json")
        .getJSONObject("items");
    mainStage = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/battle/stage.json")
        .getJSONObject("MainStage");
    normalGachaData = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/normalGacha.json");
    uniequipTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/uniequip_table.json")
        .getJSONObject("equipDict");
    skinGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/SkinGoodList.json");
    skinTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/skin_table.json")
        .getJSONObject("charSkins");
    charwordTable = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/charword_table.json");
    CashGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/CashGoodList.json");
    GPGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/GPGoodList.json");
    LowGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/LowGoodList.json");
    HighGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/HighGoodList.json");
    ExtraGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/ExtraGoodList.json");
    LMTGSGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/LMTGSGoodList.json");
    EPGSGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/EPGSGoodList.json");
    RepGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/RepGoodList.json");
    FurniGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/FurniGoodList.json");
    SocialGoodList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/SocialGoodList.json");
    AllProductList = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/shop/AllProductList.json");
    unlockActivity = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/unlockActivity.json");
    roguelike = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/roguelike.json");

    CrisisData = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/battle/crisis.json");
    buildingData = IOTools.ReadJsonFile(System.getProperty("user.dir") + "/data/excel/building_data.json")
        .getJSONObject("workshopFormulas");

    LOGGER.info("载入游戏Mod");
    LoadMods();

    long endTime = System.currentTimeMillis();
    LOGGER.info("载入完成，耗时：" + (endTime - startTime) + "ms");
  }
}