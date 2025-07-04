variable "repository_name" {
  description = "Name of the ECR repository"
  type        = string
}

variable "images_to_keep" {
  description = "Number of images to keep in the repository"
  type        = number
  default     = 10
}

variable "allowed_account_ids" {
  description = "List of AWS account IDs that are allowed to pull images"
  type        = list(string)
  default     = []
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
  default     = {}
}
