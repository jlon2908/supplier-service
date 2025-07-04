resource "aws_db_subnet_group" "default" {
  name       = "arka-db-subnet-group"
  subnet_ids = var.private_subnets

  tags = {
    Name = "ArkaDBSubnetGroup"
  }
}

resource "aws_db_instance" "rds_instance" {
  identifier        = var.db_identifier
  engine            = "postgres"
  instance_class    = var.instance_class
  username          = var.db_username
  password          = var.db_password
  db_name           = var.db_name
  allocated_storage = var.allocated_storage
  storage_type      = "gp2"

  db_subnet_group_name   = aws_db_subnet_group.default.name

  skip_final_snapshot = true

  publicly_accessible = false
  multi_az            = false

  tags = {
    Name = "ArkaPostgres"
  }
}
