Rastajax
========
[![Build Status](https://secure.travis-ci.org/kelveden/rastajax.png?branch=master)](http://travis-ci.org/kelveden/rastajax)

A lightweight library for dynamically generating descriptions of JAX-RS-compliant REST APIs. Please see the wiki for an in-depth discussion. The rest of this page is a brief overview and quick start.

Key features
------------

* Standards compliant. Generation of descriptions is based on the annotations from the [JAX-RS 1.1 Specification](http://jsr311.java.net/).
* Completely independent of any JAX-RS implementation.
* Lightweight. Dependencies both direct and transitive are kept to a minimum. 
* Extensible. Rastajax can be extended with classes that:
   * implement alternative strategies for finding resource classes.
   * use the information scanned in from your resource classes to describe your API in alternative ways.

What's in the box
-----------------
Rastajax comes as 3 Maven artifacts:

* _rastajax-core_: The core library. This is the minimum you will need to add to your REST application's classpath.
* _rastajax-representation_: Contains the default REST API description representations supported by Rastajax. You can extend Rastajax with your own representations either in your own codebase or (even better!) to the _rastajax-representation_ library itself. See [the wiki](https://github.com/kelveden/rastajax/wiki/How-It-Works) for more details.
* _rastajax-servlet_: Contains the default servlets that you can use to quickly try out Rastajax. See the "Quick start" section below for more details.

Quick start
-----------

Rastajax ships with some default servlets that you can hook in to your REST application to quickly try it out. Here's what you need to do:

1) Add the _rastajax-servlet_ and _rastajax-core_ libraries to your REST application classpath. If you're using Maven as your build tool, this is straightforward enough; add this to your POM:

```xml
<dependency>
  <groupId>com.kelveden.rastajax</groupId>
  <artifactId>rastajax-servlet</artifactId>
  <version>0.9.0</version>
  <scope>runtime</scope>
</dependency>
```

(Maven will pull in _rastajax-core_ as a transitive dependency.)

2) Hook the servlet(s) into your application web.xml:

```xml
<context-param>
    <param-name>rastajax.apipackages</param-name>
    <param-value>your.rest.application.package1,your.rest.application.package2</param-value>
</context-param>

...

<servlet>
  <servlet-name>RastajaxHtml</servlet-name>
  <servlet-class>com.kelveden.rastajax.servlet.DefaultHtmlServlet</servlet-class>
</servlet>

<servlet>
  <servlet-name>RastajaxJson</servlet-name>
  <servlet-class>com.kelveden.rastajax.servlet.DefaultJsonServlet</servlet-class>
</servlet>

...

<servlet-mapping>
  <servlet-name>RastajaxHtml</servlet-name>
  <url-pattern>/resources.html</url-pattern>
</servlet-mapping>

<servlet-mapping>
  <servlet-name>RastajaxJson</servlet-name>
  <url-pattern>/resources.json</url-pattern>
</servlet-mapping>
```

That's it! Now just browse to the servlets and see examples of your API described as JSON and HTML. You can continue using these servlets of course but you'll probably want something more sophisticated longer term. See [the wiki](https://github.com/kelveden/rastajax/wiki/Using-Rastajax) for more details on creating how to create your own Rastajax integration.

Logging
-------
Rastasjax logs using the <a href="http://www.slf4j.org">SLF4J logging facade</a>. Why? So that you can continue using your application's existing logging framework without having to worry about managing yet another one.

If your application doesn't already use SLF4J, you'll need to wrap it up as an SLF4J binding using one of the several "bridging" libraries that SLF4J provide. The SLF4J website has <a href="http://www.slf4j.org/legacy.html">extensive notes</a> on doing this. Alternatively, you could switch to a logging framework that exposes itself as a SLF4J binding out of the box - e.g. <a href="http://logback.qos.ch/">Logback</a>.

Rastajax info-level logging is pretty minimal; however, if you enable debug-level logging you'll see a load of information explaining exactly how Rastajax processes your application. This is great for diagnostic purposes when Rastajax is processing a resource oddly (or just missing it altogether).
