### Build ###

To build with unit tests please modify your credentials in BaseClientTest.java file
and run

    mvn clean package

To build without unit tests please run

    mvn clean package -DskipTests

### Installation ###

0) Create a Maven java project

1) Add a repository to your pom.xml (or settings.xml):

        <repositories>
            <repository>
                <id>bodhi-java-public</id>
                <layout>default</layout>
                <name>Hotschedules Public Maven Repository</name>
                <url>https://rbcplatform.artifactoryonline.com/rbcplatform/bodhi-java</url>
            </repository>
        </repositories>

2) Add this dependency to your pom.xml

        <dependency>
            <groupId>com.bodhi.superagent</groupId>
            <artifactId>bodhi-java-superagent</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>


### Example ###

    import com.bodhi.superagent.BasicCredentials;
    import com.bodhi.superagent.Client;
    import com.bodhi.superagent.Environment;
    import java.io.IOException;

    public class Test {

        public static void main(String[] args) throws IOException, InterruptedException {

            Client client = new Client(Environment.DEV, "<namespace>", new BasicCredentials("<login>", "<password>"));
            client.get("resources/Agent", null, result -> {
                System.out.println("Status code: " + result.getStatusCode());
                System.out.println("Agents count: " + result.getData().getArray().length());
                System.exit(0);

            });
            System.in.read();
        }
    }
