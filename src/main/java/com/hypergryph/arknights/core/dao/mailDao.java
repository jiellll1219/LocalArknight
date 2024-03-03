package com.hypergryph.arknights.core.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.core.pojo.Mail;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

public class mailDao {
   public static List<Mail> queryMails() {
      String sql = "SELECT * from mail";
      BeanPropertyRowMapper<Mail> rowMapper = new BeanPropertyRowMapper(Mail.class);
      return ArknightsApplication.jdbcTemplate.query(sql, (RowMapper) rowMapper);
   }

   public static List<Mail> queryMailById(int id) {
      String sql = "SELECT * FROM mail WHERE id = ?";
      BeanPropertyRowMapper<Mail> rowMapper = new BeanPropertyRowMapper(Mail.class);
      Object[] params = { Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.query(sql, params, (RowMapper) rowMapper);
   }

   public static List<Mail> queryMailByName(String name) {
      String sql = "SELECT * FROM mail WHERE name = ?";
      BeanPropertyRowMapper<Mail> rowMapper = new BeanPropertyRowMapper(Mail.class);
      Object[] params = { name };
      return ArknightsApplication.jdbcTemplate.query(sql, params, (RowMapper) rowMapper);
   }

   public static int setMailName(int id, String name) {
      String sql = "UPDATE mail SET name = ? where id = ?";
      Object[] params = { name, Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int setMailFrom(int id, String from) {
      String sql = "UPDATE mail SET `from` = ? where id = ?";
      Object[] params = { from, Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int setMailSubject(int id, String subject) {
      String sql = "UPDATE mail SET subject = ? where id = ?";
      Object[] params = { subject, Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int setMailContent(int id, String content) {
      String sql = "UPDATE mail SET content = ? where id = ?";
      Object[] params = { content, Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int setMailItems(int id, JSONArray items) {
      String sql = "UPDATE mail SET items = ? where id = ?";
      Object[] params = { JSON.toJSONString(items, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }),
            Integer.valueOf(id) };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int createMail(String name) {
      String sql = "insert into mail (`name`, `items`) value (?, '[]');";
      Object[] params = { name };
      return ArknightsApplication.jdbcTemplate.update(sql, params);
   }

   public static int insertTable() {
      String sql = "create table mail\n(\n    id      int auto_increment\n        primary key,\n    name  text null,\n    `from`  text null,\n    subject text null,\n    content text null,\n    items   json null\n);";
      return ArknightsApplication.jdbcTemplate.update(sql);
   }
}