#include <queue>
#include <string>
#include <vector>
#include <utility>

#include <tr1/memory>

#include <GL/gl.h>

#ifndef RUBIKSCUBE_H
#define RUBIKSCUBE_H

class Cubie;

class RubiksCube
{
public:
    typedef std::tr1::shared_ptr<Cubie> cube_ptr;

public:
    RubiksCube();
    void UpdateAnimation(float delta);
    void Render();
    void AddMoves(std::string moves);

private:
    void RenderCubie(const Cubie& c);
    void FindCubies(std::string s, std::vector<cube_ptr>& result);

private:
    std::vector<cube_ptr > Cubies;
    std::queue<std::string>  MoveQueue;
    GLfloat CubieLength;
    GLfloat CubieGap;
    std::pair<std::string, int> RotatePair;
};

#endif      //RUBIKSCUBE_H
