import cors from 'cors';
import express, { Request, Response } from 'express';
import path from 'path';
import { Person } from './models/server.models';

const app = express();
app.use(cors());

app.use(express.static(path.join(__dirname, 'build')));

app.use(express.json({ limit: '100mb' }));
app.use(
  express.urlencoded({ limit: '100mb', extended: true, parameterLimit: 50000 })
);

const PORT = process.env.PORT || 8000;

const persons: Person[] = [
  {
    name: 'Alexandra Onegård',
    personId: '93b3d0a4-02f9-11eb-adc1-0242ac120002',
  },
  {
    name: 'Jens Nilsson',
    personId: 'a856f0a4-02f9-11eb-adc1-0242ac120002',
  },
];

const random: number = Math.floor(Math.random() * persons.length);

app.post('/predict', (req: Request, res: Response) => {
  const body: string = req.body.image;
  const base64String: string = body.split('base64,')[1];

  return res.send({
    name: persons[random].name,
    isKnownFace: Math.random() >= 0.5,
    isFace: true, //Math.random() >= 0.1,
    id: persons[random].personId,
  });
});

app.post('/register', (req: Request, res: Response) => {
  console.log(req.body);

  return res.sendStatus(200);
});

app.delete('/delete/:id', (req: Request, res: Response) => {
  const id = req.params.id;
  console.log('id: ', id);

  return res.sendStatus(200);
});

app.listen(PORT, () => {
  console.log(`App listening at port: ${PORT}`);
});
