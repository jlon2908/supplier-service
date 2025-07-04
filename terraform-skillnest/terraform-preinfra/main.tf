
# VPC for the infrastructure
module "vpc" {
  source = "./modules/vpc"

  vpc_cidr           = "10.0.0.0/16"
  public_subnets     = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnets    = ["10.0.3.0/24", "10.0.4.0/24"]
  availability_zones = ["us-east-1a", "us-east-1b"]
  name_prefix        = local.name_prefix
  tags               = local.common_tags
}

# S3 Bucket for artifacts
module "artifacts_bucket" {
  source = "./modules/s3"

  bucket_name = var.artifacts_bucket_name
  tags        = local.common_tags
}



# ECR Repositories
module "ecr_repositories" {
  source   = "./modules/ecr"
  for_each = { for repo in var.ecr_repositories : repo.name => repo }

  repository_name     = "${each.value.name}"
  images_to_keep      = each.value.images_to_keep
  allowed_account_ids = concat([data.aws_caller_identity.current.account_id], each.value.allowed_accounts)
  tags                = local.common_tags
}


# Outputs
output "vpc" {
  description = "VPC details"
  value = {
    id              = module.vpc.vpc_id
    public_subnets  = module.vpc.public_subnet_ids
    private_subnets = module.vpc.private_subnet_ids
  }
}

output "artifacts_bucket" {
  description = "Details of the artifacts S3 bucket"
  value = {
    name = module.artifacts_bucket.bucket_name
    arn  = module.artifacts_bucket.bucket_arn
  }
}

output "ecr_repositories" {
  description = "Details of created ECR repositories"
  value = {
    for name, repo in module.ecr_repositories : name => {
      url = repo.repository_url
      arn = repo.repository_arn
    }
  }
}


