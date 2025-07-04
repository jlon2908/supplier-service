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
  default     = 8080
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
