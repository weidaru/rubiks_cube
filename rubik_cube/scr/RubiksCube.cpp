#include "RubiksCube.h"

#include "Cubie.h"

#include <iostream>
#include <cmath>

#include <GL/gl.h>
#include <GL/glu.h>

#include "globals.h"

namespace
{

    void MeasurePositionBias(const std::string position, GLfloat* result)
    {
        size_t p = position.find('L');
        size_t q = position.find('R');
        if(p != std::string::npos)
            result[0] = -1.5f;
        else if(q != std::string::npos)
            result[0] = 0.5f;
        else if(p == std::string::npos && q == std::string::npos)
            result[0] = -0.5f;

        p = position.find('D');
        q = position.find('U');
        if(p != std::string::npos)
            result[1] = -1.5f;
        else if(q != std::string::npos)
            result[1] = 0.5f;
        else if(p == std::string::npos && q == std::string::npos)
            result[1] = -0.5f;

        p = position.find('F');
        q = position.find('B');
        if(p != std::string::npos)
            result[2] = 1.5f;
        else if(q != std::string::npos)
            result[2] = -0.5f;
        else if(p == std::string::npos && q == std::string::npos)
            result[2] = 0.5f;
    }
}

RubiksCube::RubiksCube() : CubieLength(10.0f), CubieGap(0.5f), RotatePair("", 0)
{
    std::string CubieName[26] = {"FLU", "FU", "FUR", "FL", "F", "FR", "FDL", "FD", "FRD",
                                                     "UL", "U", "UR", "R", "RD", "D", "DL", "L",
                                                     "BLU", "BU", "BUR", "BL", "B", "BR", "BDL", "BD", "BRD", };

    for(int i = 0; i<26; i++)
        Cubies.push_back(std::tr1::shared_ptr<Cubie>(new Cubie(CubieName[i], CubieName[i])));
}

void RubiksCube::UpdateAnimation(float delta)
{
    if(!MoveQueue.empty() && RotatePair.second == 0)
    {
        std::string s = MoveQueue.front();
        MoveQueue.pop();

        RotatePair.first = s;
        if((int)s.size() > 1)
        {
            switch(s[(int)s.size()-1])
            {
                case '-':
                case '3':
                    RotatePair.second = -1;
                    break;
                case '2':
                    RotatePair.second = 2;
                    break;
                default:
                    break;
            }
        }
        else
            RotatePair.second = 1;
    }

    if(RotatePair.second == 0)
    {
        return;
    }
    std::vector<cube_ptr> rotateFaces;
    float deltaR = 0.0f;
    bool rotateFlag = false;
    if(RotatePair.first == "F")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(-deltaR, 'Z');
    }
    else if(RotatePair.first == "B")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(deltaR, 'Z');
    }
    else if(RotatePair.first == "L")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(deltaR, 'X');
    }
    else if(RotatePair.first == "R")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(-deltaR, 'X');
    }
    else if(RotatePair.first == "U")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(-deltaR, 'Y');
    }
    else if(RotatePair.first == "D")
    {
        FindCubies(RotatePair.first, rotateFaces);
        deltaR = 0.2f* RotateSpeed;
        if(RotatePair.second < 0)
            deltaR *= -1.0f;
        for(int i = 0; i<(int)rotateFaces.size(); i++)
            rotateFlag = rotateFaces[i]->Rotate(deltaR, 'Y');
    }
    if(rotateFlag == true)
        RotatePair.second -= (int)(deltaR/std::abs(deltaR));

}

void RubiksCube::Render()
{
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
    for(int i = 0; i < (int)Cubies.size(); i++)
    {
        RenderCubie(*Cubies[i]);
    }
    glPopMatrix();
}

void RubiksCube::AddMoves(std::string moves)
{
    int i = 0;
    std::string LegalMoveString = "FBLRUD123+-^";
    std::string LegalMoveSuffix = "+-123^";
    while(i < (int)moves.size())
    {
        if(LegalMoveString.find(moves[i]) == std::string::npos)
        {
            std::cout<<"Bad Move String!"<<std::endl;
            return;
        }

        std::string token = moves.substr(i,1);
        if(i != (int)moves.size()-1)
        {
            char nc = moves[i+1];
            while(LegalMoveSuffix.find(nc) != std::string::npos)
            {
                token.push_back(nc);
                i++;
            }
        }
        MoveQueue.push(token);
        i++;
    }
}

