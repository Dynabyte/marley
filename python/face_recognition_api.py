import numpy as np
from flask import Flask
from flask import request
import uuid
import io
import base64
import pymongo
from PIL import Image
import face_recognition
import time

app = Flask(__name__)


@app.route('/label', methods=['POST'])
def label():
    start = time.time()
    db_save(
        request.get_json()['personId'],
        face_encodings(
            load_image(
                    request.get_json()['image'])))
    print(f"Label took: '{round((time.time()-start) * 1000, 0)}' ms")
    return "ok"

@app.route('/predict', methods=['POST'])
def predict():
    start = time.time()
    face_encoding_input = face_encodings(
        load_image(
                request.get_json()['image']))

    if len(face_encoding_input) == 0:
        return '{"isFace":false, "faceId":null}'
    
    min_face_distance_result = min_face_distance(
        euclidean_distance_means(
            face_encoding_input[0],
            db_load()))

    if min_face_distance_result[1] > 0.5:
        return '{"isFace":true, "faceId":null}'

    print(f"Predict took: '{round((time.time()-start) * 1000, 0)}' ms")
    return f'{{"isFace":true, "faceId":{min_face_distance_result[0]}}}'

def min_face_distance(euclidean_distance_means):
    start = time.time()
    min_face_distance = min(
        euclidean_distance_means,
        key=lambda x: x[1])
    print(f"min_face_distance took: '{round((time.time()-start) * 1000, 0)}' ms")
    return min_face_distance

def load_image(imageRequestParam):
    start = time.time()
    image = face_recognition.load_image_file(
        io.BytesIO(
            base64.b64decode(
                imageRequestParam)))
    print(f"load_image took: '{round((time.time()-start) * 1000, 0)}' ms")                
    return image

def face_encodings(image):
    start = time.time()
    face_encodings = face_recognition.face_encodings(
        image)
    print(f"face_encodings took: '{round((time.time()-start) * 1000, 0)}' ms")
    return face_encodings

def euclidean_distance_means(face_encoding_input, face_encoding_references):
    for face_encoding_ref in face_encoding_references:
        print(face_encoding_ref["value"])
        print(np.array(face_encoding_ref["value"]))
        print(face_encoding_input)
        yield (
            face_encoding_ref["person_id"],
            np.mean(
                face_recognition.face_distance(
                    np.array(np.fromiter(face_encoding_ref["value"], float)),
                    face_encoding_input)))

def db_save(person_id, face_encoding):
    start = time.time() 
    pymongo \
        .MongoClient("mongodb+srv://marley-db-user:DUElFH0k35peHesM@marleycluster.42wu5.mongodb.net/marley?retryWrites=true&w=majority") \
        .marley \
        .face_encodings \
        .insert_one({
            "person_id": uuid.UUID(person_id),
            "value": face_encoding[0].tolist() })
    print(f"db_save took: '{round((time.time()-start) * 1000, 0)}' ms")

def db_load():
#    return [(1, [niklas_face_encoding, niklas_face_encoding2])]
    start = time.time() 
    face_encodings = pymongo \
        .MongoClient("mongodb+srv://marley-db-user:DUElFH0k35peHesM@marleycluster.42wu5.mongodb.net/marley?retryWrites=true&w=majority") \
        .marley \
        .face_encodings \
        .find({})
    print(f"db_load took: '{round((time.time()-start) * 1000, 0)}' ms")
    return face_encodings
