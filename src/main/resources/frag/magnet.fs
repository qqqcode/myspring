/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
#version 330 core

uniform vec3 uLightPosition;
uniform vec3 uViewPosition;

struct Light {
    vec3 uAmbientColor;//环境光颜色
    vec3 uDiffuseColor;//漫反射颜色
    vec3 uSpecularColor;//高光颜色
};

varying vec3 vPosition;
varying vec3 vNormal;
uniform Light light;

void main() {
    float ambientStrength = 0.5;
    float diffuseStrength = 1.0;
    float specularStrength = 0.5;
    float shininess = 4.0;
    //ambient
    vec3 ambientColor = ambientStrength * light.uAmbientColor;
    //diffuse
    vec3 normal = normalize(vNormal);
    vec3 lightDirection = normalize(uLightPosition - vPosition);
    float diff =  max(0.0, dot(normal, lightDirection));
    vec3 diffuseColor = diffuseStrength * diff * light.uDiffuseColor;
    //specular
    vec3 viewDirection = normalize(uViewPosition - vPosition);
    vec3 reflectDirection = reflect(-lightDirection, normal);
    float spec = pow(max(dot(viewDirection, reflectDirection), 0.0), shininess);
    vec3 specularColor = light.uSpecularColor * spec * specularStrength;

    gl_FragColor = vec4(ambientColor + diffuseColor + specularColor, 1.0);
}
