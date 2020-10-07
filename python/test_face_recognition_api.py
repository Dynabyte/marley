import face_recognition
import face_recognition_api
import json
import os

json_file_name = 'encoding_data.json'

def test_predict():
    image = face_recognition.load_image_file('test_images/person1.jpg')
    encoding = face_recognition \
        .face_encodings(image)[0]
    with open(json_file_name) as json_file:
        faces = json.load(json_file)
    
    assert face_recognition_api.predict(encoding, faces) == 'face1'

