# Terraform Pre-Infrastructure

Este proyecto contiene la infraestructura base necesaria para otros proyectos, como buckets S3 y repositorios ECR.

## Variables Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `environment` | Entorno de despliegue | `"dev"`, `"qa"`, `"prod"` |
| `project` | Nombre del proyecto | `"skillnest"` |
| `artifacts_bucket_name` | Nombre del bucket S3 para artefactos | `"skillnest-dev-artifacts"` |
| `ecr_repositories` | Lista de repositorios ECR a crear | ```[{name = "message-processor"}]``` |

## Configuración del State

1. Crear el bucket para el state:
```bash
aws s3 mb s3://skillnest-terraform-states
```

2. Crear el archivo de backend para dev:
```bash
# En terraform-preinfra/environments/dev/backend.tfvars
bucket         = "skillnest-terraform-states"
key            = "preinfra/dev/terraform.tfstate"
region         = "us-east-1"
dynamodb_table = "terraform-locks"
encrypt        = true
```

## Despliegue

1. Inicializar Terraform con el backend:
```bash
cd terraform-preinfra
terraform init -backend-config=environments/dev/backend.tfvars
```

2. Seleccionar el workspace:
```bash
terraform workspace new dev
terraform workspace select dev
```

3. Planear y aplicar cambios:
```bash
terraform plan -var-file=environments/dev/terraform.tfvars
terraform apply -var-file=environments/dev/terraform.tfvars
```
