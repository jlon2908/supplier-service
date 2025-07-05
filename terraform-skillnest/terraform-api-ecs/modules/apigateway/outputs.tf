output "api_endpoint" {
  description = "Endpoint URL of the API Gateway"
  value       = aws_apigatewayv2_api.main.api_endpoint
}

output "stage_url" {
  description = "URL of the API Gateway stage"
  value       = aws_apigatewayv2_stage.main.invoke_url
}

output "vpc_link_security_group_id" {
  description = "ID of the VPC Link security group"
  value       = var.vpc_link_security_group_id != null ? var.vpc_link_security_group_id : aws_security_group.vpc_link[0].id
}

output "log_group_name" {
  description = "Nombre del Log Group usado por API Gateway"
  value       = var.log_group_name != null ? var.log_group_name : aws_cloudwatch_log_group.api_gateway[0].name
}
