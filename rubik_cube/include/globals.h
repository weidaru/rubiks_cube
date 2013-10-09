#include <map>
#include <tr1/memory>

#include <GL/gl.h>

#ifndef GLOBALS_H
#define GLOBALS_H

struct ColorVector
{
    ColorVector(GLfloat red, GLfloat green, GLfloat blue)
    : r(red), g(green), b(blue)
    {

    }

    GLfloat r, g, b;
};

typedef std::map<char, std::tr1::shared_ptr<ColorVector> > ColorVectorMap;

extern std::map<char, std::tr1::shared_ptr<ColorVector> > ColorMap;
extern float CameraSpeed;
extern float RotateSpeed;
#endif              //GLOBALS_H
