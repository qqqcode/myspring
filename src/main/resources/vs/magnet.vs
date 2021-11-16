/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
#version 330 core

attribute vec4 aVertex;
attribute vec3 aNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

varying vec3 vPosition;
varying vec3 vNormal;

void main() {
    vec4 modelPosition = model * aVertex;
    gl_Position = projection * view *  modelPosition;
    vPosition = modelPosition.xyz;
    vNormal = aNormal;
}
