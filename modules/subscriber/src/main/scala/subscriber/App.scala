package subscriber

import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.amazonaws.xray.AWSXRay
import com.amazonaws.xray.handlers.TracingHandler

import scala.collection.JavaConverters._

class App extends RequestStreamHandler {

  private val region = sys.env.getOrElse("region", "us-east-1")
  private val tableName = sys.env("databaseName")

  override def handleRequest(input: InputStream, output: OutputStream, context: Context) = {

    val bytes = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray

    val dynamo = AmazonDynamoDBClient.builder()
      .withRegion(region)
      .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder))
      .build

    dynamo.putItem(tableName, Map(
      "id" -> new AttributeValue("id").withS(new String(bytes, StandardCharsets.UTF_8))
    ).asJava)
  }
}
