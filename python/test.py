import numpy as np
from flask import Flask
from flask import request
import io
import base64
from PIL import Image
import face_recognition


niklas_face_encoding = np.array([
    -4.64915745e-02,  5.08996844e-02, -1.46287978e-02, -1.77614931e-02,
    -9.24152210e-02,  2.55672429e-02, -1.82103962e-02, -1.39756888e-01,
    1.65140599e-01, -1.89345330e-04, 2.63896406e-01, -1.33701107e-02,
    -1.37185261e-01, -4.80646715e-02, -5.03800530e-03,  4.69740555e-02,
    -9.20617431e-02, -1.13354437e-01, -7.47346506e-02, -1.23340804e-02,
    2.43818164e-02, -6.31772587e-03, 4.92535718e-02, 6.63432628e-02,
    -1.46434173e-01, -3.51101011e-01, -9.44994912e-02, -2.09628731e-01,
    1.50169339e-02, -1.55001700e-01, 2.96557993e-02, 5.38103022e-02,
    -2.20464528e-01, -5.10398559e-02, -3.32146101e-02, -6.47161007e-02,
    -1.43318102e-01, -3.05716451e-02,  2.82144785e-01,  7.52678216e-02,
    -1.30558044e-01,  6.18303120e-02, -5.37175126e-02,  2.98958689e-01,
    1.86518982e-01, 1.75630804e-02, 8.39207172e-02, -6.66824505e-02,
    1.13536008e-01, -2.53243774e-01, 1.48968071e-01, 1.35271281e-01,
    1.51800826e-01, 2.40050871e-02, 1.21078245e-01, -1.16233699e-01,
    -5.20920269e-02,  1.07264355e-01, -1.91047683e-01,  2.11537182e-01,
    3.52515057e-02, -3.23438644e-03, -2.36552600e-02, -9.86030474e-02,
    2.36462578e-01, 1.18139468e-01, -1.01077810e-01, -5.20034358e-02,
    1.15233399e-01, -9.35867801e-02, -3.45191285e-02, 1.06335416e-01,
    -1.53175727e-01, -2.81644136e-01, -2.88389236e-01,  1.10829592e-01,
    4.14135247e-01, 1.01869680e-01, -2.55774677e-01, -6.96565285e-02,
    -7.77942687e-02, -3.38964760e-02,  7.63371438e-02,  7.23587796e-02,
    -6.95834681e-03, -1.21964671e-01, -5.61342910e-02, -2.47463025e-03,
    1.95907816e-01, -4.34143022e-02, -7.47753233e-02, 2.51570642e-01,
    -4.97937277e-02, -2.83004995e-02, -2.38421373e-02,  5.13103902e-02,
    -7.49206245e-02, -1.19004808e-02, -4.23495173e-02,  3.09424251e-02,
    -6.30991235e-02, -2.88180411e-02, -4.49266694e-02,  1.12247482e-01,
    -1.49153113e-01,  1.25262424e-01, -2.76796408e-02, -4.44860011e-02,
    -3.80569398e-02, -2.10323483e-02, -1.12629138e-01, -8.28666389e-02,
    1.28158987e-01, -3.19296151e-01, 1.88993707e-01, 1.19367734e-01,
    4.85410215e-04, 1.29757598e-01, -5.99319302e-02, 4.86202762e-02,
    -1.57061946e-02,  4.54311147e-02, -1.93910718e-01, -1.75432023e-02,
    6.41586185e-02, 4.84591201e-02, -6.96861967e-02, 9.24183056e-03])

app = Flask(__name__)


@app.route('/predict', methods=['POST'])
def predict():
    req = request.get_json()

    image = face_recognition.load_image_file(
        io.BytesIO(base64.b64decode(req['image'])))
    face_encoding = face_recognition.face_encodings(image)[0]
    results = face_recognition.face_distance(
        [niklas_face_encoding], face_encoding)

    return f"{round(results[0], 2)}"


"""
# Load the jpg file into a numpy array

image = face_recognition.load_image_file(
    io.BytesIO(base64.b64decode(base64_image)))
#image = face_recognition.load_image_file("nf1.jpg")

# Find all the faces in the image using the default HOG-based model.
# This method is fairly accurate, but not as accurate as the CNN model and not GPU accelerated.
# See also: find_faces_in_picture_cnn.py
face_locations = face_recognition.face_locations(image)

print("I found {} face(s) in this photograph.".format(len(face_locations)))

for face_location in face_locations:

    # Print the location of each face in this image
    top, right, bottom, left = face_location
    print("A face is located at pixel location Top: {}, Left: {}, Bottom: {}, Right: {}".format(
        top, left, bottom, right))

    # You can access the actual face itself like this:
    face_image = image[top:bottom, left:right]
    pil_image = Image.fromarray(face_image)
    pil_image.show()
"""
