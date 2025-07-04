# Skillnest Terraform Project

Este proyecto contiene la infraestructura como código (IaC) para el procesamiento de mensajes usando AWS Lambda y SQS. El proyecto está organizado en múltiples capas para facilitar el manejo de la infraestructura.

## Estructura del Proyecto

```
.
├── init-terraform-backend.sh     # Script para inicializar el backend de Terraform
├── lambda/                      # Código fuente de la Lambda (TypeScript)
├── terraform-lambda/            # Infraestructura de la Lambda
└── terraform-preinfra/         # Infraestructura base compartida
```

## Componentes Principales

### 1. Backend de Terraform (init-terraform-backend.sh)

Este script inicializa tres componentes críticos para el manejo de estados en Terraform:

```bash
# Bucket S3 para estados
aws s3 mb s3://skillnest-terraform-states

# Tabla DynamoDB para bloqueos
aws dynamodb create-table --table-name terraform-locks ...

# Versionamiento del bucket
aws s3api put-bucket-versioning ...
```

#### ¿Por qué necesitamos esto?

- **Bucket S3 (States)**: 
  - Almacena el estado de la infraestructura
  - Permite trabajo colaborativo
  - Mantiene historial de cambios

- **DynamoDB (Locks)**:
  - Previene modificaciones simultáneas
  - Evita conflictos entre desarrolladores
  - Asegura consistencia del estado

- **Versionamiento S3**:
  - Permite recuperar estados anteriores
  - Facilita rollbacks
  - Mantiene historial completo

### 2. Pre-Infraestructura (terraform-preinfra/)

Contiene recursos compartidos que otros proyectos necesitarán:
- Buckets S3 para artefactos
- Repositorios ECR para imágenes Docker
- Políticas y roles IAM base

### 3. Infraestructura Lambda (terraform-lambda/)

Despliega la función Lambda y sus componentes:
- Función Lambda
- Cola SQS FIFO
- Roles y políticas IAM
- Mapeos de eventos

### 4. Código Lambda (lambda/)

Implementación en TypeScript usando Clean Architecture:
```
lambda/
├── src/
│   ├── domain/         # Reglas de negocio
│   ├── application/    # Casos de uso
│   ├── infrastructure/ # Adaptadores AWS
│   └── interfaces/     # Interfaces externas
```

## Flujo de Despliegue

1. **Inicializar Backend**:
```bash
# Dar permisos de ejecución
chmod +x init-terraform-backend.sh

# Ejecutar script
./init-terraform-backend.sh
```

2. **Desplegar Pre-Infraestructura**:
```bash
cd terraform-preinfra

# Inicializar Terraform
terraform init -backend-config=environments/dev/backend.tfvars

# Crear y seleccionar workspace
terraform workspace new dev
terraform workspace select dev

# Desplegar
terraform plan -var-file=environments/dev/terraform.tfvars
terraform apply -var-file=environments/dev/terraform.tfvars
```

3. **Preparar y Desplegar Lambda**:
```bash
# Compilar código TypeScript
cd lambda
npm install
npm run build

# Desplegar infraestructura
cd ../terraform-lambda
terraform init -backend-config=environments/dev/backend.tfvars
terraform workspace select dev
terraform apply -var-file=environments/dev/terraform.tfvars
```

## Workspaces de Terraform

El proyecto utiliza workspaces para manejar múltiples entornos:
- `dev`: Desarrollo
- `qa`: Pruebas (cuando se necesite)
- `prod`: Producción

Beneficios:
- Estados separados por entorno
- Prevención de conflictos
- Configuraciones específicas por entorno
- Mismo código, diferentes configuraciones

## Mantenimiento y Operaciones

### Verificar Estado
```bash
terraform workspace show    # Ver workspace actual
terraform workspace list   # Listar workspaces
terraform show            # Ver estado actual
```

### Recuperación ante Fallos
```bash
# Ver versiones anteriores del state
aws s3 ls s3://skillnest-terraform-states

# Recuperar version específica
aws s3api get-object --bucket skillnest-terraform-states \
                    --key preinfra/dev/terraform.tfstate \
                    --version-id <VERSION_ID> \
                    recovered-state.tfstate
```

## Seguridad

- Los states están encriptados en S3
- Acceso controlado mediante roles IAM
- Bloqueos previenen modificaciones simultáneas
- Versionamiento permite auditoría y recuperación

## Contribución

1. Crear nuevo workspace para features:
```bash
terraform workspace new feature-name
```

2. Desarrollar y probar cambios

3. Merge a dev para pruebas integradas

4. Promover a producción mediante pipeline
