from collections import namedtuple
import numpy as np
from flask import Flask
from flask import request
import uuid
import io
import base64
import pymongo
from PIL import Image
import face_recognition
from bson.objectid import ObjectId
import time

face_comparison = namedtuple(
    "face_comparison",
    ["face_id", "euclidean_distance_mean"])

app = Flask(__name__)

mongo_client = pymongo \
    .MongoClient("mongodb+srv://marley-db-user:DUElFH0k35peHesM@marleycluster.42wu5.mongodb.net/marley?retryWrites=true&w=majority")


@app.route('/label', methods=['POST', 'PUT'])
def label_endpoint():
    start = time.time()
    if request.method == 'POST':
        _id = create_face(
            face_encode(
                to_numpy_array(
                    request.get_json()['image'])))
    if request.method == 'PUT':
        _id = update_face(
            request.get_json()['faceId'],
            face_encode(
                to_numpy_array(
                    request.get_json()['image'])))
    print(f"Label took: '{round((time.time()-start) * 1000, 0)}' ms")
    return str(_id)


@app.route('/predict', methods=['POST'])
def predict_endpoint():
    start = time.time()

    encoding_input = face_encode(
        to_numpy_array(
            request.get_json()['image']))
    if encoding_input is None:
        return '{"isFace":false, "faceId":null}'

    face_id = predict(
        encoding_input,
        get_faces())
    if face_id is None:
        return '{"isFace":true, "faceId":null}'

    print(f"Predict took: '{round((time.time()-start) * 1000, 0)}' ms")
    return f'{{"isFace":true, "faceId":{face_id}}}'


def predict(encoding, faces):
    closest_comparison = closest(
        compare(
            encoding,
            faces))
    if closest_comparison.euclidean_distance_mean > 0.5:
        return None
    return closest_comparison.face_id


def closest(comparisons):
    start = time.time()
    min_face_distance = min(
        comparisons,
        key=lambda comparison: comparison.euclidean_distance_mean)
    print(
        f"closest took: '{round((time.time()-start) * 1000, 0)}' ms")
    return min_face_distance


def to_numpy_array(image_base64):
    start = time.time()
    image = face_recognition.load_image_file(
        io.BytesIO(
            base64.b64decode(
                image_base64)))
    print(f"to_numpy_array took: '{round((time.time()-start) * 1000, 0)}' ms")
    return image


def face_encode(image):
    start = time.time()
    face_encodings = face_recognition \
        .face_encodings(image)
    print(f"face_encode took: '{round((time.time()-start) * 1000, 0)}' ms")
    if len(face_encodings) == 0:
        return None
    return face_encodings[0]


def compare(encoding, faces):
    for face in faces:
        yield face_comparison(
            face_id=face["_id"],
            euclidean_distance_mean=np.mean(
                face_recognition.face_distance(
                    np.array(face["encodings"]),
                    encoding)))


def create_face(encoding):
    start = time.time()
    _id = faces_db() \
        .insert_one({
            "encodings": [encoding.tolist()]}) \
        .inserted_id
    print(
        f"create_face for record '{_id}' took: '{round((time.time()-start) * 1000, 0)}' ms")
    return _id


def update_face(_id, encoding):
    start = time.time()
    faces_db() \
        .update(
            {'_id': ObjectId(_id)},
            {'$push': {'encodings': encoding.tolist()}})
    print(
        f"update_face for record '{_id}' took: '{round((time.time()-start) * 1000, 0)}' ms")
    return _id


def get_faces():
    start = time.time()
    faces = faces_db() \
        .find({})
    print(f"get_faces took: '{round((time.time()-start) * 1000, 0)}' ms")
    return faces


def faces_db():
    return mongo_client \
        .marley \
        .faces