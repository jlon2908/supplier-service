module "alb" {
  source = "./modules/alb"

  name_prefix                = var.name_prefix
  vpc_id                     = var.vpc_id
  public_subnets             = var.public_subnets
  container_port             = 8080 # Ajusta si tu app escucha en otro puerto
  tags                       = var.common_tags
  vpc_link_security_group_id = module.apigateway.vpc_link_security_group_id
}

module "apigateway" {
  source = "./modules/apigateway"

  name_prefix     = var.name_prefix
  vpc_id          = var.vpc_id
  private_subnets = var.private_subnets
  alb_listener_arn = module.alb.listener_arn
  alb_dns_name    = module.alb.load_balancer_dns
  tags            = var.common_tags
  allowed_origins = ["*"] # Cambia esto si quieres restringir CORS
}

module "ecs" {
  source = "./modules/ecs"

  name_prefix                = var.name_prefix
  vpc_id                     = var.vpc_id
  private_subnets            = var.private_subnets
  container_port             = 8080
  target_group_arn           = module.alb.target_group_arn
  vpc_link_security_group_id = module.apigateway.vpc_link_security_group_id
  alb_security_group_id      = module.alb.security_group_id
  tags                       = var.common_tags

  container_image            = var.container_image
  aws_region                 = var.aws_region

  container_environment = [
    {
      name  = "DB_R2DBC_URL"
      value = var.db_r2dbc_url
    },
    {
      name  = "DB_JDBC_URL"
      value = var.db_jdbc_url
    },
    {
      name  = "DB_USERNAME"
      value = var.db_username
    },
    {
      name  = "DB_PASSWORD"
      value = var.db_password
    },
    {
      name  = "JWT_SECRET"
      value = var.jwt_secret
    }
  ]
}
