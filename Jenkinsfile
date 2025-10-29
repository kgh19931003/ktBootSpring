pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Deploy with Git Pull') {
            steps {
                script {
                    echo "🚀 원격 서버에서 git pull 실행 중..."

                    sh """
                        ssh -i '${env.SSH_KEY_PATH}' -o StrictHostKeyChecking=no ${env.REMOTE_USER}@${env.REMOTE_SERVER} '
                            cd ${env.GODTECH_API_REMOTE_PATH} && \
                            git reset --hard && \
                            git pull origin main && \
                            echo "✅ git pull 완료"
                        '
                    """
                }
            }
        }

        stage('Run Post-Deployment Tasks') {
            steps {
                script {
                    echo "⚙️ 배포 후 gradle build 및 서비스 재시작 실행 중..."

                    sh """
                        ssh -i '${env.SSH_KEY_PATH}' -o StrictHostKeyChecking=no ${env.REMOTE_USER}@${env.REMOTE_SERVER} '
                            cd ${env.GODTECH_API_REMOTE_PATH} && \

                            # 5️⃣ 초기 도커 시스템 정리
                            docker system prune -a --volumes --force

                            # 컨테이너 존재 시 중지 및 삭제
                            if docker ps -a --format "{{.Names}}" | grep -q "^godtech-api\\\$"; then
                                docker stop godtech-api && docker rm godtech-api
                            fi && \
                            # 이미지 존재 시 삭제
                            if docker images --format "{{.Repository}}" | grep -q "^html_godtech-api\\\$"; then
                                docker rmi -f html_godtech-api
                            fi && \
                            ./gradlew clean build && \
                            cd ${env.DOCKER_COMPOSE_PATH} && \
                            docker compose build godtech-api && \
                            docker compose up -d godtech-api && \

                            # 5️⃣ 도커 시스템 정리
                            docker system prune -a --volumes --force

                            echo "✅ 빌드 및 서비스 재시작 완료"
                        '
                    """
                }
            }
        }



    }

    post {
        success {
            echo "✅ Git pull 기반 배포 완료"
        }
        failure {
            echo "❌ 배포 실패. 로그를 확인하세요."
        }
    }
}
