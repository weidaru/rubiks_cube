#include "globals.h"

namespace
{
    ColorVectorMap InitColorMap()
    {
        ColorVectorMap map;
        map['F'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(1.0f, 0.0f, 0.0f));
        map['B'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(0.0f, 1.0f, 0.0f));
        map['U'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(0.0f, 0.0f, 1.0f));
        map['D'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(1.0f, 1.0f, 0.0f));
        map['L'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(1.0f, 0.0f, 1.0f));
        map['R'] = std::tr1::shared_ptr<ColorVector>(new ColorVector(0.0f, 1.0f, 1.0f));

        return map;
    }
}

ColorVectorMap ColorMap = InitColorMap();
float CameraSpeed = 1.0f;
float RotateSpeed = 1.0f;
