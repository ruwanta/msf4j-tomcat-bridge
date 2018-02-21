# Proof Of Concept MSF4J can be embedded in traditional Web-APP


## usage

Add the following to the web.xml
```XML
    <servlet>
        <servlet-name>msf4j</servlet-name>
        <servlet-class>Msf4jBridgeServlet</servlet-class>
        <init-param>
            <param-name>ServiceClasses</param-name>
            <!-- Comma sepereted service classes -->
            <param-value>com.wso2.carbon.identity.msf4j.bridge.hello.world.HelloService</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>msf4j</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
```
Build the root pom and then drop the .war found inside sample in Identity Server webapps directory.