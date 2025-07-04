module "rds" {
  source            = "./modules/rds"

  db_identifier     = var.db_identifier
  db_username       = var.db_username
  db_password       = var.db_password
  db_name           = var.db_name
  instance_class    = var.instance_class
  allocated_storage = var.allocated_storage

  vpc_id           = var.vpc_id
  private_subnets  = var.private_subnets
}
