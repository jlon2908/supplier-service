# Referencia al ALB existente por nombre
# Cambia "arka-alb" por el nombre real si es diferente

data "aws_lb" "main" {
  name = "arka-alb"
}

# Data source to reference the existing Security Group for ALB
data "aws_security_group" "alb" {
  name   = "arka-alb-sg"
  vpc_id = var.vpc_id
}

# Data source to reference the existing Target Group
data "aws_lb_target_group" "existing" {
  arn = var.target_group_arn
}

# Listener (referencia al existente, no lo crea)
data "aws_lb_listener" "http" {
  load_balancer_arn = data.aws_lb.main.arn
  port              = 80
}

# Listener Rule for supplier-service paths
resource "aws_lb_listener_rule" "supplier_service" {
  listener_arn = data.aws_lb_listener.http.arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = data.aws_lb_target_group.existing.arn
  }

  condition {
    path_pattern {
      values = [
        "/api/suppliers",
        "/api/suppliers/*",
        "/api/supplier-orders",
        "/api/supplier-orders/*",
        "/api/receipts",
        "/api/receipts/*"
      ]
    }
  }
}
