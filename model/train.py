from ultralytics import YOLO

if __name__ == '__main__':
    # Khởi tạo model
    model = YOLO("yolov8n.pt") 
    
    # Thiết lập tham số train
    model.train(
        data="C:/Users/Hii/Downloads/License Plate Recognition.v11i.yolov8/data.yaml",
        epochs=20,
        batch=16,
        patience=5,
        imgsz=640,
        device=0  # Sử dụng GPU nếu có
    )

