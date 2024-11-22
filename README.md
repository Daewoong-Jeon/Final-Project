# 도서 대여 서비스

- 해당 서비스에 가입한 회원들의 도서대여를 관리하는 서비스입니다.

## 아키텍처

<img width="709" alt="스크린샷 2024-11-22 오전 3 35 12" src="https://github.com/user-attachments/assets/f3ce2692-0d2c-4dea-99bf-7dc55e5fc09a">

## 시나리오

1. 도서관 사서가 회원카드를 스캔한다.
2. 도서관 사서가 도서의 바코드를 스캔한다.
3. 도서의 바코드를 스캔하면 해당 도서는 대여처리된다.
4. 도서가 대여처리되면 대여한 회원의 대여포인트를 차감한다.
5. 회원카드를 스캔하고, 해당 회원이 대여한 도서의 바코드를 스캔하면 반납처리된다.
6. 대여한 도서가 반납처리되면 반납한 회원의 대여포인트가 적립된다.
7. 반납한 도서가 반납기한을 초과했을경우 반납한 회원의 대여포인트는 유지된다.

## 이벤트스토밍
- 효율적인 도서 대여/반납 프로세스를 위해 4개 서브도메인 분리 (rental/book/member/view)

![스크린샷 2024-11-22 오전 4 04 19](https://github.com/user-attachments/assets/b10dc793-9af8-4715-870b-3ae679272abe)

## 서비스상세기능

### 분산트랜잭션 - Saga
- 분리된 마이크로서비스간에 이벤트를 주고 받는 패턴을 구현하였습니다.
#### Saga패턴 구현을 위해 azure aks상에 배포된 서비스
![스크린샷 2024-11-22 오전 4 15 55](https://github.com/user-attachments/assets/479437b8-f928-4d9f-b566-8fcc6e97bb73)

#### 마이크로서비스-kafka 연동 설정
```
spring:
  cloud:
    stream:
      kafka:
        binder:
          brokers: my-kafka:9092
        streams:
          binder:
            configuration:
              default:
                key:
                  serde: org.apache.kafka.common.serialization.Serdes$StringSerde
                value:
                  serde: org.apache.kafka.common.serialization.Serdes$StringSerde
      bindings:
        event-in:
          group: book
          destination: bookrental
          contentType: application/json
        event-out:
          destination: bookrental
          contentType: application/json
```

#### 로직처리 후 이벤트 publish
```
    public void rentBook(RentBookCommand rentBookCommand) {
        Date now = new Date();

        setBookId(rentBookCommand.getBookId());
        setMemberId(rentBookCommand.getMemberId());
        setRentalDate(now);
        setResult("rent success");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7); // 일 계산
        Date requiredReturnDate = new Date(cal.getTimeInMillis());
        setRequiredReturnDate(requiredReturnDate);

        BookRent bookRent = new BookRent(this);
        bookRent.publishAfterCommit();
    }
```

#### 이벤트 Subscription
```
    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookRent'"
    )
    public void wheneverBookRent_UpdateRentalStatus(
        @Payload BookRent bookRent
    ) {
        BookRent event = bookRent;
        System.out.println(
            "\n\n##### listener UpdateRentalStatus : " + bookRent + "\n\n"
        );

        // Sample Logic //
        Book.updateRentalStatus(event);
    }
```

#### 결과
- 요청 : `http {Gateway-IP}/rentals/rentbook memberId=1 bookId=1`
  
1. 대여이력 생성

![스크린샷 2024-11-22 오전 4 25 46](https://github.com/user-attachments/assets/47699ac2-22f8-4286-a02b-81eb278a20ee)

2. view 생성 및 현행화 (도서상태 rental 및 대여회원 현행화)

![스크린샷 2024-11-22 오전 4 25 19](https://github.com/user-attachments/assets/b837abfb-b883-4f6d-bf15-578fc99899c5)

3. 도서상태 및 대여회원 현행화

![스크린샷 2024-11-22 오전 4 26 45](https://github.com/user-attachments/assets/e679279b-177f-4880-84b4-d5952aa04e58)

4. 대여회원 대여포인트 차감

![스크린샷 2024-11-22 오전 4 27 04](https://github.com/user-attachments/assets/a2279b12-d348-4740-a73b-b39d443e7a99)


### 보상처리 - Compensation
- 로직처리 실패 또는 예외발생 시, 이전 처리에 대해 rollback을 수행하기 위한 보상처리 기능을 구현하였습니다.

#### 보상처리를 위한 이벤트 발행 로직 (대여할 도서가 이용가능한 상태가 아닐 경우, 보상처리 Event 발행)
```
    public static void updateRentalStatus(BookRent bookRent) {

        repository().findById(bookRent.getBookId()).ifPresent(book->{
            if ("available".equals(book.getStatus())) {

                book.setStatus("rental");
                book.setMemberId(bookRent.getMemberId());
                repository().save(book);

                RentalStatusUpdated rentalStatusUpdated = new RentalStatusUpdated(book);
                rentalStatusUpdated.publishAfterCommit();

            } else {

                NotAvailableReturned notAvailableReturned = new NotAvailableReturned();
                notAvailableReturned.setId(book.getId());
                notAvailableReturned.setMemberId(bookRent.getMemberId());
                notAvailableReturned.setStatus(book.getStatus());
                notAvailableReturned.publishAfterCommit();

            }

         });

    }
```

#### 결과
- 요청 (2번 회원이 대여한 도서를 1번 회원이 대여시도)
  `http 20.249.70.87/rentals/rentbook memberId=2 bookId=1`
  `http 20.249.70.87/rentals/rentbook memberId=1 bookId=1`

![스크린샷 2024-11-22 오전 4 41 41](https://github.com/user-attachments/assets/66d3476b-e47c-4d5b-8222-12b9f2d19ef9)

### 단일 진입점 - Gateway
- 단일 진입점으로 트래픽을 받아 각 Micro Service에 routing하기 위한 Gateway를 구현하였습니다.
- 소스코드기반이 아닌 메니페스트 파일 기반의 라우팅 rule을 설정하기 위해 Ingress를 활용하였습니다.

#### Ingress.yaml (라우팅룰 설정)
```
apiVersion: networking.k8s.io/v1
kind: "Ingress"
metadata:
  name: "bookrental-ingress"
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    ingressclass.kubernetes.io/is-default-class: "true"
spec:
  ingressClassName: nginx
  rules:
    - host: ""
      http:
        paths:
          - path: /books
            pathType: Prefix
            backend:
              service:
                name: book
                port:
                  number: 8080
          - path: /rentals
            pathType: Prefix
            backend:
              service:
                name: rental
                port:
                  number: 8080
          - path: /members
            pathType: Prefix
            backend:
              service:
                name: member
                port:
                  number: 8080
          - path: /bookLists
            pathType: Prefix
            backend:
              service:
                name: view
                port:
                  number: 8080
```

#### Ingress Controller

![스크린샷 2024-11-22 오전 4 48 45](https://github.com/user-attachments/assets/3ea45a7d-c657-4c55-b5b8-8e969a1b2dd0)

#### 결과

- path에 따른 각 서비스 요청 결과
1. `http 20.249.70.87/bookLists`

![스크린샷 2024-11-22 오전 4 51 40](https://github.com/user-attachments/assets/aadfcb92-90cd-4041-a0bd-41b084f60122)

2. `http 20.249.70.87/rentals`

![스크린샷 2024-11-22 오전 4 52 03](https://github.com/user-attachments/assets/828e421e-dff1-40b0-83fc-106608f9b0c9)


### 분산 데이터 프로젝션 - CQRS
- CUD 작업을 위한 Command 모델과 Read 작업을 위한 Query 모델을 분리한 CQRS 기능을 구현하였습니다.

#### Command와 Query 분리를 위한 View 모델 생성 및 데이터 수집 (발행된 모든 이벤트 메세지 수집)
```
    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookAdded_then_CREATE_1(@Payload BookAdded bookAdded) {
        try {
            if (!bookAdded.validate()) return;

            // view 객체 생성
            BookList bookList = new BookList();
            // view 객체에 이벤트의 Value 를 set 함
            bookList.setBookId(String.valueOf(bookAdded.getId()));
            bookList.setRentalStatus(bookAdded.getStatus());
            bookList.setRentalCost(bookAdded.getCost());
            // view 레파지 토리에 save
            bookListRepository.save(bookList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRentalStatusUpdated_then_UPDATE_1(
        @Payload RentalStatusUpdated rentalStatusUpdated
    ) {
        try {
            if (!rentalStatusUpdated.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                rentalStatusUpdated.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(rentalStatusUpdated.getStatus());
                bookList.setRecentRentalMemberId(
                    rentalStatusUpdated.getMemberId()
                );
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenAvailableStatusUpdated_then_UPDATE_2(
        @Payload AvailableStatusUpdated availableStatusUpdated
    ) {
        try {
            if (!availableStatusUpdated.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                availableStatusUpdated.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(availableStatusUpdated.getStatus());
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRent_then_UPDATE_3(@Payload BookRent bookRent) {
        try {
            if (!bookRent.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookRent.getBookId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRecentRentalDate(bookRent.getRentalDate());
                bookList.setRequiredReturnDate(
                        bookRent.getRequiredReturnDate()
                );
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookReturned_then_UPDATE_4(
            @Payload BookReturned bookReturned
    ) {
        try {
            if (!bookReturned.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookReturned.getBookId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRollbacked_then_UPDATE_5(
            @Payload BookRollbacked bookRollbacked
    ) {
        try {
            if (!bookRollbacked.validate()) return;
            // view 객체 조회
            Optional<BookList> bookListOptional = bookListRepository.findByBookId(
                    bookRollbacked.getId()
            );

            if (bookListOptional.isPresent()) {
                BookList bookList = bookListOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                bookList.setRentalStatus(bookRollbacked.getStatus());
                bookList.setRecentRentalMemberId(null);
                bookList.setRecentRentalDate(null);
                bookList.setRequiredReturnDate(null);
                // view 레파지 토리에 save
                bookListRepository.save(bookList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### view 모델 조회 결과 (rental 및 book Aggregate에서 확인할 수 있는 데이터 모두 확인 가능)

![스크린샷 2024-11-22 오전 5 00 17](https://github.com/user-attachments/assets/bbe66332-7856-4c32-9e34-d8fe5387f4b4)

### 클라우드 배포 - Container 운영
- 빌드 및 배포 자동화(CI/CD)를 위한 Jenkins pipeline를 생성하였습니다.

#### pipeline 생성을 위한 Jenkinsfile
```
pipeline {
    agent any

    environment {
        REGISTRY = 'user13.azurecr.io'
        IMAGE_NAME = 'book'
        AKS_CLUSTER = 'user13-aks'
        RESOURCE_GROUP = 'user13-rsrcgrp'
        AKS_NAMESPACE = 'default'
        AZURE_CREDENTIALS_ID = 'Azure-Cred'
        TENANT_ID = '29d166ad-94ec-45cb-9f65-561c038e1c7a' // Service Principal 등록 후 생성된 ID
    }
 
    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }
        
        stage('Maven Build') {
            steps {
                withMaven(maven: 'Maven') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    image = docker.build("${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}")
                }
            }
        }
        
        stage('Azure Login') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: env.AZURE_CREDENTIALS_ID, usernameVariable: 'AZURE_CLIENT_ID', passwordVariable: 'AZURE_CLIENT_SECRET')]) {
                        sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant ${TENANT_ID}'
                    }
                }
            }
        }
        
        stage('Push to ACR') {
            steps {
                script {
                    sh "az acr login --name ${REGISTRY.split('\\.')[0]}"
                    sh "docker push ${REGISTRY}/${IMAGE_NAME}:v${env.BUILD_NUMBER}"
                }
            }
        }
        
        stage('CleanUp Images') {
            steps {
                sh """
                docker rmi ${REGISTRY}/${IMAGE_NAME}:v$BUILD_NUMBER
                """
            }
        }
        
        stage('Deploy to AKS') {
            steps {
                script {
                    sh "az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${AKS_CLUSTER}"
                    sh """
                    sed 's/latest/v${env.BUILD_ID}/g' kubernetes/deployment.yaml > output.yaml
                    cat output.yaml
                    kubectl apply -f output.yaml
                    kubectl apply -f kubernetes/service.yaml
                    rm output.yaml
                    """
                }
            }
        }
    }
}

```

#### git push 시, 자동 CI/CD 수행
- github repository webhook 추가 및 jenkins 설정

<img width="783" alt="스크린샷 2024-11-22 오후 12 33 06" src="https://github.com/user-attachments/assets/e9b14401-89b3-450a-b025-3522b6c31829">
<br/><br/>

<img width="436" alt="스크린샷 2024-11-22 오후 12 33 38" src="https://github.com/user-attachments/assets/685ec56d-82a4-4fe2-8177-b5f2fc8466b1">

#### jenkins pipeline이 진행되어 배포 완료
![스크린샷 2024-11-22 오전 5 03 51](https://github.com/user-attachments/assets/5d5256d2-3d3c-4a65-b862-9be15dfeb365)
<br/><br/>

![스크린샷 2024-11-22 오전 5 04 22](https://github.com/user-attachments/assets/e4cfd3bd-4638-42f3-af38-b06116a75b08)

### 컨테이너 자동확장 - HPA
- CPU사용률 등 특정 지표 기준을 초과하면 자동 Scaling되는 AutoScaling기능을 구현하였습니다.

#### CPU 사용량 설정
```
spec:
  replicas: 1
  selector:
    matchLabels:
      app: book
  template:
    metadata:
      labels:
        app: book
        sidecar.istio.io/inject: "true" # Pod 레이블 추가
    spec:
      containers:
        - name: book
          image: "user13.azurecr.io/book:latest"
          ports:
            - containerPort: 8080
          resources:  # 추가된 부분
            requests:
              cpu: "200m"
```

#### AutoScaling 기능을 추가하는 HPA(HorizontalPodAutoscaler) 배포

`kubectl autoscale deployment book --cpu-percent=50 --min=1 --max=3`
-> cpu 사용량 50%를 초과할 경우, 최대 3개까지 Pod를 Scale-out

<img width="975" alt="스크린샷 2024-11-22 오전 9 23 09" src="https://github.com/user-attachments/assets/da199277-c0fa-43db-8409-49eb8a0344cb">

#### 결과

- 아래 명령어로 부하 입력 시, 3개까지 pod scaling되는 것 확인
`siege -c20 -t40S -v http://book:8080/books`

<img width="567" alt="스크린샷 2024-11-22 오전 9 27 42" src="https://github.com/user-attachments/assets/e2b2fa06-42df-4084-b74e-fc75c58506c6">

- auto scaling 확인

<img width="674" alt="스크린샷 2024-11-22 오전 9 31 03" src="https://github.com/user-attachments/assets/193b2545-57cd-4be8-a989-80c08f6c0985">
<br/><br/>

<img width="668" alt="스크린샷 2024-11-22 오전 9 26 56" src="https://github.com/user-attachments/assets/b1802901-939f-434e-87aa-fe78656e3b00">

### 컨테이너로부터 환경분리 - ConfigMap
- 환경설정에 관한 내용을 서비스로부터 분리하기 위해 ConfigMap을 작성하였습니다.
- book 도메인의 log레벨은 INFO, 나머지 VIEW/RENTAL/MEMBER 도메인의 log레벨은 DEBUG로 설정하겠습니다.

#### config-map.yaml
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: config-dev
  namespace: default
data:
  VIEW_LOG_LEVEL: DEBUG
  BOOK_LOG_LEVEL: INFO
  RENTAL_LOG_LEVEL: DEBUG
  MEMBER_LOG_LEVEL: DEBUG
```

#### 결과
- book 도메인은 info log, rental 도메인은 debug log를 출력합니다.

<img width="1287" alt="스크린샷 2024-11-22 오전 9 38 27" src="https://github.com/user-attachments/assets/53de9748-4c1a-43b4-a066-99006512706e">
<br/><br/>

<img width="1298" alt="스크린샷 2024-11-22 오전 9 38 59" src="https://github.com/user-attachments/assets/7f9c0a09-31f8-44dd-b69e-187986e86447">

### 클라우드스토리지 활용 - PVC
- NAS 파일 스토리지를 Azure에 생성한 후에 해당 스토리지를 aks pod에서 마운트하여 사용하도록 구성하였습니다.

#### Volume 생성을 위한 azurefile class의 PVC 생성
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: azurefile
spec:
  accessModes:
  - ReadWriteMany
  storageClassName: azurefile
  resources:
    requests:
      storage: 1Gi
```

#### NAS Volume을 마운트하도록 서비스의 deployment.yaml 수정
```
spec:
  replicas: 1
  selector:
    matchLabels:
      app: book
  template:
    metadata:
      labels:
        app: book
        sidecar.istio.io/inject: "true" # Pod 레이블 추가
    spec:
      containers:
        - name: book
          image: "user13.azurecr.io/book:latest"
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          env:
            - name: BOOK_LOG_LEVEL
              valueFrom:
                configMapKeyRef:
                  name: config-dev
                  key: BOOK_LOG_LEVEL
          volumeMounts:  # 추가된 부분
            - mountPath: "/mnt/data"
              name: volume
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: azurefile
```


#### 결과
- book pod에 접속하여 test.txt 파일 생성
- rental pod에 접속하여 test2.txt 파일 생성
- 두 pod에서 생성된 2개 파일 모두 확인

<img width="962" alt="스크린샷 2024-11-22 오전 9 49 20" src="https://github.com/user-attachments/assets/2d5fd7cf-c43c-4402-b920-d0fe579d01b3">
<br/><br/>

<img width="999" alt="스크린샷 2024-11-22 오전 9 49 53" src="https://github.com/user-attachments/assets/e8ed9a2f-1ce1-4489-b3e6-014b42732464">

### 셀프힐링 - Liveness Probe
- 컨테이너 플랫폼이 자동으로 장애를 감지하여 복구할 수 있도록 Liveness Probe를 구성하였습니다.

#### rental deployment.yaml 설정
- pod가 시작되고 120초가 지나면 주기적으로 pod상태 체크
```
spec:
  replicas: 1
  selector:
    matchLabels:
      app: rental
  template:
    metadata:
      labels:
        app: rental
        sidecar.istio.io/inject: "true" # Pod 레이블 추가
    spec:
      containers:
        - name: rental
          image: "user13.azurecr.io/rental:latest"
          ports:
            - containerPort: 8080
          livenessProbe:  # 추가된 부분
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```

#### 정상 실행

<img width="1267" alt="스크린샷 2024-11-22 오전 9 52 49" src="https://github.com/user-attachments/assets/e1c416f6-3a94-44fb-8218-8d22013a622c">

#### 결과
- liveness probe 기능을 테스트하기 위해 `initialDelaySeconds`를 0으로 설정하여 배포하였습니다.
- Container 시작이 완료되기 전에 liveness probe 체크를 진행하므로 Warning이 표시되는 모습입니다.

<img width="1265" alt="스크린샷 2024-11-22 오전 9 54 55" src="https://github.com/user-attachments/assets/ef0510a2-a6f2-4a4e-b6f5-bfb361c4faed">

### 서비스 메쉬 응용 - Mesh
- 쿠버네티스의 기본 서비스 메쉬인 istio를 설치하여 "도서 대여 서비스"에 등록하였습니다.

#### istio 설치 완료

<img width="1380" alt="스크린샷 2024-11-22 오전 10 13 34" src="https://github.com/user-attachments/assets/ecaa243c-2412-4cbf-9562-973779038d01">

#### istio 사이드카 설정
- pod가 생성될 때마다 istio 사이드카 컨테이너가 생성될 수 있도록 각 마이크로서비스의 `deployment.yaml` 에 설정을 추가하였습니다.

```
spec:
  replicas: 1
  selector:
    matchLabels:
      app: rental
  template:
    metadata:
      labels:
        app: rental

        sidecar.istio.io/inject: "true" # Pod 레이블 추가 (추가된 부분)
```

#### 결과
- `deployment.yaml` 설정 후 각 마이크로서비스마다 사이드카 컨테이너가 추가된 모습입니다.
- istio addon중 하나인 kiali/Jaeger를 통해 Service Mesh를 모니터링하고 추적하였습니다.

<img width="633" alt="스크린샷 2024-11-22 오전 10 19 43" src="https://github.com/user-attachments/assets/242593f2-da00-44b8-b07e-6408e7d6a961">

<img width="1470" alt="스크린샷 2024-11-22 오전 10 21 11" src="https://github.com/user-attachments/assets/061b2a89-ba3f-453a-9810-f5994692c6e1">

<img width="1470" alt="스크린샷 2024-11-22 오전 10 24 18" src="https://github.com/user-attachments/assets/16f29d5f-52d4-4afd-a39a-7b9d3e3331d8">

### 통합 모니터링 - Monitoring
- 분산환경의 서비스 성능 및 장애를 추적하기 위해 Monitoring tool인 Grafana를 설치하여 적용하였습니다.

#### Grafana 환경세팅

<img width="1467" alt="스크린샷 2024-11-22 오전 10 41 50" src="https://github.com/user-attachments/assets/158eedc4-f5ae-4d81-8906-ec8ffd670377">

#### book 서비스 부하 입력
- 서비스 성능을 확인하기 위해 서비스에 부하를 입력하였습니다.

<img width="552" alt="스크린샷 2024-11-22 오전 10 39 47" src="https://github.com/user-attachments/assets/17843a2c-ddbc-4e6c-b40a-45d571a046ce">

#### 결과
- Grafana Built-in Dashboard 상에서 서비스 성능을 확인하였습니다.
- Grafana에서 제공하는 prividing dashboard를 통해 서비스 성능을 확인하였습니다.

<img width="1470" alt="스크린샷 2024-11-22 오전 10 41 23" src="https://github.com/user-attachments/assets/53c937de-27b0-4700-a70e-ba35c133da7f">
<br/><br/>

<img width="1470" alt="스크린샷 2024-11-22 오전 10 45 08" src="https://github.com/user-attachments/assets/f23dcbf3-bc2f-4d5d-b20d-8726eb476281">




