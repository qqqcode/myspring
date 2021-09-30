#version 330 core
out vec4 color;

in vec3 vertexColor;
in vec2 vertexTexture;

uniform sampler2D ourTexture;

void main()
{
    color = texture(ourTexture, vertexTexture);
}