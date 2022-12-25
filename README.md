# Doodle Classifier
Author: Harry He

## Project Description

https://user-images.githubusercontent.com/69978107/209475129-1cee21a9-a66a-4bd3-9155-d61b7ef80ecd.mp4

This application is a use of the Template Neural Network library for classifying different types of doodles in a 280 by 280 pixel square. The application can classify 3 types of drawings: airplanes, cakes, and computers. The application can support the training the neural network with specific numbers of epochs, testing, and guessing user drawn images as well as saving and loading previously trained neural networks from the hard drive.

## Implementation Details

The application uses a depth 2 neural network to guess the doodle. Neural networks are trained using a primitive training technique of backpropagation using the CPU. Data used for this program can be found at https://github.com/googlecreativelab/quickdraw-dataset.

## License

MIT License

Copyright (c) 2022 Harry He

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
