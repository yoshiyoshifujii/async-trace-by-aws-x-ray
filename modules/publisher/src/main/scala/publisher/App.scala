package publisher

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.xray.AWSXRay
import com.amazonaws.xray.handlers.TracingHandler

class App extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val region = sys.env.getOrElse("region", "us-east-1")
    val queueUrl = sys.env("queueUrl")
    val sqs = AmazonSQSClient.builder()
      .withRegion(region)
      .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder))
      .build()
    val traceId = sys.env("_X_AMZN_TRACE_ID")

    sqs.sendMessage(queueUrl, traceId)
  }

}
