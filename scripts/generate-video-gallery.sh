#!/bin/bash

# Скрипт для создания HTML галереи видео тестов
# Использование: ./generate-video-gallery.sh <run_number> <videos_directory>

set -e

RUN_NUMBER="$1"
VIDEOS_DIR="$2"

if [ -z "$RUN_NUMBER" ] || [ -z "$VIDEOS_DIR" ]; then
    echo "Usage: $0 <run_number> <videos_directory>"
    echo "Example: $0 209 allure-history/209/videos"
    exit 1
fi

if [ ! -d "$VIDEOS_DIR" ]; then
    echo "Video directory does not exist: $VIDEOS_DIR"
    exit 1
fi

echo "Generating video gallery for run #$RUN_NUMBER in $VIDEOS_DIR..."

# Создаём HTML страницу
cat > "$VIDEOS_DIR/index.html" << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Videos - Run #RUN_NUMBER_PLACEHOLDER</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 20px;
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .video-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
        }
        .video-item {
            border: 1px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            background: white;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .video-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.15);
        }
        video {
            width: 100%;
            height: 200px;
            object-fit: cover;
        }
        .video-info {
            padding: 15px;
        }
        .video-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
            word-break: break-all;
            font-size: 14px;
        }
        .video-size {
            color: #666;
            font-size: 12px;
        }
        .no-videos {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 40px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
            font-weight: 500;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="../" class="back-link">← Back to Test Allure Report</a>
        <h1>🎥 Test Videos - Run #RUN_NUMBER_PLACEHOLDER</h1>
        <div class="video-grid" id="videoGrid">
            <!-- Videos will be populated by JavaScript -->
        </div>
    </div>
    
    <script>
        // Get list of video files
        const videoFiles = [VIDEO_FILES_PLACEHOLDER];
        
        const videoGrid = document.getElementById('videoGrid');
        
        if (videoFiles.length === 0) {
            videoGrid.innerHTML = '<div class="no-videos">No test videos available for this run.</div>';
        } else {
            videoFiles.forEach(file => {
                const videoItem = document.createElement('div');
                videoItem.className = 'video-item';
                
                videoItem.innerHTML = `
                    <video controls preload="metadata">
                        <source src="${file}" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                    <div class="video-info">
                        <div class="video-name">${file}</div>
                        <div class="video-size">Click to play</div>
                    </div>
                `;
                
                videoGrid.appendChild(videoItem);
            });
        }
    </script>
</body>
</html>
EOF

# Заменяем плейсхолдеры
sed -i "s/RUN_NUMBER_PLACEHOLDER/$RUN_NUMBER/g" "$VIDEOS_DIR/index.html"

# Получаем список видео файлов
video_files=$(ls "$VIDEOS_DIR"/*.mp4 2>/dev/null | sed "s|$VIDEOS_DIR/||g" | sed 's/^/"/;s/$/"/' | tr '\n' ',' | sed 's/,$//')

if [ -n "$video_files" ]; then
    sed -i "s/VIDEO_FILES_PLACEHOLDER/$video_files/g" "$VIDEOS_DIR/index.html"
    echo "Video gallery generated successfully with $(echo $video_files | tr ',' '\n' | wc -l) videos"
else
    sed -i "s/VIDEO_FILES_PLACEHOLDER/[]/g" "$VIDEOS_DIR/index.html"
    echo "Video gallery generated successfully (no videos found)"
fi

echo "Gallery available at: https://01yura.github.io/nbank-for-docker/$RUN_NUMBER/videos/"
