# 

## Model
www.msaez.io/#/204953361/storming/0625

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- author
- writing
- point
- subscriber
- platform
- ai


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- author
```
 http :8088/authors id="id"email="email"authorName="authorName"introduction="introduction"feturedWorks="feturedWorks"isApprove="isApprove"
```
- writing
```
 http :8088/manuscripts id="id"title="title"content="content"Status = "WRITING"AuthorId := '{"id": 0}'
```
- point
```
 http :8088/points id="id"point="point"isSubscribe="isSubscribe"
```
- subscriber
```
 http :8088/users id="id"email="email"userName="userName"isPurchase="isPurchase"mseeage="mseeage"
 http :8088/subscriptions id="id"isSubscription="isSubscription"rentalstart="rentalstart"rentalend="rentalend"webUrl="webURL"
```
- platform
```
 http :8088/books id="id"bookName="bookName"category="category"isBestSeller="isBestSeller"authorName="authorName"viewCount="viewCount"point="point"cost="cost"
```
- ai
```
 http :8088/publishings id="id"image="image"summaryContent="summaryContent"bookName="bookName"pdfPath="pdfPath"authorId="authorId"webUrl="webURL"category="category"cost="cost"
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```
