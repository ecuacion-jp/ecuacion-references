# code-generator-web Setup

## 1. Clone the Repository and Build

Follow the same steps as [code-generator-batch Setup](/public/en/article?id=code-generator-batch/setup)
to clone and build the repository.

```bash
git clone https://github.com/ecuacion-jp/ecuacion-tool-code-generator.git
cd ecuacion-tool-code-generator
mvn clean install -DskipTests
```

## 2. Configure the Work Directory

The web module temporarily stores uploaded Excel files and generated output on the server.
Specify the storage location with the `app.work-root-dir` property.

Create `application-profile.properties` in the CLASSPATH directory of the application server
and add the following:

```properties
app.work-root-dir=/path/to/work/directory
```

### Local Development Configuration

Edit `application-profile.properties` in
`ecuacion-tool-code-generator-web/src/envs/local/resources/`:

```properties
app.work-root-dir=/tmp/code-generator-work
```

## 3. Deploy to Application Server (Production)

Build and deploy the WAR file from the `ecuacion-tool-code-generator-web` module to Tomcat or another server.

```bash
cd ecuacion-tool-code-generator/ecuacion-tool-code-generator-web
mvn package
```

The WAR file is generated at `target/ecuacion-tool-code-generator-web-x.x.x.war`.

## System Requirements

- JDK 21 or above
- Maven 3.x
- A Java application server such as Tomcat (for server deployment)
