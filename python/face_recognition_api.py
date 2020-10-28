import traceback
from collections import namedtuple
import numpy as np
import flask as fl
from waitress import serve
from flask_restx import Resource, Api, fields
import io
import base64
import pymongo
import face_recognition
from bson.objectid import ObjectId
import json
import time as tm
import threading
import os
from dotenv import load_dotenv
load_dotenv()

app = fl.Flask(__name__)
api = Api(
    app,
    version='0.1',
    title='Face Recognition API',
    description='Detection and prediction of faces using DLIB')
lock = threading.Lock()
faces_collection = []
mongo_client = pymongo \
    .MongoClient(os.getenv('MONGO_CLIENT'))
database_name = os.getenv('MONGO_DB_NAME')

face_comparison = namedtuple(
    "face_comparison",
    ["face_id", "euclidean_distance_minimum"])

ns = api.namespace('face-recognition',
                   description='Labeling and prediction')

image = api.model('Image', {
    'image': fields.String(required=True, description='Base64 binary image')
})

face_id = api.model('FaceId', {
    'faceId': fields.String(required=True, description='The face identifier (MongoDB ObjectID)')
})


class FaceNotFoundException(BaseException):
    pass


class FaceNotDetectedException(BaseException):
    pass


@ns.route('/label')
@ns.response(409, 'Face not detected')
class CreateLabel(Resource):
    @ns.doc('create_label')
    @ns.expect(image)
    @ns.marshal_with(face_id)
    def post(self):
        try:
            return time_lambda(
                lambda: label_face(
                    lambda face_encoding:
                        time_lambda(
                            lambda: db_create_face(face_encoding),
                            db_create_face.__name__),
                    api.payload['image']),
                label_face.__name__), 201
        except FaceNotDetectedException:
            traceback.print_exc()
            fl.abort(409, 'Face not detected')


@ns.route('/label/<string:id>')
@ns.response(404, 'Face not found')
@ns.response(409, 'Face not detected')
@ns.param('id', 'The face identifier (MongoDB ObjectID)')
class UpdateLabel(Resource):
    @ns.doc('update_label')
    @ns.expect(image)
    @ns.marshal_with(face_id)
    def put(self, id):
        try:
            return time_lambda(
                lambda: label_face(
                    lambda face_encoding:
                    time_lambda(
                        lambda: db_update_face(
                            id,
                            face_encoding),
                        db_update_face.__name__),
                    api.payload['image']),
                label_face.__name__)
        except FaceNotDetectedException:
            traceback.print_exc()
            fl.abort(409, 'Face not detected')
        except FaceNotFoundException:
            traceback.print_exc()
            fl.abort(404, 'Face not found')

@ns.route('/delete/<string:id>')
@ns.response(404, 'Face not found')
@ns.param('id', 'The face identifier (MongoDB ObjectID)')
class Delete(Resource):
    @ns.doc('delete_face')
    @ns.marshal_with(face_id)
    def delete(self, id):
        try:
            face_id = time_lambda(
                lambda: db_delete_face(id),
                db_delete_face.__name__)
            if face_id is None:
                raise FaceNotFoundException
            return {"faceId": str(face_id)}
        except FaceNotFoundException:
            traceback.print_exc()
            fl.abort(404, 'FaceId not found')
            
        
        
        

@ns.route('/predict')
@ns.response(409, 'Face not detected')
class Predict(Resource):
    @ns.doc('do_prediction')
    @ns.expect(image)
    @ns.marshal_with(face_id)
    def post(self):
        try:
            return time_lambda(
                lambda: predict_face(
                    api.payload['image']),
                predict_face.__name__)
        except FaceNotDetectedException:
            traceback.print_exc()
            fl.abort(409, 'Face not detected')


def label_face(db_save_face, image_base64):
    encoding_input = face_encode(
        to_numpy_array(image_base64))
    if encoding_input is None:
        raise FaceNotDetectedException()

    face_id = db_save_face(encoding_input)
    if face_id is None:
        raise FaceNotFoundException()
    return {"faceId": str(face_id)}


def predict_face(image_base64):
    encoding_input = face_encode(
        to_numpy_array(image_base64))
    if encoding_input is None:
        raise FaceNotDetectedException()

    faces = time(db_get_faces)
    if not faces:
        return {"faceId": None}

    face_id = predict(
        encoding_input,
        faces)

    if face_id is None:
        return {"faceId": None}
    return {"faceId": str(face_id)}


def predict(encoding, faces):
    closest_comparison = \
        time_lambda(
            lambda: closest(
                compare(
                    encoding,
                    faces)),
            "closest_comparison")
        
    print(closest_comparison)

    if closest_comparison.euclidean_distance_minimum > 0.5:
        return None
    return closest_comparison.face_id


def closest(comparisons):
    return min(
        comparisons,
        key=lambda comparison: comparison.euclidean_distance_minimum)


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
        for face in faces:
            yield face_comparison(
                face_id=face["_id"],
                euclidean_distance_minimum=np.amin(
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
        matched_count = faces_db() \
            .update_one(
                {'_id': ObjectId(_id)},
                {'$push': {'encodings': encoding.tolist()}}) \
            .matched_count
        global faces_collection
        faces_collection = []
    if matched_count == 0:
        return None
    return _id

def db_delete_face(_id):
    with lock:
        deleted_count = faces_db() \
            .delete_one({'_id': ObjectId(_id)}) \
            .deleted_count
        if deleted_count == 0:
            return None
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
    return mongo_client[database_name]\
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


if __name__ == '__main__':
    # app.run(debug=True, host='0.0.0.0')
    serve(app, host='0.0.0.0', port=5000)
