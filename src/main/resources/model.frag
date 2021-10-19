#version 330

uniform vec3 uAmbientColor;
uniform vec3 uDiffuseColor;
uniform vec3 uSpecularColor;


void main() {
    float ambientStrength = 0.5;
    float diffuseStrength = 0.5;
    float specularStrength = 0.5;
    float shininess = 4.0;
    vec3 ambientColor = ambientStrength * uAmbientColor;
    vec3 diffuseColor = diffuseStrength * uDiffuseColor;
    vec3 specularColor = specularStrength * uSpecularColor;
    gl_FragColor = vec4(ambientColor + diffuseColor + specularColor, 1.0);
}