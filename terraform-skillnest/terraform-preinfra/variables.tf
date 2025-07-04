variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "project" {
  description = "Project name"
  type        = string
}

variable "default_tags" {
  description = "Default tags for all resources"
  type        = map(string)
  default     = {}
}

variable "default_tags_environment" {
  description = "Default tags for all resources"
  type        = map(string)
  default     = {}
}

# S3 Configuration
variable "artifacts_bucket_name" {
  description = "Name of the S3 bucket for storing artifacts"
  type        = string
}

# ECR Configuration
variable "ecr_repositories" {
  description = "List of ECR repositories to create"
  type = list(object({
    name             = string
    images_to_keep   = optional(number, 10)
    allowed_accounts = optional(list(string), [])
  }))
  default = []
}
