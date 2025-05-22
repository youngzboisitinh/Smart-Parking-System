import cv2
import numpy as np
import os
from PIL import Image
from ultralytics import YOLO
from vietocr.tool.predictor import Predictor
from vietocr.tool.config import Cfg
from datetime import datetime

# Load model YOLOv8 và VietOCR
model = YOLO('C:/Users/Hii/bienso/runs/detect/train3/weights/best.pt')
config = Cfg.load_config_from_name('vgg_transformer')
config['weights'] = 'C:/Users/Hii/bienso/vgg-transformer.pth'
config['cnn']['pretrained'] = False
config['device'] = 'cuda'
ocr = Predictor(config)

# Tạo thư mục lưu ảnh
os.makedirs('plates', exist_ok=True)

cap = cv2.VideoCapture(0)

while True:
    ret, frame = cap.read()
    if not ret:
        break

    results = model(frame)
    annotated = frame.copy()

    for i, box in enumerate(results[0].boxes.xyxy):
        x1, y1, x2, y2 = map(int, box.cpu().numpy())
        plate_img = frame[y1:y2, x1:x2]

        # Lưu ảnh biển số
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S_%f')
        plate_path = f"plates/plate_{timestamp}.jpg"
        cv2.imwrite(plate_path, plate_img)

        # OCR
        pil_img = Image.open(plate_path).convert('RGB')
        plate_np = np.array(pil_img)
        h, w = plate_np.shape[:2]

        # Nếu chiều cao lớn hơn ~60% chiều ngang, có thể là 2 dòng
        if h > 0.5 * w:
            # Cắt thành 2 nửa dọc
            upper_half = plate_np[0:h//2, :]
            lower_half = plate_np[h//2:, :]

            try:
                upper_text = ocr.predict(Image.fromarray(upper_half).convert('RGB')).strip()
                lower_text = ocr.predict(Image.fromarray(lower_half).convert('RGB')).strip()
                text = upper_text + lower_text
            except Exception as e:
                text = ''
                print("[OCR lỗi 2 dòng]", e)
        else:
            # 1 dòng
            try:
                text = ocr.predict(pil_img).strip()
            except Exception as e:
                text = ''
                print("[OCR lỗi 1 dòng]", e)

        print("[BIỂN SỐ]", text)

        # Vẽ lên ảnh gốc
        cv2.rectangle(annotated, (x1, y1), (x2, y2), (0, 255, 0), 2)
        cv2.putText(annotated, text, (x1, y1 - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.9, (36, 255, 12), 2)

    cv2.imshow('ANPR - Capture & Recognize', annotated)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
