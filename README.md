Rastajax
========

> **IMPORTANT**: This project is in its early stages of evolution. There are currently NO releases available, just the source code. As soon as I have a first release ready I'll get it pushed to Maven Central. Keep your eyes on [issue #1](https://github.com/kelveden/rastajax/issues/1) for an indication of when this is done. Whilst it is already usable, I strongly suggest that you hold off trying Rastajax until then as there are likely to be a fair few changes breaking backwards compatibility until then.

A lightweight library for dynamically generating descriptions REST APIs. Please see the wiki for an in-depth discussion. The rest of this page is a brief overview and quick start.

Key features
------------

* Standards compliant. Generation of descriptions is based entirely on the annotations from the [JAX-RS 1.1 Specification](http://jsr311.java.net/) plus a single custom annotation.
* Completely independent of any JAX-RS implementation.
* Lightweight. Dependencies both direct and transitive are kept to a minimum. 
* Extensible. Rastajax can be extended with classes that:
   * implement alternative strategies for finding resource classes.
   * use the information scanned in from your resource classes to describe your API in alternative ways

What's in the box
-----------------
Rastajax comes as 3 Maven artifacts:

* _rastajax-core_: The core library. This is the minimum you will need to add to your REST application's classpath.
* _rastajax-representation_: Contains the default REST API description representations supported by Rastajax. You can extend Rastajax with your own representations either in your own codebase or (even better!) to the _rastajax-representation_ library itself. See [the wiki](https://github.com/kelveden/rastajax/wiki/Extending-Rastajax) for more details.
* _rastajax-servlet_: Contains the default servlets that you can use to quickly try out Rastajax. See the "Quick start" section below for more details.

Quick start
-----------

Rastajax ships with some default servlets that you can hook in to your REST application to quickly try it out. Here's what you need to do:

1) Add the _rastajax-servlet_ and _rastajax-core_ libraries to your REST application classpath. If you're using Maven as your build tool, this is straightforward enough; add this to your POM:

```
<groupId>com.kelveden.rastajax</groupId>
<artifactId>rastajax-servlet</artifactId>
<version>???</version>
<scope>runtime</scope>
```

(Maven will pull in _rastajax-core_ as a transitive dependency.)

2) Hook the servlet(s) into your application web.xml:

```
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

