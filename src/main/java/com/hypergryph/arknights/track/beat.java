package com.hypergryph.arknights.track;import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;@RestController
public class beat
{
  @RequestMapping({"/beat"})
  public JSONObject Beat() {
     JSONObject result = new JSONObject(true);
     result.put("code", Integer.valueOf(200));
     result.put("msg", "ok");
     return result;
  }
}