#include <iostream>
#include <assert.h>

#include "Glee.h"

#include <GL/gl.h>
#include <GL/glu.h>

#include <SFML/Graphics.hpp>

#include "RubiksCube.h"
#include "globals.h"

using namespace std;

void OpenGLInit();
void SetupProjection(GLfloat width, GLfloat height);
void Update(float delta);
void OpenglRender();
void HandleEvent(sf::Event& Event);

sf::Window App(sf::VideoMode(800, 600, 32), "Rubik's cube with SFML & OpenGL.");
float UpdateInterval = 1.0f/100;
RubiksCube Cube;
float eyexr = 30.0f, eyeyr = -40.0f, eyezr = 0.0f;

int main()
{
    OpenGLInit();
    SetupProjection(App.GetWidth(), App.GetHeight());

    sf::Clock Clock;

    float UpdateNext = Clock.GetElapsedTime() + UpdateInterval;
    while(App.IsOpened())
    {
        sf::Event Event;

        do
        {
            while(App.GetEvent(Event))
            {
                HandleEvent(Event);
            }
            Update(UpdateInterval);
            UpdateNext += UpdateInterval;
        }while(Clock.GetElapsedTime() > UpdateNext);

        App.SetActive();

        OpenglRender();

        App.Display();
    }

    return EXIT_SUCCESS;
}

void OpenGLInit()
{
    assert(GLeeInit());

    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClearDepth(1.0f);

    glEnable(GL_DEPTH_TEST);
    glDepthMask(true);
    glShadeModel(GL_FLAT);

    glFrontFace(GL_CCW);
    glCullFace(GL_BACK);
    glEnable(GL_CULL_FACE);
}

void SetupProjection(GLfloat width, GLfloat height)
{
    float aspectRatio = width / height;
    glViewport(0.0f, 0.0f, width, height);

    //perspective projection
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(90.f, aspectRatio, 1.0f, 300.0f);
    //glOrtho(-35.0f * aspectRatio, 35.0f * aspectRatio, -35.0f, 35.0f, 1.0f, 300.0f);
    glMatrixMode(GL_MODELVIEW);
}

void Update(float delta)
{
    Cube.UpdateAnimation(delta);
}

void OpenglRender()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glTranslatef(0.0f, 0.0f, -50.0f);
    glRotatef(eyexr, 1.0f, 0.0f, 0.0f);
    glRotatef(eyeyr, 0.0f, 1.0f, 0.0f);
    glRotatef(eyezr, 0.0f, 0.0f, 1.0f);
    Cube.Render();
}

void HandleEvent(sf::Event& Event)
{
    static int previousX = 0;
    static int previousY = 0;
    static bool mousePressed = false;
    switch(Event.Type)
    {
        case sf::Event::Closed:
            App.Close();
            break;
        case sf::Event::MouseButtonPressed:
            if(Event.MouseButton.Button == sf::Mouse::Right)
            {
                mousePressed = true;
                previousX = (float)Event.MouseButton.X;
                previousY = (float)Event.MouseButton.Y;
            }
            break;
        case sf::Event::MouseButtonReleased:
            mousePressed = false;
            break;
        case sf::Event::MouseMoved:
            if(mousePressed == true)
            {
                eyeyr += float(Event.MouseMove.X - previousX) * CameraSpeed * 0.6;
                eyexr += float(Event.MouseMove.Y - previousY) * CameraSpeed * 0.6;
                previousX = Event.MouseMove.X;
                previousY = Event.MouseMove.Y;
            }
            break;
        default:
            break;
    }
}
