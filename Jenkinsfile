pipeline {
    agent any

    environment {
        TEST_SERVER_ADDRESS = '10.30.14.55'
        TOKEN = '1750472607:AAHMKAmTUufWqkYMdDKxlcDhnbugVVmDBDM'
        CHAT_ID = '-439007388'
    }

    stages {
        stage('start CI') {
            steps {
                echo 'start...'
            }
        }

        stage('SCM Checkout of Reporting Service'){
              steps {
                  script{
                    def scmVars = checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'refs/remotes/origin/dev']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [],
                        submoduleCfg: [],
                        userRemoteConfigs: [[credentialsId: 'fbcc7781-9f80-44d9-8be3-c36f630c8b9c', url: 'git@gitlab.ctechnology.kg:campusdemirbank/campus-photo-service.git']]
                    ])
                    result = sh (script: 'git log --stat -1',returnStdout: true)
                    sh """
                           curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d text="${result}"
                       """
                  }
              }
            }

        stage('Test') {
            steps {
                echo 'testing...'
            }
        }

        stage('Deploy') {
            steps {
                sh """
                   eval \$(ssh-agent -s)
                   echo "\$TEST_RSA_KEY" | tr -d '\r' | ssh-add ~/.ssh/id_rsa
                   mkdir -p ~/.ssh
                   chmod 700 ~/.ssh/
                   ssh-keyscan -H "\$TEST_SERVER_ADDRESS" >> ~/.ssh/known_hosts
                   chmod 644 ~/.ssh/known_hosts
                """
            }
            post {
                success {
                    echo 'Deploy to staging'
                    sh """
                      mvn clean -s /root/.m2/settings.xml --batch-mode --errors --fail-at-end --show-version -DinstallAtEnd=true -DdeployAtEnd=true package
                      scp -i /opt/key target/campus-photo-service-1.0.2.jar root@"\$TEST_SERVER_ADDRESS":/opt/campus-photo-service/campus-photo-service-1.0.2.jar
                      ssh -i /opt/key root@"\$TEST_SERVER_ADDRESS" "cd /opt/campus-photo-service; ./stop.sh; ./startup.sh;"
                    """
                }
            }
        }
    }
}
