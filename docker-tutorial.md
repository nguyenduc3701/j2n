# Docker Tutorial for BFF Service (Backend Service)

## STEP 1: Build Docker Image (Open terminal in backend/bff-srv/)
```bash
docker build --no-cache -t j2n/bff-srv .
```

## STEP 2: Run Container and Open Port (first time)
```bash
docker run --network j2n-network -p 8180:8180 --name bff-srv j2n/bff-srv
```

## STEP 2.5: Run Container on Later Runs
```bash
docker run --network j2n-network -p 8180:8180 j2n/bff-srv
```


# Docker Tutorial for Web About Service (Frontend Service)

## STEP 1: Build Docker Image (Open terminal in frontend/)
```bash
docker build --no-cache -t j2n/web-about-srv -f apps/web/about/Dockerfile .
```

## STEP 2: Run Container and Open Port (first time)
```bash
docker run --network j2n-network -p 3100:3100 --name web-about-srv j2n/web-about-srv 
```

## STEP 2.5: Run Container on Later Runs
```bash
docker run --network j2n-network -p 3100:3100 j2n/web-about-srv 
```


