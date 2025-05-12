#  Prediction using trained model

from ultralytics import YOLO

# Load a pretrained YOLOv8n model
model = YOLO('runs/detect/train11/weights/best.pt')

# Run inference 
model.predict('test_images', save=True, imgsz=640, conf=0.2)