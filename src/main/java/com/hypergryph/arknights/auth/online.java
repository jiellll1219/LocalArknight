package com.hypergryph.arknights.auth;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/online" })
public class online {
   private static final Logger LOGGER = LogManager.getLogger();

   @PostMapping(value = { "/v1/ping" }, produces = { "application/json;charset=UTF-8" })
   public JSONObject Ping(HttpServletRequest request) {
      JSONObject result = new JSONObject(true);
      result.put("result", Integer.valueOf(0));
      result.put("message", "OK");
      result.put("interval", Integer.valueOf(2242));
      result.put("timeLeft", Integer.valueOf(-1));
      result.put("alertTime", Integer.valueOf(600));
      return result;
   }

   @PostMapping(value = { "/v1/loginout" }, produces = { "application/json;charset=UTF-8" })
   public JSONObject LoginOut(@RequestHeader("secret") String secret, @RequestBody JSONObject JsonBody,
         HttpServletResponse response, HttpServletRequest request) {
      JSONObject jsonObject = new JSONObject(true);
      jsonObject.put("result", Integer.valueOf(0));
      return jsonObject;
   }
}