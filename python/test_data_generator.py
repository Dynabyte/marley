import face_recognition
import json
import os

json_file_name = 'encoding_data.json'
test_folder = 'test_images'

def generate_test_data():
    faces = []
    for root, dirs, files in os.walk(test_folder, topdown=False):
        for dir in dirs:
            print(os.path.join(root, dir))
            encodings = []
            for filename in os.listdir(test_folder + '/' + dir):
                image = face_recognition.load_image_file(test_folder + '/' + dir + '/' + filename)
                encoding = face_recognition \
                    .face_encodings(image)[0]
                encodings.append(encoding.tolist())
                print('encoded ' + filename)
        face = {"_id": "1",
            "encodings": encodings}
        faces.append(face)
    
    
    with open(json_file_name, 'w') as outfile:
        json.dump([face], outfile)
    
    print('data generated')

generate_test_data()