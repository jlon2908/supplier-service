variable "name_prefix" {
  description = "Prefix for naming ECS resources"
  type        = string
}

variable "aws_region" {
  description = "AWS region to deploy ECS resources"
  type        = string
}

variable "container_image" {
  description = "Docker image URI for the container"
  type        = string
}

variable "container_port" {
  description = "Port the container listens on"
  type        = number
  default     = 8082
}

variable "container_environment" {
  description = "List of environment variables to pass to container"
  type = list(object({
    name  = string
    value = string
  }))
}

variable "vpc_id" {
  description = "VPC ID for ECS networking"
  type        = string
}

variable "private_subnets" {
  description = "Private subnets for ECS Fargate"
  type        = list(string)
}

variable "target_group_arn" {
  description = "ARN of the target group for ALB"
  type        = string
}

variable "vpc_link_security_group_id" {
  description = "Security group ID to allow ingress to ECS service"
  type        = string
}

variable "alb_security_group_id" {
  description = "Security group ID of the ALB"
  type        = string
}

variable "tags" {
  description = "Common tags"
  type        = map(string)
  default     = {}
}

variable "task_execution_role_arn" {
  description = "ARN of the existing ECS task execution role"
  type        = string
}

variable "log_group_name" {
  description = "Name of the existing CloudWatch log group"
  type        = string
}

variable "ecs_service_security_group_id" {
  description = "ID of the existing ECS service security group"
  type        = string
}
