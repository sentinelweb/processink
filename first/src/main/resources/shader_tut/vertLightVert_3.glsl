uniform mat4 modelview;
uniform mat4 transform;
uniform mat3 normalMatrix;

uniform vec4 lightPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;

varying vec4 vertColor;
// The idea is to interpolate the normal and direction vectors instead of the final color of the vertex, and then
// calculate the intensity value at each fragment by using the normal and direction passed from the vertex shader with
// varying variables.
void main() {
    gl_Position = transform * position;
    vec3 ecPosition = vec3(modelview * position);// eye coordinates
    vec3 ecNormal = normalize(normalMatrix * normal);

    // light direction vector
    vec3 direction = normalize(lightPosition.xyz - ecPosition);
    // dot product is the ration of the light direction to vertex mormal vector
    float intensity = max(0.0, dot(direction, ecNormal));
    vertColor = vec4(intensity, intensity, intensity, 1) * color;
}