# EveKit Model Frontend

This module provides a servlet which exposes EveKit model data through a REST API.  We use [Swagger](http://swagger.io) to annotate our REST API, which in turn makes it trivial to generate documentation and experiment with the API, as well as generate client libraries in various languages.  A public instance of this module runs [here](https://evekit-model.orbital.enterprises).  However, the servlet for this module only exposes the REST API and a Swagger configuration file for driving supporting tools.  You can always view the live Swagger configuration file [here](https://evekit-model.orbital.enterprises/api/swagger.json).

This module assumes you have already set up an appropriate database with EveKit account and model data.  See the [EveKit Frontend](https://github.com/OrbitalEnterprises/evekit-frontend) page for an overview of the EveKit service and instructions for setting up a backing database.

The rest of this guide describes how to configure, build and deploy the EveKit Model Frontend.

## Configuration

The model frontend requires the setting and substitution of several parameters which control database and servlet settings.  Since the model frontend is normally built with [Maven](http://maven.apache.org), configuration is handled by setting or overriding properties in your local Maven settings.xml file.  The following configuration parameters should be set:

| Parameter | Meaning |
|-----------|---------|
|enterprises.orbital.evekit.model-frontend.basepath|The base location where the servlet is hosted, e.g. http://localhost:8080|
|enterprises.orbital.evekit.model-frontend.appname|Name of the servlet when deployed|
|enterprises.orbital.evekit.model-frontend.db.properties.url|Hibernate JDBC connection URL for properties|
|enterprises.orbital.evekit.model-frontend.db.properties.user|Hibernate JDBC connection user name for properties|
|enterprises.orbital.evekit.model-frontend.db.properties.password|Hibernate JDBC connection password for properties|
|enterprises.orbital.evekit.model-frontend.db.properties.driver|Hibernate JDBC driver class name for properties|
|enterprises.orbital.evekit.model-frontend.db.properties.dialect|Hibernate dialect class name for properties|
|enterprises.orbital.evekit.model-frontend.db.account.url|Hibernate JDBC connection URL for account info|
|enterprises.orbital.evekit.model-frontend.db.account.user|Hibernate JDBC connection user name for account info|
|enterprises.orbital.evekit.model-frontend.db.account.password|Hibernate JDBC connection password for user info|
|enterprises.orbital.evekit.model-frontend.db.account.driver|Hibernate JDBC driver class name for account info|
|enterprises.orbital.evekit.model-frontend.db.account.dialect|Hibernate dialect class name for account info|

As with all EveKit components, two database connections are required: one for retrieving general settings for system and user accounts; and, one for retrieving user account and model information.  The can be (and often are) the same database.

At build and deploy time, the parameters above are substituted into the following files:

* src/main/resources/META-INF/persistence.xml
* src/main/resources/EveKitModelFrontend.properties
* src/main/webapp/WEB-INF/web.xml

If you are not using Maven to build, you'll need to substitute these settings manually.

## Build

We use [Maven](http://maven.apache.org) to build all EveKit modules.  EveKit dependencies are released and published to [Maven Central](http://search.maven.org/).  EveKit front ends are released but must be installed by cloning a repository.  To build the EveKit Model Frontend, clone this repository and use "mvn install".  Make sure you have set all required configuration parameters before building (as described in the previous section).

## Deployment

This project is designed to easily deploy in a standard Servlet container.  Two parameters need to be substituted in the web.xml file in order for deployment to work correctly:

| Parameter | Meaning |
|-----------|---------|
|enterprises.orbital.evekit.model-frontend.basepath|The base location where the servlet is hosted, e.g. http://localhost:8080|
|enterprises.orbital.evekit.model-frontend.appname|Name of the servlet when deployed|

If you follow the configuration and build instructions above, these parameters will be substituted for you.  These settings are used to define the base path for the REST API endpoints (via Swagger).

### Deploying to Tomcat

The default pom.xml in the project includes the [Tomcat Maven plugin](http://tomcat.apache.org/maven-plugin.html) which makes it easy to deploy directly to a Tomcat instance.  This is normally done by adding two stanzas to your settings.xml:

```xml
<servers>
  <server>
    <id>LocalTomcatServer</id>
    <username>admin</username>
    <password>password</password>
  </server>    
</servers>

<profiles>
  <profile>
    <id>LocalTomcat</id>
    <properties>
      <enterprises.orbital.evekit.model-frontend.tomcat.url>http://127.0.0.1:8080/manager/text</enterprises.orbital.evekit.model-frontend.tomcat.url>
      <enterprises.orbital.evekit.model-frontend.tomcat.server>LocalTomcatServer</enterprises.orbital.evekit.model-frontend.tomcat.server>
      <enterprises.orbital.evekit.model-frontend.tomcat.path>/evekit-model</enterprises.orbital.evekit.model-frontend.tomcat.path>
    </properties>	
  </profile>
</profiles>
```

The first stanza specifies the management credentials for your Tomcat instance.  The second stanza defines the properties needed to install into the server you just defined.  With these settings, you can deploy to your Tomcat instance as follows (this example uses Tomcat 7):

```
mvn -P LocalTomcat tomcat7:deploy
```

If you've already deployed, use "redploy" instead.  See the [Tomcat Maven plugin documentation](http://tomcat.apache.org/maven-plugin-2.2/) for more details on how the deployment plugin works.

## Usage

### Viewing Documentation and Trying the API with Swagger

You can always view the Swagger UI for the public instance of the model frontend from the public instance of [EveKit](https://evekit.orbital.enterprises), either by selecting API -> Model API, or by using this [direct link](https://evekit.orbital.enterprises//#/api/model/-1/-1/-1).

You can view the Swagger UI for another instance using the [Swagger UI online demo](http://petstore.swagger.io).  Navigate to the Swagger UI online demo page, then enter the URL for the Swagger configuration file for the instance you want to view.  Using the configuration properties above, the URL for the Swagger configuration file is always ${enterprises.orbital.evekit.model-frontend.basepath}/${enterprises.orbital.evekit.model-frontend.appname}/api/swagger.json .

### Using Swagger to Generate Client Code

The model frontend REST API can be accessed directly by using the paths and arguments described in the API documentation.  However, you can also use the Swagger configuration file to automatically generate appropriate client code.  There are two ways to do this.

For a Javascript client, there is no need to generate client code statically.  Instead, you can use the [Swagger Javascript](https://github.com/swagger-api/swagger-js) module.  For example, the following HTML snippet will create a Javascript client from the public model frontend instance (note the use of rawgit.com to return a proper content type):

```html
<!-- Set up Swagger -->
<script src='https://cdn.rawgit.com/swagger-api/swagger-js/master/browser/swagger-client.min.js' type='text/javascript'></script>
<script type="text/javascript">
  var url = "https://evekit-model.orbital.enterprises/api/swagger.json";
  window.swagger = new SwaggerClient({ 
    url: url,
    success: function() { /* called when the client is ready */ }
  });
</script>
```

For a non-Javascript client, you can use the [Swagger Editor online demo](http://editor.swagger.io/#/).  To generate a client for the public model frontend instance, type in "https://evekit-model.orbital.enterprises/api/swagger.json" under "File->Import URL..", then use the "Generate Client" menu to download an appropriate client.

## Getting Help

The best place to get help is on the [Orbital Forum](https://groups.google.com/forum/#!forum/orbital-enterprises).
