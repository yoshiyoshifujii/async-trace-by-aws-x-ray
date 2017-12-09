import sbt._

object Dependencies {

  val AwsSdkVersion = "1.11.245"
  val awsSQS      = "com.amazonaws" % "aws-java-sdk-sqs"        % AwsSdkVersion
  val awsDynamoDB = "com.amazonaws" % "aws-java-sdk-dynamodb"   % AwsSdkVersion
  val awsLambda   = "com.amazonaws" % "aws-java-sdk-lambda"   % AwsSdkVersion

  val AwsXRayVersion = "1.2.2"
  val awsXRayCore         = "com.amazonaws" % "aws-xray-recorder-sdk-core" % AwsXRayVersion
  val awsXRaySDK          = "com.amazonaws" % "aws-xray-recorder-sdk-aws-sdk" % AwsXRayVersion
  val awsXRayInstrumentor = "com.amazonaws" % "aws-xray-recorder-sdk-aws-sdk-instrumentor" % AwsXRayVersion

  // Amazon Lambda
  val lambdaCore = "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"

  val publisherDeps = Seq(
    awsSQS,
    awsXRayCore,
    awsXRaySDK,
    awsXRayInstrumentor,
    lambdaCore,
  )

  val subscriberDeps = Seq(
    awsSQS,
    awsDynamoDB,
    awsLambda,
    awsXRayCore,
    awsXRaySDK,
    awsXRayInstrumentor,
    lambdaCore
  )
}
