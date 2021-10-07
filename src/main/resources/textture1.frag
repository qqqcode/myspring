#version 330 core
out vec4 color;

in vec3 vertexColor;
in vec2 vertexTexture;

uniform sampler2D ourTexture;
uniform sampler2D ourTexture2;

void main()
{
    color = mix(texture(ourTexture, vertexTexture),texture(ourTexture2, vertexTexture),0.2);
}