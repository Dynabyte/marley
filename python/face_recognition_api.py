from collections import namedtuple
import numpy as np
import flask as fl
import io
import base64
import pymongo
import face_recognition
from bson.objectid import ObjectId
import json
import time as tm
import threading

face_comparison = namedtuple(
    "face_comparison",
    ["face_id", "euclidean_distance_mean"])

app = fl.Flask(__name__)
lock = threading.Lock()
faces_collection = []
mongo_client = pymongo \
    .MongoClient("mongodb+srv://marley-db-user:DUElFH0k35peHesM@marleycluster.42wu5.mongodb.net/marley?retryWrites=true&w=majority")


@app.route('/label', methods=['POST', 'PUT'])
def label_endpoint():
    return time(
        inner_label_endpoint)


def inner_label_endpoint():
    encoding_input = face_encode_from_request(fl.request)
    if encoding_input is None:
        return response({"faceId": None})

    if fl.request.method == 'POST':
        _id = time_lambda(
            lambda: db_create_face(encoding_input),
            db_create_face.__name__)
    if fl.request.method == 'PUT':
        _id = time_lambda(
            lambda: db_update_face(
                fl.request.get_json()['faceId'],
                encoding_input),
            db_update_face.__name__)

    return response({"faceId": str(_id)})


@ app.route('/predict', methods=['POST'])
def predict_endpoint():
    return time(
        inner_predict_endpoint)


def inner_predict_endpoint():
    encoding_input = face_encode_from_request(fl.request)
    if encoding_input is None:
        return response({"isFace": False, "faceId": None})

    face_id = predict(
        encoding_input,
        db_get_faces)
    if face_id is None:
        return response({"isFace": True, "faceId": None})

    return response({"isFace": True, "faceId": str(face_id)})


def predict(encoding, faces):
    closest_comparison = closest(
        compare(
            encoding,
            faces))
    if closest_comparison.euclidean_distance_mean > 0.5:
        return None
    return closest_comparison.face_id


def closest(comparisons):
    return min(
        comparisons,
        key=lambda comparison: comparison.euclidean_distance_mean)


def to_numpy_array(image_base64):
    return face_recognition.load_image_file(
        io.BytesIO(
            base64.b64decode(
                image_base64)))


def face_encode_from_request(request):
    return time_lambda(
        lambda: face_encode(
            to_numpy_array(
                request.get_json()['image'])),
        face_encode.__name__)


def face_encode(image):
    face_encodings = face_recognition \
        .face_encodings(image)

    if len(face_encodings) == 0:
        return None

    return face_encodings[0]


def compare(encoding, faces):
    with lock:
        for face in time(faces):
            yield face_comparison(
                face_id=face["_id"],
                euclidean_distance_mean=np.mean(
                    face_recognition.face_distance(
                        np.array(face["encodings"]),
                        encoding)))


def db_create_face(encoding):
    with lock:
        _id = faces_db() \
            .insert_one({
                "encodings": [encoding.tolist()]}) \
            .inserted_id
        global faces_collection
        faces_collection = []
    return _id


def db_update_face(_id, encoding):
    with lock:
        faces_db() \
            .update(
                {'_id': ObjectId(_id)},
                {'$push': {'encodings': encoding.tolist()}})
        global faces_collection
        faces_collection = []
    return _id


def db_get_faces():
    global faces_collection
    if not faces_collection:
        for face in faces_db().find({}):
            faces_collection.append(face)
    return faces_collection


def faces_db():
    return mongo_client \
        .marley \
        .faces


def response(json_data):
    return fl.Response(
        json.dumps(json_data),
        mimetype='application/json')


def time(func):
    start = tm.time()
    result = func()
    print_time(func.__name__, start)
    return result


def time_lambda(func, name):
    start = tm.time()
    result = func()
    print_time(name, start)
    return result


def print_time(name, start):
    print(f"'{name}' took: '{round((tm.time()-start) * 1000, 0)}' ms")
