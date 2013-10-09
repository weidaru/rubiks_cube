#include "Cubie.h"
#include "globals.h"

#include <cmath>

const char Cubie::xRotationArray[4] = {'F', 'U', 'B', 'D'};
const char Cubie::yRotationArray[4] = {'F', 'L', 'B', 'R'};
const char Cubie::zRotationArray[4] = {'U', 'R', 'D', 'L'};

namespace
{
int isInArray(char c, const char* array, int length)
{
    for(int i = 0; i<length; i++)
    {
        if(c == array[i])
            return i;
    }
    return -1;
}
}

Cubie::Cubie(std::string p, std::string c)
: Position(p), ColorString(c), xrbias(0.0f), yrbias(0.0f), zrbias(0.0f)         //improve! Make all characters in Postion and ColorString capitalized.
{

}

bool Cubie::Rotate(float delta, char axis)
{
    bool changePosition = false;
    float* b = NULL;
    const char* rotationArray = NULL;
    if(axis == 'x' || axis == 'X')
    {
        b = &xrbias;
        rotationArray =xRotationArray;
    }
    else if(axis == 'y' || axis == 'Y')
    {
        b = &yrbias;
        rotationArray =yRotationArray;
    }
    else if(axis == 'z' || axis == 'Z')
    {
        b = &zrbias;
        rotationArray =zRotationArray;
    }
    else
        return false;               //should throw exception!

    float& bias = *b;
    bias += delta;
    if(bias >= 90.0f)
    {
        changePosition = true;
        bias -= 90;
        for(int i = 0; i < (int)Position.size(); i++)
        {
            int p = isInArray(Position[i], rotationArray, 4);
            if(p == -1)
                continue;
            else if(p == 0)
                Position.replace(i, 1, 1, rotationArray[3]);
            else
                Position.replace(i, 1, 1, rotationArray[p-1]);
        }
    }

    if(bias <= -90.0f)
    {
        changePosition = true;
        bias += 90;
        for(int i = 0; i < (int)Position.size(); i++)
        {
            int p = isInArray(Position[i], rotationArray, 4);
            if(p == -1)
                continue;
            else
                Position.replace(i, 1, 1, rotationArray[(p+1)%4]);
        }
    }
    if(bias < 0.2f * RotateSpeed && bias > -0.2f * RotateSpeed)
        bias = 0.0f;
    return changePosition;
}

std::string Cubie::GetPosition() const
{
    return Position;
}

std::string Cubie::GetColorString() const
{
    return ColorString;
}

float Cubie::Getxrbias() const
{
    return xrbias;
}

float Cubie::Getyrbias() const
{
    return yrbias;
}

float Cubie::Getzrbias() const
{
    return zrbias;
}
