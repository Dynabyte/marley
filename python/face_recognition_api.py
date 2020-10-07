from collections import namedtuple
import numpy as np
import flask as fl
from flask_restx import Resource, Api, fields
import io
import base64
import pymongo
import face_recognition
from bson.objectid import ObjectId
import json
import time as tm
import threading

app = fl.Flask(__name__)
api = Api(
    app,
    version='0.1',
    title='Face Recognition API',
    description='Detection and prediction of faces using DLIB')
lock = threading.Lock()
faces_collection = []
mongo_client = pymongo \
    .MongoClient("mongodb+srv://marley-db-user:DUElFH0k35peHesM@marleycluster.42wu5.mongodb.net/marley?retryWrites=true&w=majority")

face_comparison = namedtuple(
    "face_comparison",
    ["face_id", "euclidean_distance_mean"])

ns = api.namespace('face-recognition',
                   description='Labeling and prediction')

image = api.model('Image', {
    'image': fields.String(required=True, description='Base64 binary image')
})

prediction = api.model('Prediction', {
    'isFace': fields.Boolean(required=True, description='Indicates if submited image has a face in it'),
    'faceId': fields.String(required=True, description='Id of predicted face')
})

face_id = api.model('FaceId', {
    'faceId': fields.String(required=True, description='Id of labeled face')
})


@ns.route('/label')
class CreateLabel(Resource):
    @ns.doc('create_label')
    @ns.expect(image)
    @ns.marshal_with(face_id)
    def post(self):
        return time_lambda(
            lambda: label_face(
                lambda face_encoding:
                    time_lambda(
                        lambda: db_create_face(face_encoding),
                        db_create_face.__name__),
                request_image()),
            label_face.__name__)


@ns.route('/label/<string:id>')
class UpdateLabel(Resource):
    @ns.doc('update_label')
    @ns.expect(image)
    @ns.marshal_with(face_id)
    def put(self, id):
        return time_lambda(
            lambda: label_face(
                lambda face_encoding:
                    time_lambda(
                        lambda: db_update_face(
                            id,
                            face_encoding),
                        db_update_face.__name__),
                    request_image()),
            label_face.__name__)


@ns.route('/predict')
class Predict(Resource):
    @ns.doc('do_prediction')
    @ns.expect(image)
    @ns.marshal_list_with(prediction)
    def post(self):
        return time_lambda(
            lambda: predict_face(
                request_image()),
            predict_face.__name__)


def request_image():
    return fl \
        .request \
        .get_json()['image']


def label_face(db_save_face, image_base64):
    encoding_input = face_encode(
        to_numpy_array(image_base64))

    if encoding_input is None:
        return {"faceId": None}
    return {"faceId": str(db_save_face(encoding_input))}


def predict_face(image_base64):
    encoding_input = face_encode(
        to_numpy_array(image_base64))
    if encoding_input is None:
        return {"isFace": False, "faceId": None}

    face_id = predict(
        encoding_input,
        db_get_faces)

    if face_id is None:
        return {"isFace": True, "faceId": None}
    return {"isFace": True, "faceId": str(face_id)}


def predict(encoding, faces):
    closest_comparison = \
        time_lambda(
            lambda: closest(
                compare(
                    encoding,
                    faces)),
            "closest_comparison")

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


def face_encode(image):
    face_encodings = face_recognition \
        .face_encodings(image)

    if not face_encodings:
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
