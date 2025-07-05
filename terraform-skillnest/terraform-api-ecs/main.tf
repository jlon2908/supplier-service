module "alb" {
  source = "./modules/alb"

  name_prefix                = var.name_prefix
  vpc_id                     = var.vpc_id
  public_subnets             = var.public_subnets
  container_port             = 8082 # Cambiado a 8082 para este microservicio
  tags                       = var.common_tags
  vpc_link_security_group_id = module.apigateway.vpc_link_security_group_id
  target_group_arn           = var.target_group_arn
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
  vpc_link_security_group_id = "sg-0d9dba80261e8ca15"
  log_group_name = "/aws/apigateway/arka"
}

module "ecs" {
  source = "./modules/ecs"

  name_prefix                = "arka-supplier" # Cambiado para evitar conflicto de nombre
  vpc_id                     = var.vpc_id
  private_subnets            = var.private_subnets
  container_port             = 8082 # Cambiado a 8082 para este microservicio
  target_group_arn           = var.target_group_arn # Ahora se pasa como variable
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
    },
    {
      name  = "RABBITMQ_HOST"
      value = var.rabbitmq_host
    },
    {
      name  = "RABBITMQ_USERNAME"
      value = var.rabbitmq_username
    },
    {
      name  = "RABBITMQ_PASSWORD"
      value = var.rabbitmq_password
    }
  ]

  task_execution_role_arn      = var.task_execution_role_arn
  log_group_name               = var.log_group_name
  ecs_service_security_group_id = var.ecs_service_security_group_id
}
