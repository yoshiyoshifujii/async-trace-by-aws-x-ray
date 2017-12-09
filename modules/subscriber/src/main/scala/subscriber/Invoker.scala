package subscriber

import java.io.{InputStream, OutputStream}

import com.amazonaws.ClientConfiguration
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.model.{InvokeRequest, LogType}
import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.xray.AWSXRay
import com.amazonaws.xray.handlers.TracingHandler

import scala.collection.JavaConverters._

class Invoker extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context) = {
    val region = sys.env.getOrElse("region", "us-east-1")
    val queueUrl = sys.env("queueUrl")
    val functionName = sys.env("functionName")
    val sqs = AmazonSQSClient.builder()
      .withRegion(region)
      .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder))
      .build

    val receiveMessageResult = sqs.receiveMessage(queueUrl)
    receiveMessageResult.getMessages
      .asScala
      .foreach { message =>
        val clientConfiguration = new ClientConfiguration()
        clientConfiguration.addHeader("x-amzn-trace-id", message.getBody)
        val lambda = AWSLambdaClient.builder()
          .withRegion(region)
          .withClientConfiguration(clientConfiguration)
          .build
        val invokeRequest = new InvokeRequest()
          .withFunctionName(functionName)
          .withPayload(s"""{"id":"${message.getBody}"}""")
          .withLogType(LogType.Tail)
        lambda.invoke(invokeRequest)
        sqs.deleteMessage(queueUrl, message.getReceiptHandle)
      }
  }
}
