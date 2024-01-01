package com.hypergryph.arknights.game;import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
@RequestMapping({"/background"})
public class background
{
   private static final Logger LOGGER = LogManager.getLogger();  
  @PostMapping(value = {"/setBackground"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject SetBackground(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody, HttpServletResponse response, HttpServletRequest request) {
     String clientIp = ArknightsApplication.getIpAddr(request);
     LOGGER.info("[/" + clientIp + "] /background/setBackground");
    
     if (!ArknightsApplication.enableServer) {
       response.setStatus(400);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(400));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "server is close");
       return jSONObject;
    } 
    
     String bgID = JsonBody.getString("bgID");
    
     List<Account> Accounts = userDao.queryAccountBySecret(secret);
     if (Accounts.size() != 1) {
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("result", Integer.valueOf(2));
       jSONObject.put("error", "无法查询到此账户");
       return jSONObject;
    } 
    
     Long uid = Long.valueOf(((Account)Accounts.get(0)).getUid());
    
     if (((Account)Accounts.get(0)).getBan() == 1L) {
       response.setStatus(500);
       JSONObject jSONObject = new JSONObject(true);
       jSONObject.put("statusCode", Integer.valueOf(403));
       jSONObject.put("error", "Bad Request");
       jSONObject.put("message", "error");
       return jSONObject;
    }     
     JSONObject UserSyncData = JSONObject.parseObject(((Account)Accounts.get(0)).getUser());
    
     UserSyncData.getJSONObject("background").put("selected", bgID);
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     playerDataDelta.put("deleted", new JSONObject(true));
     JSONObject modified = new JSONObject(true);
     JSONObject jSONObject1 = new JSONObject(true);
     jSONObject1.put("selected", bgID);
     modified.put("background", jSONObject1);
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }
}