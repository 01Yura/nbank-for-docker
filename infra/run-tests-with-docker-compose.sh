#!/bin/bash


echo "Поднимаем тестовое окружение..."
./restart_docker.sh


echo "Запускаем тесты..."
cd ..
./run-tests.sh

echo "Останавливаем тестовое окружение..."
cd infra
docker-compose down