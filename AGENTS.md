# 🤖 J2N AI Agent Guide & Context (AGENTS.md)

Welcome, AI Agent! This document is your map to the project's knowledge base located in the `.agents/` directory. By reading this file first, you can understand the project's standards, use existing tools, and **save valuable context tokens** by only loading the files you actually need for your current task.

👉 **Need to find where code lives?** Check out the [Project Structure Guide](.agents/j2n-project-structure.md) to quickly locate microservices, frontend apps, and shared packages without scanning the filesystem.

---

## 📂 Architecture of `.agents/`

The `.agents/` directory is organized into four main categories: `rules/`, `skills/`, `workflows/`, and `tutorials/`.

### 1. 📜 Rules (`.agents/rules/`)
These files contain strict project rules and conventions. You **must** read the relevant rule file before modifying or creating code in that domain.
* **`j2n-frontend-rules.md`**: Next.js, React, TypeScript, Tailwind CSS v4, Mantine, React Query, State Management, and i18n rules. *(Read this for any frontend task)*
* **`j2n-backend-rules.md`**: Java, Spring, Microservices, Error Handling, DTO standards, and Logging rules. *(Read this for any backend task)*
* **`j2n-rest-rules.md`**: REST API design, HTTP methods, status codes, and Swagger rules.
* **`j2n-graphql-rules.md`**: GraphQL schema, query, mutation, and error handling conventions.
* **`j2n-grpc-rules.md`**: gRPC service design, protobuf, and communication standards.

### 2. 🧠 Skills (`.agents/skills/`)
These are specialized, high-level AI context modules cloned from `antigravity-awesome-skills`. They provide best practices and architectural patterns. 
**⚠️ Token Saving Tip:** Do NOT read them all. Only use `view_file` on the specific `SKILL.md` if your task explicitly requires deep architectural understanding in that area.

**Available Skill Categories include:**
* **Backend & Architecture:** `java-pro`, `microservices-patterns`, `architecture-patterns`, `clean-code`, `backend-architect`, `software-architecture`, `backend-dev-guidelines`
* **Frontend & UI:** `nextjs-app-router-patterns`, `nextjs-best-practices`, `react-best-practices`, `typescript-pro`, `tailwind-patterns`, `zustand-store-ts`, `tanstack-query-expert`
* **Database & DevOps:** `database-design`, `docker-expert`, `monorepo-architect`, `turborepo-caching`, `database-optimizer`
* **API & Integrations:** `api-design-principles`, `openapi-spec-generation`, `frontend-api-integration-patterns`
*(To use a skill, read `.agents/skills/[skill-name]/SKILL.md`)*

### 3. 🔄 Workflows (`.agents/workflows/`)
Workflows are step-by-step Standard Operating Procedures (SOPs). If your task matches one of these workflows, **you MUST follow it strictly**.
* **`j2n-db-schema-change-workflow.md`**: Procedure for altering database schemas securely and consistently.
* **`j2n-generate-curl-workflow.md`**: Guide to generate Postman collections and cURL commands for a service API.
* **`j2n-rabbitmq-workflow.md`**: Complete guide to implement RabbitMQ messaging (Consumers & Publishers).
* **`use-brain.md`**: Guidelines for brainstorming and planning processes.

### 4. 📚 Tutorials (`.agents/tutorials/`)
Tutorials provide specific examples and explanations of how to apply certain workflows or rules effectively.
* **`generate-curl-usage-tutorial.md`**: Example and usage of generating API curl commands.
* **`project-rules-usage-tutorial.md`**: How to apply project rules properly.
* **`rabbitmq-usage-tutorial.md`**: Examples of RabbitMQ configurations and usage.

---

## 🎯 How to be an Efficient AI in this Project (Token Optimization)

1. **Context Awareness:** Do not load every rule file automatically. Only `view_file` the specific `rules/*.md` relevant to your current domain (e.g., if working on the frontend, only read `j2n-frontend-rules.md`).
2. **Check for Workflows:** Before starting a complex task (like changing a DB schema or adding RabbitMQ), always check if a workflow exists in `.agents/workflows/` and load it.
3. **Use Skills on Demand:** If you are asked to design a new architecture or implement a complex new technology, read the corresponding `.agents/skills/[skill]/SKILL.md`. If you are just fixing a bug or making a minor change, skip the skills to save tokens.

## 🚨 Core Project Mandates
To save tokens, here are the absolute non-negotiable rules you must follow for every task:
1. **English First:** ALL source code, variables, functions, classes, comments, and Swagger/OpenAPI descriptions **MUST** be in English. The only exception is translation values in `common.json`, but their keys must still be English.
2. **Strict `snake_case` for APIs:** All data exchanged between Client, BFF, and Microservices (REST/BFF APIs) MUST use `snake_case` for keys (e.g., `user_name`, `role_id`). DTOs and TypeScript interfaces must reflect this exactly.
3. **Clean Code & Unit Tests:** 
   - **No Trash Code:** Never leave commented-out code, debug statements, or old unused logic in your final output.
   - **Decompose:** Break down long functions into smaller, single-responsibility helpers.
   - **Unit Tests:** Whenever you modify or add new business logic (Backend or Frontend), you **MUST** update or write corresponding unit tests to maintain coverage.
