package com.hypergryph.arknights;import com.hypergryph.arknights.ArknightsApplication;
import com.hypergryph.arknights.http;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;@Configuration
public class http {
    int ServerPort = ArknightsApplication.serverConfig.getJSONObject("server").getIntValue("https");
    int HttpPort = ArknightsApplication.serverConfig.getJSONObject("server").getIntValue("http");    @Bean
  public TomcatServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory object = new TomcatServletWebServerFactory();
    object.addAdditionalTomcatConnectors(new Connector[] { initiateHttpConnector() });
    return object;
  }    private Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(this.HttpPort);
        connector.setRedirectPort(this.ServerPort);
        connector.setSecure(true);
        return connector;
    }
}