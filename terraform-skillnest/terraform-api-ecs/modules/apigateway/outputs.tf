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
  value       = aws_security_group.vpc_link.id
}
