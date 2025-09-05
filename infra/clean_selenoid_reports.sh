#!/usr/bin/env bash
set -euo pipefail

# Базовый каталог с подпапками video и logs
BASE="${1:-/home/yura/nbank-for-docker/selenoid_reports}"
VIDEO_DIR="$(realpath -m "$BASE/video")"
LOGS_DIR="$(realpath -m "$BASE/logs")"
PATH="/usr/sbin:/usr/bin:/sbin:/bin"

# Флаг без подтверждения (--yes / -y) может быть вторым аргументом
AUTO_YES="${2:-}"

err() { echo "ERROR: $*" >&2; }
info(){ echo ">>> $*"; }

# Мини-безопасность: работаем только с подпапками .../video и .../logs
for d in "$VIDEO_DIR" "$LOGS_DIR"; do
  [[ "$d" == *"/video" || "$d" == *"/logs" ]] || {
    err "Подозрительный путь: $d (ожидается оканчивающийся на /video или /logs)"; exit 2; }
done

# Проверяем существование
MISSING=0
[[ -d "$VIDEO_DIR" ]] || { err "Нет папки: $VIDEO_DIR"; MISSING=1; }
[[ -d "$LOGS_DIR"  ]] || { err "Нет папки: $LOGS_DIR";  MISSING=1; }
(( MISSING == 1 )) && { err "Создайте отсутствующие папки и повторите."; exit 1; }

echo "Будет удалено ВСЁ содержимое:"
echo "  - $VIDEO_DIR"
echo "  - $LOGS_DIR"

# Подтверждение
if [[ "$AUTO_YES" != "--yes" && "$AUTO_YES" != "-y" ]]; then
  read -r -p "Продолжить? [y/N] " ans
  [[ "$ans" == "y" || "$ans" == "Y" ]] || { echo "Отмена."; exit 0; }
fi

# Удаляем всё внутри (но не сами каталоги), включая скрытые файлы/папки
for d in "$VIDEO_DIR" "$LOGS_DIR"; do
  info "Чищу: $d"
  find "$d" -mindepth 1 -exec rm -rf -- {} + || true
  cnt=$(ls -A "$d" 2>/dev/null | wc -l || echo 0)
  echo "Осталось элементов в $(basename "$d"): $cnt"
done

echo "Готово."