# Deployment

AeroMaint is deployed by the GitHub Actions pipeline in `.github/workflows/ci-cd.yml`.

## Pipeline

| Job | When | What it does |
|---|---|---|
| Build backend | every push and pull request | `mvn -B package` with Java 17 |
| Build frontend | every push and pull request | `npm ci` + `ng build --configuration production` |
| Build and push Docker images | push to `main`, after both builds pass | builds `backend/Dockerfile` and `frontend/Dockerfile`, pushes to GitHub Container Registry |
| Deploy to server | after the images, **only if the `DEPLOY_HOST` secret exists** | copies the compose files over SSH, pulls the new images, restarts |

Images:

- `ghcr.io/<owner>/aeromaint-backend:latest` and `:<commit-sha>`
- `ghcr.io/<owner>/aeromaint-frontend:latest` and `:<commit-sha>`

The commit-SHA tag lets you go back to any previous version.

## Turning on deployment (one-time setup)

You need a Linux server (a small VPS or a free cloud VM) with Docker and the Docker Compose plugin installed.

### 1. On the server

```bash
mkdir -p ~/aeromaint
nano ~/aeromaint/.env      # same variables as .env.example, with real production values
```

The `.env` file only ever lives on the server. Use new, strong values for `DB_PASSWORD`,
`MYSQL_ROOT_PASSWORD` and `JWT_SECRET`, not the ones from your laptop.

### 2. An SSH key for GitHub Actions

On your computer:

```bash
ssh-keygen -t ed25519 -f aeromaint_deploy -C "github-actions-deploy"
```

Add the content of `aeromaint_deploy.pub` to `~/.ssh/authorized_keys` on the server.

### 3. GitHub secrets

In the repository: **Settings → Secrets and variables → Actions → New repository secret**

| Secret | Value |
|---|---|
| `DEPLOY_HOST` | server IP address or domain name |
| `DEPLOY_USER` | the Linux user on the server |
| `DEPLOY_SSH_KEY` | the full content of the private key file `aeromaint_deploy` |

The next push to `main` deploys automatically. The app is then on `http://<server>/`.

## Rollback

On the server, start the images of an earlier commit:

```bash
cd ~/aeromaint
export IMAGE_PREFIX=ghcr.io/<owner-lowercase>
export IMAGE_TAG=<older-commit-sha>
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

Database migrations (Flyway) only move forward, so a rollback must not go back past a migration
the older code does not know about.
