# =========================
# Stage 1: Builder
# =========================
FROM node:20-alpine AS builder

# Thư mục làm việc chung
WORKDIR /app

# Copy các file config root của Turbo
COPY package.json yarn.lock turbo.json ./
COPY .env.production ./

# Copy toàn bộ packages và ứng dụng about
COPY packages ./packages
COPY apps/web/about ./apps/web/about

# Copy .env.production vào thư mục app about trước build
COPY .env.production ./apps/web/about/

# Cài đặt tất cả dependencies (bao gồm dev)
RUN yarn install --frozen-lockfile

# Build ứng dụng about
WORKDIR /app/apps/web/about
RUN yarn build

# =========================
# Stage 2: Runner (Production)
# =========================
FROM node:20-alpine AS runner
WORKDIR /app

# Set environment cho production
ENV NODE_ENV=production
ENV PORT=3100

# Copy build output và public folder
COPY --from=builder /app/apps/web/about/.next ./.next
COPY --from=builder /app/apps/web/about/public ./public
COPY --from=builder /app/apps/web/about/package.json ./
COPY --from=builder /app/apps/web/about/yarn.lock ./

# (Tùy chọn) copy .env.production nếu muốn
# COPY --from=builder /app/.env.production ./

# Cài đặt production dependencies
RUN yarn install --production --frozen-lockfile

# Expose port 3100
EXPOSE 3100

# CMD chạy FE production
CMD ["yarn", "start"]
