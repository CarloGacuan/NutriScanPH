Track your calories easily on Android using real-time object detection.
![image](https://github.com/CarloGacuan/NutriScanPH/assets/124227436/ad724c87-111d-4aec-9397-c0e3e3af294e)

**Overview**

This demo app allows users to quickly obtain calorie information and know what is the common Filipino foods using their camera. The object detection model utilizes a [[MobileNetV2 SSD]](https://github.com/tensorflow/models/tree/master/research/object_detection) architecture that was trained using transfer learning on 10 common Filipino foods classes from the [Yolov5](https://github.com/ultralytics/yolov5) and [Open Images v4](https://storage.googleapis.com/openimages/web/factsfigures_v4.html) dataset.

**Usage**

To set up the pre-trained demo in Android Studio, opt for "Open an existing Android Studio project," find the Food.AI/Food.AI directory, connect a device, and then click 'run' to start the execution.

**Model Training**

• Create a directory in Google Drive called food_detection.

• Add the [training dataset](https://drive.google.com/drive/u/4/folders/1IxrGgg_7ublLS8AtIuFwzLBqpnIPdnA1) and [label_map.pbtxt](https://github.com/CarloGacuan/NutriScanPH/blob/master/app/src/main/assets/food_labelmap.txt) to food_detection.

• Open (FoodAI_train.ipynb) and follow the notebook instructions.

• To use the newly trained model, download (food_detect.tflite) from (model_checkpoints/tflite_model/) and move it to the assets folder in Android Studio. It should replace the existing pretrained model.


Custom food classes

Preparing the data

• Create a directory in Google Drive called (food_detection).

• Use [OIDv4 ToolKit](https://github.com/EscVM/OIDv4_ToolKit) to download images and bounding box annotations for the desired classes.

• Change the classes in (OIDv4_ToolKit/classes.txt) accordingly. Then, zip the (OIDv4_ToolKit folder) and upload it to (food_detection).

• Modify label_map.pbtxt to match the custom classes and upload it tofood_detection.
