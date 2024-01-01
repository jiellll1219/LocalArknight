package com.hypergryph.arknights.game;import com.alibaba.fastjson.JSONObject;
import com.hypergryph.arknights.core.dao.userDao;
import com.hypergryph.arknights.core.pojo.Account;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
@RequestMapping({"/storyreview"})
public class storyreview
{
  @RequestMapping({"/readStory"})
  public JSONObject readStory() {
     return JSONObject.parseObject("{\"result\":0,\"rewards\":[],\"unlockStages\":[],\"alert\":[],\"playerDataDelta\":{\"modified\":{},\"deleted\":{}}}");
  }  
  @PostMapping(value = {"/markStoryAcceKnown"}, produces = {"application/json;charset=UTF-8"})
  public JSONObject markStoryAcceKnown(@RequestHeader("secret") String secret, HttpServletResponse response) {
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
    
     UserSyncData.getJSONObject("storyreview").getJSONObject("tags").put("knownStoryAcceleration", Integer.valueOf(1));
    
     userDao.setUserData(uid, UserSyncData);
    
     JSONObject result = new JSONObject(true);
     JSONObject playerDataDelta = new JSONObject(true);
     JSONObject modified = new JSONObject(true);
     JSONObject dungeon = new JSONObject(true);
     JSONObject stages = new JSONObject(true);
    
     dungeon.put("stages", stages);
     modified.put("storyreview", UserSyncData.getJSONObject("storyreview"));
     playerDataDelta.put("deleted", new JSONObject(true));
     playerDataDelta.put("modified", modified);
     result.put("playerDataDelta", playerDataDelta);
     return result;
  }
}