void RubiksCube::RenderCubie(const Cubie& c)
{
    GLfloat pBias[3];
    MeasurePositionBias(c.GetPosition(), pBias);

    GLfloat Colors[6][3];       //instead of "F B L R U D"       Replace color value with global
    std::string sColor = c.GetColorString();
    std::string sPosition = c.GetPosition();
    static std::string cArray = "FBLRUD";
    for(int i =  0; i<6; i++)
    {
        Colors[i][0] = 0.5f;
        Colors[i][1] = 0.5f;
        Colors[i][2] = 0.5f;
    }
    for(int i = 0; i<(int)sPosition.size(); i++)
    {
        int n = (int)cArray.find(sPosition[i]);
        Colors[n][0] = ColorMap[sColor[i]]->r;
        Colors[n][1] = ColorMap[sColor[i]]->g;
        Colors[n][2] = ColorMap[sColor[i]]->b;
    }

    glShadeModel(GL_FLAT);
    glMatrixMode(GL_MODELVIEW);
    glPushMatrix();
        //std::cout<<c.Getxrbias()<<'\t'<<c.Getyrbias()<<'\t'<<c.Getzrbias()<<std::endl;
        glRotatef(c.Getxrbias(), 1.0f, 0.0f ,0.0f);
        glRotatef(c.Getyrbias(), 0.0f, 1.0f ,0.0f);
        glRotatef(c.Getzrbias(), 0.0f, 0.0f ,1.0f);
        glTranslatef(pBias[0]*CubieLength, pBias[1]*CubieLength, pBias[2]*CubieLength);
        glTranslatef((pBias[0] + 0.5f)*CubieGap,
                            (pBias[1] + 0.5f)*CubieGap,
                            (pBias[2] - 0.5f)*CubieGap);

        glBegin(GL_QUADS);
            //Front face
            glColor3fv(Colors[0]);
            glVertex3f(0.0f, CubieLength, 0.0f);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(CubieLength, 0.0f, 0.0f);
            glVertex3f(CubieLength, CubieLength, 0.0f);
            //Right face
            glColor3fv(Colors[3]);
            glVertex3f(CubieLength, CubieLength, 0.0f);
            glVertex3f(CubieLength, 0.0f, 0.0f);
            glVertex3f(CubieLength, 0.0f, -CubieLength);
            glVertex3f(CubieLength, CubieLength, -CubieLength);
            //Back face
            glColor3fv(Colors[1]);
            glVertex3f(CubieLength, CubieLength, -CubieLength);
            glVertex3f(CubieLength, 0.0f, -CubieLength);
            glVertex3f(0.0f, 0.0f, -CubieLength);
            glVertex3f(0.0f, CubieLength, -CubieLength);
            //Left face
            glColor3fv(Colors[2]);
            glVertex3f(0.0f, CubieLength, -CubieLength);
            glVertex3f(0.0f, 0.0f, -CubieLength);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, CubieLength, 0.0f);
            //Up face
            glColor3fv(Colors[4]);
            glVertex3f(0.0f, CubieLength, 0.0f);
            glVertex3f(CubieLength, CubieLength, 0.0f);
            glVertex3f(CubieLength, CubieLength, -CubieLength);
            glVertex3f(0.0f, CubieLength, -CubieLength);
            //Down face;
            glColor3fv(Colors[5]);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, -CubieLength);
            glVertex3f(CubieLength, 0.0f, -CubieLength);
            glVertex3f(CubieLength, 0.0f, 0.0f);
        glEnd();

    glPopMatrix();
}

void RubiksCube::FindCubies(std::string s, std::vector<cube_ptr>& result)
{
    for(int i=0; i<(int)Cubies.size(); i++)
    {
        std::string cstr = Cubies[i]->GetPosition();
        bool inResult = true;
        for(int j=0; j<(int)s.size(); j++)
        {
            size_t p = cstr.find(s[j]);
            if(p == std::string::npos)
            {
                inResult  = false;
                break;
            }
        }
        if(inResult == true)
            result.push_back(Cubies[i]);
    }
}
