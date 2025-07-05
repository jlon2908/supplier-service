variable "name_prefix" {
  description = "Prefix to use for resource names"
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC"
  type        = string
}

variable "private_subnets" {
  description = "List of private subnet IDs"
  type        = list(string)
}

variable "alb_listener_arn" {
  description = "ARN of the ALB listener"
  type        = string
}

variable "alb_dns_name" {
  description = "DNS name of the ALB"
  type        = string
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}

variable "allowed_origins" {
  description = "List of allowed origins for CORS"
  type        = list(string)
  default     = ["*"] # Puedes cambiar esto según tus necesidades
}

variable "vpc_link_security_group_id" {
  description = "ID de un Security Group existente para el VPC Link (opcional)"
  type        = string
  default     = null
}

variable "log_group_name" {
  description = "Nombre de un Log Group existente para API Gateway (opcional)"
  type        = string
  default     = null
}
