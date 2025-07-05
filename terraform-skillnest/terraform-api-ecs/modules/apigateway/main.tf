# API Gateway
resource "aws_apigatewayv2_api" "main" {
  name          = "${var.name_prefix}-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_headers = ["Content-Type", "Authorization", "X-Amz-Date", "X-Api-Key", "X-Amz-Security-Token"]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_origins = var.allowed_origins
    max_age       = 300
  }

  tags = var.tags
}

# VPC Link
resource "aws_apigatewayv2_vpc_link" "main" {
  name               = "${var.name_prefix}-vpclink"
  security_group_ids = [var.vpc_link_security_group_id != null ? data.aws_security_group.vpc_link[0].id : aws_security_group.vpc_link[0].id]
  subnet_ids         = var.private_subnets
  tags               = var.tags
}

# API Stage
resource "aws_apigatewayv2_stage" "main" {
  api_id      = aws_apigatewayv2_api.main.id
  name        = "$default"
  auto_deploy = true

  access_log_settings {
    destination_arn = var.log_group_name != null ? data.aws_cloudwatch_log_group.api_gateway[0].arn : aws_cloudwatch_log_group.api_gateway[0].arn
    format = jsonencode({
      requestId        = "$context.requestId"
      requestTime      = "$context.requestTime"
      httpMethod       = "$context.httpMethod"
      path             = "$context.path"
      routeKey         = "$context.routeKey"
      status           = "$context.status"
      responseLength   = "$context.responseLength"
      integrationError = "$context.integrationErrorMessage"
    })
  }

  tags = var.tags
}

# Routes
resource "aws_apigatewayv2_route" "register" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "POST /user/register"
  target    = "integrations/${aws_apigatewayv2_integration.alb.id}"

  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "login" {
  api_id    = aws_apigatewayv2_api.main.id
  route_key = "POST /user/login"
  target    = "integrations/${aws_apigatewayv2_integration.alb.id}"

  authorization_type = "NONE"
}

# Integration with ALB
resource "aws_apigatewayv2_integration" "alb" {
  api_id           = aws_apigatewayv2_api.main.id
  integration_type = "HTTP_PROXY"
  integration_uri  = var.alb_listener_arn

  integration_method   = "ANY" # Cambiado a ANY para permitir cualquier método
  connection_type      = "VPC_LINK"
  connection_id        = aws_apigatewayv2_vpc_link.main.id
  timeout_milliseconds = 29000

  request_parameters = {
    "overwrite:path" = "$request.path"
  }

  tls_config {
    server_name_to_verify = var.alb_dns_name
  }
}

data "aws_security_group" "vpc_link" {
  count = var.vpc_link_security_group_id != null ? 1 : 0
  id    = var.vpc_link_security_group_id
}
