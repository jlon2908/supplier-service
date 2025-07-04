##########################################
# 🔐 SECRETS (provenientes de GitHub Secrets)
##########################################

variable "environment" {
  type        = string
  description = "Deployment environment (e.g. dev, prod)"
}

variable "project" {
  type        = string
  description = "Project name"
}

variable "db_r2dbc_url" {
  description = "R2DBC connection URL for the database"
  type        = string
}

variable "db_jdbc_url" {
  description = "JDBC connection URL for the database"
  type        = string
}

variable "db_username" {
  description = "Database username"
  type        = string
}

variable "db_password" {
  description = "Database password"
  type        = string
}

variable "jwt_secret" {
  description = "JWT secret used for authentication"
  type        = string
}



variable "name_prefix" {
  description = "Prefix to use for naming AWS resources"
  type        = string
  default     = "arka"
}

variable "aws_region" {
  description = "AWS region where resources will be deployed"
  type        = string
  default     = "us-east-1"
}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
  default     = {
    Project     = "catalog-service"
    Environment = "dev"
    Owner       = "arka"
  }
}

variable "container_image" {
  description = "Docker image URI for the container"
  type        = string
}

variable "vpc_id" {
  description = "ID de la VPC principal"
  type        = string
}

variable "public_subnets" {
  description = "IDs de las subnets públicas"
  type        = list(string)
}

variable "private_subnets" {
  description = "IDs de las subnets privadas"
  type        = list(string)
}
