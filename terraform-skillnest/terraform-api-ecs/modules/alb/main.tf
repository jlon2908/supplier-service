# Data source to reference the existing Security Group for ALB
data "aws_security_group" "alb" {
  name   = "arka-alb-sg"
  vpc_id = var.vpc_id
}

# Application Load Balancer
resource "aws_lb" "main" {
  name               = "${var.name_prefix}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [data.aws_security_group.alb.id]
  subnets            = var.public_subnets

  enable_deletion_protection = false
  idle_timeout               = 60

  tags = var.tags
}

# Data source to reference the existing Target Group
data "aws_lb_target_group" "existing" {
  arn = var.target_group_arn
}

# Listener
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = data.aws_lb_target_group.existing.arn
  }
}

# Listener Rule for supplier-service paths
resource "aws_lb_listener_rule" "supplier_service" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = data.aws_lb_target_group.existing.arn
  }

  condition {
    path_pattern {
      values = [
        "/api/suppliers/*",
        "/api/supplier-orders/*",
        "/api/receipts/*"
      ]
    }
  }
}
