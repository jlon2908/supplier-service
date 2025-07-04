# Data sources
data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

# Common tags and naming
locals {
  common_tags = merge(var.default_tags, {
    Environment = var.environment
    Project     = var.project
    Region      = data.aws_region.current.name
    Account     = data.aws_caller_identity.current.account_id
    ManagedBy   = "terraform"
  })

  name_prefix = "${var.project}-${var.environment}-${data.aws_region.current.name}"
}
