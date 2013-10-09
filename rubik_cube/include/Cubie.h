#include <string>

#ifndef CUBIE_H
#define CUBIE_H

class Cubie
{
public:
    Cubie(std::string Position, std::string ColorString);
    bool Rotate(float delta, char axis);
    std::string GetPosition() const;
    std::string GetColorString() const;
    float Getxrbias() const;
    float Getyrbias() const;
    float Getzrbias() const;

private:
    std::string Position;
    std::string ColorString;

    const static char xRotationArray[4];
    const static char yRotationArray[4];
    const static char zRotationArray[4];

private:
    float xrbias, yrbias, zrbias;
};

#endif          //CUBIE_H
