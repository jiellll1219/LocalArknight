package com.hypergryph.arknights.auth;import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
@RequestMapping({"/captcha"})
public class captcha
{
  @RequestMapping({"/v1/register"})
  public JSONObject RegisterCaptcha() {
     JSONObject result = new JSONObject(true);
     result.put("result", Integer.valueOf(1));
     return result;
  }
}