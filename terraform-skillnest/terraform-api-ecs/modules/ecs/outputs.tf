output "cluster_id" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.this.id
}

output "service_name" {
  description = "ECS service name"
  value       = aws_ecs_service.this.name
}

output "task_definition_arn" {
  description = "Task definition ARN"
  value       = aws_ecs_task_definition.this.arn
}

output "security_group_id" {
  description = "Security group ID for ECS service"
  value       = aws_security_group.ecs_service.id
}
