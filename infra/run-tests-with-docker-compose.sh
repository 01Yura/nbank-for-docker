#!/bin/bash


echo "Поднимаем тестовое окружение..."
./start-docker-compose.sh


echo "Запускаем тесты..."
cd ..
./run-tests.sh

echo "Останавливаем тестовое окружение..."
cd infra
docker-compose down