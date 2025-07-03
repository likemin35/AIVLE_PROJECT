#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 KT 걷다가서재 시스템 상태 확인${NC}"
echo "=================================================="

# 서비스 상태 확인 함수
check_service() {
    local service_name=$1
    local port=$2
    local path=${3:-"/actuator/health"}
    
    echo -n "📡 $service_name (Port $port): "
    
    if curl -s --max-time 5 "http://localhost:$port$path" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ 정상${NC}"
        return 0
    else
        echo -e "${RED}❌ 실패${NC}"
        return 1
    fi
}

# API 테스트 함수
test_api() {
    local description=$1
    local url=$2
    local method=${3:-"GET"}
    
    echo -n "🧪 $description: "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s --max-time 10 "$url")
    else
        response=$(curl -s --max-time 10 -X "$method" "$url")
    fi
    
    if [ $? -eq 0 ] && [ -n "$response" ]; then
        echo -e "${GREEN}✅ 정상${NC}"
        return 0
    else
        echo -e "${RED}❌ 실패${NC}"
        return 1
    fi
}

# Docker 서비스 확인
echo -e "\n${YELLOW}🐳 Docker 서비스 상태${NC}"
echo "--------------------------------------------------"

if docker ps | grep -q "my-kafka"; then
    echo -e "Kafka: ${GREEN}✅ 실행중${NC}"
    kafka_status=0
else
    echo -e "Kafka: ${RED}❌ 실행되지 않음${NC}"
    kafka_status=1
fi

if docker ps | grep -q "zookeeper"; then
    echo -e "Zookeeper: ${GREEN}✅ 실행중${NC}"
    zk_status=0
else
    echo -e "Zookeeper: ${RED}❌ 실행되지 않음${NC}"
    zk_status=1
fi

# 백엔드 서비스 상태 확인
echo -e "\n${YELLOW}🔧 백엔드 서비스 상태${NC}"
echo "--------------------------------------------------"

services_status=0

check_service "Point Service" "8084" || services_status=1
check_service "Subscriber Service" "8085" || services_status=1  
check_service "Author Service" "8082" || services_status=1
check_service "Writing Service" "8083" || services_status=1
check_service "Platform Service" "8086" || services_status=1
check_service "Gateway Service" "8088" || services_status=1

# API 기능 테스트
echo -e "\n${YELLOW}🧪 API 기능 테스트${NC}"
echo "--------------------------------------------------"

api_status=0

test_api "도서 목록 조회" "http://localhost:8088/books" || api_status=1
test_api "포인트 조회 (테스트)" "http://localhost:8088/points/search?userId=1&isSubscription=false" || api_status=1
test_api "작가 목록 조회" "http://localhost:8088/authors" || api_status=1
test_api "원고 목록 조회" "http://localhost:8088/manuscripts" || api_status=1

# 프론트엔드 상태 확인
echo -e "\n${YELLOW}🌐 프론트엔드 상태${NC}"
echo "--------------------------------------------------"

frontend_status=0
check_service "React Frontend" "3000" "/" || frontend_status=1

# 전체 결과 요약
echo -e "\n${BLUE}📊 전체 시스템 상태 요약${NC}"
echo "=================================================="

total_issues=0

if [ $kafka_status -ne 0 ] || [ $zk_status -ne 0 ]; then
    echo -e "Docker 인프라: ${RED}❌ 문제 있음${NC}"
    total_issues=$((total_issues + 1))
else
    echo -e "Docker 인프라: ${GREEN}✅ 정상${NC}"
fi

if [ $services_status -ne 0 ]; then
    echo -e "백엔드 서비스: ${RED}❌ 문제 있음${NC}"
    total_issues=$((total_issues + 1))
else
    echo -e "백엔드 서비스: ${GREEN}✅ 정상${NC}"
fi

if [ $api_status -ne 0 ]; then
    echo -e "API 기능: ${YELLOW}⚠️  일부 문제${NC}"
    total_issues=$((total_issues + 1))
else
    echo -e "API 기능: ${GREEN}✅ 정상${NC}"
fi

if [ $frontend_status -ne 0 ]; then
    echo -e "프론트엔드: ${RED}❌ 문제 있음${NC}"
    total_issues=$((total_issues + 1))
else
    echo -e "프론트엔드: ${GREEN}✅ 정상${NC}"
fi

echo ""
if [ $total_issues -eq 0 ]; then
    echo -e "${GREEN}🎉 모든 시스템이 정상적으로 작동중입니다!${NC}"
    echo -e "${GREEN}👉 http://localhost:3000 에서 서비스를 이용하세요${NC}"
else
    echo -e "${RED}⚠️  $total_issues개 영역에서 문제가 발견되었습니다.${NC}"
    echo ""
    echo -e "${YELLOW}🔧 해결 방법:${NC}"
    
    if [ $kafka_status -ne 0 ] || [ $zk_status -ne 0 ]; then
        echo "1. Kafka 인프라: cd infra && docker-compose up -d"
    fi
    
    if [ $services_status -ne 0 ]; then
        echo "2. 백엔드 서비스: 각 서비스 디렉토리에서 mvn spring-boot:run"
    fi
    
    if [ $frontend_status -ne 0 ]; then
        echo "3. 프론트엔드: npm start"
    fi
fi

echo ""
echo -e "${BLUE}📝 자세한 로그 확인:${NC}"
echo "- 백엔드 서비스 로그: 각 서비스 실행 터미널 확인"
echo "- Kafka 로그: docker logs my-kafka"
echo "- Gateway 로그: http://localhost:8088/actuator/logfile"

exit $total_issues